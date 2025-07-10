package com.umc.pyeongsaeng.domain.auth.service;

import java.time.Duration;
import java.util.Random;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

	private static final String SMS_PREFIX = "sms:";
	private static final int VERIFICATION_CODE_LENGTH = 6;
	private static final int EXPIRY_MINUTES = 5;
	private static final int MAX_CODE_VALUE = 1000000;

	private final RedisTemplate<String, Object> redisTemplate;

	public void sendVerificationCode(String phone) {
		String code = generateVerificationCode();
		String redisKey = SMS_PREFIX + phone;

		redisTemplate.opsForValue().set(redisKey, code, Duration.ofMinutes(EXPIRY_MINUTES));

		log.info("SMS 발송 - 전화번호: {}, 인증번호: {}", phone, code);
	}

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

	private String generateVerificationCode() {
		Random random = new Random();
		return String.format("%0" + VERIFICATION_CODE_LENGTH + "d",
			random.nextInt(MAX_CODE_VALUE));
	}
}
