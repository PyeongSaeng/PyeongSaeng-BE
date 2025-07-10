package com.umc.pyeongsaeng.global.apiPayload.code.status;

import org.springframework.http.HttpStatus;

import com.umc.pyeongsaeng.global.apiPayload.code.BaseCode;
import com.umc.pyeongsaeng.global.apiPayload.code.ReasonDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

	// 일반적인 응답
	_OK(HttpStatus.OK, "COMMON200", "성공입니다."),

    // Auth
    LOGOUT_SUCCESS(HttpStatus.OK, "AUTH200", "로그아웃되었습니다."),
	USERNAME_AVAILABLE(HttpStatus.OK, "AUTH201", "사용 가능한 아이디입니다."),

    // Sms
    SMS_SENT(HttpStatus.OK, "SMS200", "인증번호가 발송되었습니다."),
	SMS_VERIFIED(HttpStatus.OK, "SMS201","SMS 인증이 성공적으로 완료되었습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public ReasonDTO getReason() {
		return ReasonDTO.builder()
			.message(message)
			.code(code)
			.isSuccess(true)
			.build();
	}

	@Override
	public ReasonDTO getReasonHttpStatus() {
		return ReasonDTO.builder()
			.message(message)
			.code(code)
			.isSuccess(true)
			.httpStatus(httpStatus)
			.build()
			;
	}
}
