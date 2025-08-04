package com.umc.pyeongsaeng.domain.sms.service;

import com.umc.pyeongsaeng.domain.sms.dto.*;

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

	/**
	 * 계정 인증을 위한 SMS 인증번호 발송
	 * @param phone 대상 전화번호
	 * @return 발송 결과를 담은 객체
	 */
	SmsResponse.SmsResultDto sendAccountVerificationCode(String phone);
}
