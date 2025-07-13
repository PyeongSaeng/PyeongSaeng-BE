package com.umc.pyeongsaeng.domain.sms.service;

public interface SmsService {

	/**
	 * 인증번호 발송 (Redis에 저장)
	 * @param phone 전화번호
	 */
	void sendVerificationCode(String phone);

	/**
	 * 인증번호 검증
	 * @param phone 전화번호
	 * @param code 입력한 인증번호
	 */
	void verifyCode(String phone, String code);
}
