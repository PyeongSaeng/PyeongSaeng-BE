package com.umc.pyeongsaeng.domain.sms.service;

import java.time.Duration;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;

import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

	private static final String SMS_PREFIX = "sms:";
	private static final int VERIFICATION_CODE_LENGTH = 6;
	private static final int EXPIRY_MINUTES = 5;

	private final RedisTemplate<String, Object> redisTemplate;

	@Value("${coolsms.api-key}")
	private String apiKey;

	@Value("${coolsms.api-secret}")
	private String apiSecret;

	@Value("${coolsms.from-number}")
	private String fromNumber;

	// 인증 코드 저장 후 발송
	@Override
	public void sendVerificationCode(String phone) {
		String code = generateVerificationCode();
		String redisKey = SMS_PREFIX + phone;

		redisTemplate.opsForValue().set(redisKey, code, Duration.ofMinutes(EXPIRY_MINUTES));

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
		DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");

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
}
