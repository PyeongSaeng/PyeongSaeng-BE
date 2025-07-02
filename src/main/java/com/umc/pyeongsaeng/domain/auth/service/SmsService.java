package com.umc.pyeongsaeng.domain.auth.service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SmsService {

	private final ConcurrentHashMap<String, VerificationCode> verificationCodes = new ConcurrentHashMap<>();

	private static class VerificationCode {
		private final String code;
		private final LocalDateTime expiredAt;

		public VerificationCode(String code, LocalDateTime expiredAt) {
			this.code = code;
			this.expiredAt = expiredAt;
		}

		public boolean isExpired() {
			return LocalDateTime.now().isAfter(expiredAt);
		}

		public boolean matches(String inputCode) {
			return code.equals(inputCode);
		}
	}

	public void sendVerificationCode(String phone) {
		String code = generateVerificationCode();
		LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(5);

		verificationCodes.put(phone, new VerificationCode(code, expiredAt));

		log.info("SMS 발송 - 전화번호: {}, 인증번호: {}", phone, code);

	}

	public boolean verifyCode(String phone, String code) {
		VerificationCode storedCode = verificationCodes.get(phone);

		if (storedCode == null) {
			return false;
		}

		if (storedCode.isExpired()) {
			verificationCodes.remove(phone);
			return false;
		}

		if (storedCode.matches(code)) {
			verificationCodes.remove(phone);
			return true;
		}

		return false;
	}

	private String generateVerificationCode() {
		Random random = new Random();
		return String.format("%06d", random.nextInt(1000000));
	}
}
