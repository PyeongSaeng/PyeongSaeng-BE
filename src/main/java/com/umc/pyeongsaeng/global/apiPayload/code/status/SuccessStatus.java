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
	CREATED(HttpStatus.CREATED, "COMMON201", "성공적으로 생성되었습니다."),
	ACCEPTED(HttpStatus.ACCEPTED, "COMMON202", "요청이 접수되었습니다."),
	NO_CONTENT(HttpStatus.NO_CONTENT, "COMMON203", "성공적으로 처리되었습니다."),

	// Auth
	LOGOUT_SUCCESS(HttpStatus.OK, "AUTH201", "로그아웃되었습니다."),
	USERNAME_AVAILABLE(HttpStatus.OK, "AUTH202", "사용 가능한 아이디입니다."),

	// SMS
	SMS_SENT(HttpStatus.OK, "SMS201", "인증번호가 발송되었습니다."),
	SMS_VERIFIED(HttpStatus.OK, "SMS202", "SMS 인증이 성공적으로 완료되었습니다."),

	// User
	WITHDRAW_SUCCESS(HttpStatus.OK, "USER201", "회원 탈퇴가 완료되었습니다. 7일 이내에 복구 가능합니다.");


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
