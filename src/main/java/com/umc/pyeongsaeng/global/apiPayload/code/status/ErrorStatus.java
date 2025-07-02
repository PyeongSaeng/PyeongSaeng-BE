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


    // Member
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "닉네임은 필수 입니다."),

	// Auth
	INVALID_KAKAO_ID(HttpStatus.BAD_REQUEST, "AUTH4001", "유효한 카카오 ID가 필요합니다."),
	KAKAO_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "AUTH4002", "이미 가입된 카카오 계정입니다."),
	USERNAME_DUPLICATED(HttpStatus.BAD_REQUEST, "AUTH4003", "이미 사용 중인 아이디입니다."),
	SMS_SEND_FAILED(HttpStatus.BAD_REQUEST, "AUTH4004", "SMS 발송에 실패했습니다."),
	SMS_VERIFICATION_FAILED(HttpStatus.BAD_REQUEST, "AUTH4005", "SMS 인증에 실패했습니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "AUTH4006", "유효하지 않은 리프레시 토큰입니다."),
	EXPIRED_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "AUTH4007", "만료된 리프레시 토큰입니다."),
	INVALID_TEMP_TOKEN(HttpStatus.BAD_REQUEST, "AUTH4008", "유효하지 않은 임시 토큰입니다."),

	// Protector
	PROTECTOR_ONLY_ACCESS(HttpStatus.FORBIDDEN, "PROTECTOR4001", "보호자만 시니어를 추가할 수 있습니다."),
	PROTECTOR_SENIOR_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "PROTECTOR4002", "보호자는 최대 2명의 시니어만 추가할 수 있습니다.");

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
