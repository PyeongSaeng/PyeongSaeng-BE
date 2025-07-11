package com.umc.pyeongsaeng.global.apiPayload.code.status;

import org.springframework.http.HttpStatus;

import com.umc.pyeongsaeng.global.apiPayload.code.BaseErrorCode;
import com.umc.pyeongsaeng.global.apiPayload.code.ErrorReasonDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // User
	PHONE_DUPLICATED(HttpStatus.BAD_REQUEST, "USER4001", "이미 사용중인 전화번호입니다."),
	PROTECTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4002", "존재하지 않는 보호자입니다."),
	INVALID_PROTECTOR_ROLE(HttpStatus.BAD_REQUEST, "USER4003", "보호자 권한이 없는 사용자입니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4004", "존재하지 않는 회원입니다."),

	// Auth
	INVALID_KAKAO_ID(HttpStatus.BAD_REQUEST, "AUTH4001", "유효하지 않은 카카오 ID입니다."),
	KAKAO_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "AUTH4002", "이미 등록된 카카오 계정입니다."),
	INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "AUTH4003", "비밀번호는 필수 입력값입니다."),
	USERNAME_DUPLICATED(HttpStatus.BAD_REQUEST, "AUTH4004", "이미 사용중인 아이디입니다."),
	PROTECTOR_SENIOR_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "AUTH4005", "보호자는 최대 3명의 시니어만 등록할 수 있습니다."),
	LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH4006", "아이디 또는 비밀번호가 올바르지 않습니다."),

	// Token
	INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN4001", "유효하지 않은 리프레시 토큰입니다."),
	EXPIRED_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN4002", "만료된 리프레시 토큰입니다."),
	INVALID_AUTH_CODE(HttpStatus.BAD_REQUEST, "TOKEN4003", "유효하지 않은 인증 코드입니다."),

	// Sms
	SMS_VERIFICATION_FAILED(HttpStatus.BAD_REQUEST, "SMS4001", "SMS 인증에 실패했습니다. 인증번호를 다시 확인하거나 재발송 해주세요.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
