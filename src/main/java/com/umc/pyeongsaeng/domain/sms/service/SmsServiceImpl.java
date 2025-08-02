package com.umc.pyeongsaeng.domain.sms.service;

import java.time.*;
import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.*;

import net.nurigo.sdk.*;
import net.nurigo.sdk.message.model.*;
import net.nurigo.sdk.message.request.*;
import net.nurigo.sdk.message.service.*;

import com.umc.pyeongsaeng.domain.sms.dto.*;
import com.umc.pyeongsaeng.domain.user.entity.*;
import com.umc.pyeongsaeng.domain.user.repository.*;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.*;
import com.umc.pyeongsaeng.global.apiPayload.code.status.*;

import jakarta.annotation.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsServiceImpl implements SmsService {

	private static final String SMS_PREFIX = "sms:";
	private static final String SMS_COUNT_PREFIX = "sms:count:";
	private static final int VERIFICATION_CODE_LENGTH = 6;
	private static final int EXPIRY_MINUTES = 5;
	private static final int MAX_SMS_PER_DAY = 20;
	private final UserRepository userRepository;
	private final SocialAccountRepository socialAccountRepository;

	private final RedisTemplate<String, Object> redisTemplate;

	@Value("${coolsms.api-key}")
	private String apiKey;

	@Value("${coolsms.api-secret}")
	private String apiSecret;

	@Value("${coolsms.from-number}")
	private String fromNumber;

	private DefaultMessageService messageService;

	@PostConstruct
	private void initCoolSmsService() {
		this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
	}

	// 인증 코드 저장 후 발송
	@Override
	public void sendVerificationCode(String phone) {
		String countKey = SMS_COUNT_PREFIX + phone;
		Integer count = (Integer) redisTemplate.opsForValue().get(countKey);

		if (count == null) {
			redisTemplate.opsForValue().set(countKey, 1, Duration.ofHours(24));
		}

		if (count != null && count >= MAX_SMS_PER_DAY) {
			throw new GeneralException(ErrorStatus.SMS_RESEND_LIMIT_EXCEEDED);
		}

		if (count != null && count < MAX_SMS_PER_DAY) {
			redisTemplate.opsForValue().increment(countKey);
		}

		String code = generateVerificationCode();
		String redisKey = SMS_PREFIX + phone;

		redisTemplate.opsForValue().set(redisKey, code, Duration.ofMinutes(EXPIRY_MINUTES));
		log.info("[SMS] 전화번호: {}, 인증번호: {}", phone, code);

		sendSms(phone, code);
	}

	// 입력 코드 검증
	@Override
	public void verifyCode(String phone, String code) {
		String redisKey = SMS_PREFIX + phone;
		String storedCode = (String) redisTemplate.opsForValue().get(redisKey);

		if (storedCode == null) {
			throw new GeneralException(ErrorStatus.SMS_VERIFICATION_FAILED);
		}

		if (!storedCode.equals(code)) {
			throw new GeneralException(ErrorStatus.SMS_VERIFICATION_FAILED);
		}

		redisTemplate.delete(redisKey);
	}

	// 랜덤 코드 생성 6자리
	private String generateVerificationCode() {
		Random random = new Random();
		StringBuilder code = new StringBuilder();

		for (int i = 0; i < VERIFICATION_CODE_LENGTH; i++) {
			code.append(random.nextInt(10));
		}

		return code.toString();
	}

	// CoolSMS API를 사용하여 실제 sms 전송
	private void sendSms(String phoneNumber, String verificationCode) {

		Message message = new Message();
		message.setFrom(fromNumber);
		message.setTo(phoneNumber);
		message.setText("[평생] 인증번호 [" + verificationCode + "]를 입력해주세요. 5분 유효합니다.");

		try {
			messageService.sendOne(new SingleMessageSendingRequest(message));
		} catch (Exception e) {
			throw new GeneralException(ErrorStatus.SMS_SEND_FAILED);
		}
	}

	@Override
	public SmsResponse.SmsResultDto sendAccountVerificationCode(String phone) {
		Optional<User> userOpt = userRepository.findByPhone(phone);

		if (userOpt.isPresent()) {
			User user = userOpt.get();
			boolean isKakaoUser = user.getSocialAccounts().stream()
				.anyMatch(sa -> "KAKAO".equals(sa.getProviderType()));

			if (isKakaoUser) {
				throw new GeneralException(ErrorStatus.KAKAO_USER_FIND_NOT_ALLOWED);
			}
		}

		sendVerificationCode(phone);

		return SmsResponse.SmsResultDto.success();
	}
}
