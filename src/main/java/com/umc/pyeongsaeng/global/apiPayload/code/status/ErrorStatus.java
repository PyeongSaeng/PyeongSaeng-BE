package com.umc.pyeongsaeng.global.apiPayload.code.status;

import co.elastic.clients.elasticsearch.nodes.Http;
import org.springframework.http.HttpStatus;

import com.umc.pyeongsaeng.global.apiPayload.code.BaseErrorCode;
import com.umc.pyeongsaeng.global.apiPayload.code.ErrorReasonDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

	// 일반적인 응답
	_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
	_BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
	_UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
	_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

	// User
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER401", "존재하지 않는 회원입니다."),
	PHONE_DUPLICATED(HttpStatus.BAD_REQUEST, "USER402", "이미 사용중인 전화번호입니다."),
	PROTECTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "USER403", "존재하지 않는 보호자입니다."),
	INVALID_PROTECTOR_ROLE(HttpStatus.BAD_REQUEST, "USER404", "보호자 권한이 없는 사용자입니다."),
	PROTECTOR_SENIOR_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "USER405", "보호자는 최대 3명의 시니어만 등록할 수 있습니다."),
	SENIOR_NOT_FOUND(HttpStatus.NOT_FOUND, "USER406", "해당 시니어를 찾을 수 없습니다."),
	ALREADY_WITHDRAWN_USER(HttpStatus.BAD_REQUEST, "USER407", "이미 탈퇴한 회원입니다."),
	NOT_WITHDRAWN_USER(HttpStatus.BAD_REQUEST, "USER408", "탈퇴하지 않은 회원입니다."),
	WITHDRAWAL_PERIOD_EXPIRED(HttpStatus.BAD_REQUEST, "USER409", "탈퇴 후 7일이 경과하여 복구할 수 없습니다."),
	USER_WITHDRAWAL_NOT_CONFIRMED(HttpStatus.BAD_REQUEST, "USER410", "탈퇴 의도가 확인되지 않았습니다."),
	SENIOR_PROFILE_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER411", "유효하지 않은 시니어 프로필입니다."),
	INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "USER412", "유효하지 않은 Role입니다."),

	// Auth
	LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH401", "아이디 또는 비밀번호가 올바르지 않습니다."),
	USERNAME_DUPLICATED(HttpStatus.BAD_REQUEST, "AUTH402", "이미 사용중인 아이디입니다."),
	INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "AUTH403", "비밀번호가 유효하지 않습니다."),
	INVALID_KAKAO_ID(HttpStatus.BAD_REQUEST, "AUTH404", "유효하지 않은 카카오 ID입니다."),
	KAKAO_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "AUTH405", "이미 등록된 카카오 계정입니다."),
	INVALID_AUTH_TOKEN(HttpStatus.BAD_REQUEST, "AUTH406", "인증 정보의 주체가 유효하지 않습니다."),
	INVALID_USER_TYPE(HttpStatus.BAD_REQUEST, "AUTH407", "User 계정이 아닙니다."),
	INVALID_COMPANY_TYPE(HttpStatus.BAD_REQUEST, "AUTH408", "Company 계정이 아닙니다."),

	// Token
	INVALID_TOKEN_FORMAT(HttpStatus.UNAUTHORIZED, "TOKEN401", "잘못된 토큰 형식입니다."),
	EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN402", "만료된 액세스 토큰입니다."),
	INVALID_TOKEN_SIGNATURE(HttpStatus.UNAUTHORIZED, "TOKEN403", "유효하지 않은 토큰 서명입니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN404", "유효하지 않은 리프레시 토큰입니다."),
	INVALID_AUTH_CODE(HttpStatus.BAD_REQUEST, "TOKEN405", "유효하지 않은 인증 코드입니다."),
	JWT_PROCESSING_ERROR(HttpStatus.UNAUTHORIZED, "TOKEN406", "JWT 처리 중 오류가 발생했습니다."),
	EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN407", "만료된 리프레시 토큰입니다."),

	// SMS
	SMS_VERIFICATION_FAILED(HttpStatus.BAD_REQUEST, "SMS401", "SMS 인증에 실패했습니다. 인증번호를 다시 확인하거나 재발송 해주세요."),
	SMS_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SMS402", "SMS 발송에 실패했습니다."),
	SMS_RESEND_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "SMS403", "하루 인증 요청 가능 횟수를 초과했습니다. 제한 10회."),

	// Travel-time
	GOOGLE_DIRECTIONS_API_FAILED(HttpStatus.BAD_GATEWAY, "TRAVEL401", "Google Directions API 호출에 실패했습니다."),
	ROUTE_NOT_FOUND(HttpStatus.BAD_REQUEST, "TRAVEL402", "출발지/도착지 좌표가 잘못되었거나, 요청 위치에서는 대중교통 이동 경로를 지원하지 않습니다. 요청 유저의 좌표를 확인해주세요."),

	// Geo
	GOOGLE_API_ERROR(HttpStatus.BAD_GATEWAY, "GEO401", "구글 API 호출 중 오류 발생"),
	ADDRESS_CONVERSION_FAILED(HttpStatus.BAD_REQUEST, "GEO402", "주소 변환 실패"),

	// Company
	DUPLICATE_USERNAME(HttpStatus.CONFLICT, "COMPANY401", "이미 사용중인 아이디입니다."),
	DUPLICATE_BUSINESS_NO(HttpStatus.CONFLICT, "COMPANY402", "이미 가입한 사업자등록번호입니다."),
	INVALID_BUSINESS_NO(HttpStatus.BAD_REQUEST, "COMPANY403", "유효하지 않거나 활성화되지 않은 사업자등록번호입니다."),
	COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "COMPANY404", "존재하지 않는 기업 계정입니다."),
	WITHDRAWN_COMPANY(HttpStatus.BAD_REQUEST, "COMPANY405", "탈퇴한 기업 계정입니다."),
	ALREADY_WITHDRAWN_COMPANY(HttpStatus.BAD_REQUEST, "COMPANY406", "이미 탈퇴한 기업 계정입니다."),
	NOT_WITHDRAWN_COMPANY(HttpStatus.BAD_REQUEST, "COMPANY407", "탈퇴하지 않은 기업 계정입니다."),
	COMPANY_WITHDRAWAL_NOT_CONFIRMED(HttpStatus.BAD_REQUEST, "COMPANY408", "탈퇴 의도가 확인되지 않았습니다."),
	DUPLICATE_PHONE(HttpStatus.CONFLICT, "COMPANY409", "이미 사용중인 전화번호입니다."),

	// External API
	EXTERNAL_API_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "EXTERNAL401", "외부 API 호출 중 오류가 발생했습니다."),

	// Page
	PAGE_NUMBER_NOT_NUMBER(HttpStatus.BAD_REQUEST, "PAGE400", "페이지는 숫자만 가능합니다."),
	PAGE_NUMBER_NEGATIVE(HttpStatus.BAD_REQUEST, "PAGE400", "페이지 숫자는 양수만 가능합니다."),

	// Search
	INVALID_SORT_TYPE(HttpStatus.BAD_REQUEST, "SEARCH401", "지원하지 않는 정렬 타입입니다."),
	ES_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"SEARCH402", "Elasticsearch 연결에 실패했습니다."),
	ES_REQUEST_ERROR(HttpStatus.BAD_REQUEST,"SEARCH403", "Elasticsearch 요청 처리 중 오류가 발생했습니다. 인덱스나 쿼리를 확인해주세요."),
	ES_PARTIAL_SHARD_FAILURE(HttpStatus.BAD_GATEWAY,"SEARCH403", "Elasticsearch 일부 샤드에서 오류가 발생했습니다. 검색 결과가 누락되었을 수 있습니다."),

	// Application
	INVALID_APPLICATION_ID(HttpStatus.BAD_REQUEST, "APPLICATION400", "유효하지 않은 ApplicationId 입니다."),
	APPLICATION_PARSING_ERROR(HttpStatus.BAD_REQUEST, "APPLICATION500", "결과 파싱에러 입니다. 관리자에게 문의해주세요"),

	// Job
	INVALID_JOB_POST_ID(HttpStatus.BAD_REQUEST, "JOB401", "유효하지 않은 채용공고 ID입니다."),

	// Qusetion
	SENIOR_QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND,"QUESTION401","해당 시니어 질문을 찾을 수 없습니다."),
	SENIOR_QUESTION_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND,"QUESTION402","해당 시니어 질문의 옵션을 찾을 수 없습니다."),

	// AI
	AI_RESPONSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AI501", "AI 응답 파싱에 실패했습니다."),
	AI_REQUEST_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AI502", "AI 요청 중 오류가 발생했습니다.");

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
