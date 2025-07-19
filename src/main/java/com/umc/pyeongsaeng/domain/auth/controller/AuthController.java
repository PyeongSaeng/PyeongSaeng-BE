package com.umc.pyeongsaeng.domain.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.umc.pyeongsaeng.domain.auth.dto.AuthRequest;
import com.umc.pyeongsaeng.domain.auth.service.AuthServiceCommand;
import com.umc.pyeongsaeng.domain.auth.service.AuthServiceQuery;
import com.umc.pyeongsaeng.domain.token.dto.TokenResponse;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;
import com.umc.pyeongsaeng.global.apiPayload.code.status.SuccessStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증", description = "회원가입, 로그인 관련 API")
public class AuthController {

	private final AuthServiceCommand authServiceCommand;
	private final AuthServiceQuery authServiceQuery;

	@PostMapping("/login")
	@SecurityRequirements
	@Operation(summary = "일반 회원 로그인", description = "아이디와 비밀번호로 로그인합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH401", description = "아이디 또는 비밀번호가 올바르지 않습니다.")
	})
	public ApiResponse<TokenResponse.TokenInfoResponseDto> login(
		@Validated @RequestBody AuthRequest.LoginRequestDto request) {

		// 일반 회원 로그인 처리
		TokenResponse.TokenInfoResponseDto response = authServiceCommand.login(request);

		return ApiResponse.onSuccess(response);
	}

	@GetMapping("/kakao/login")
	@SecurityRequirements
	@Operation(summary = "카카오 로그인",
		description = "카카오 OAuth 로그인을 시작합니다.\n"
			+ "브라우저에서 직접 접근하시면 됩니다. http://localhost:8080/oauth2/authorization/kakao \n"
			+ "최초 로그인 시 회원가입이 진행됩니다.\n")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다")
	public ApiResponse<String> kakaoLogin() {
		return ApiResponse.onSuccess("접근 URL: http://localhost:8080/oauth2/authorization/kakao");
	}

	@PostMapping("/signup/protector")
	@SecurityRequirements
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "보호자 회원가입",
		description = "보호자 회원가입을 진행합니다.\n\n"
			+ "**회원가입 절차:**\n"
			+ "1. 아이디(username), 비밀번호(password) 입력 (일반 회원가입)\n"
			+ "2. 카카오 회원가입의 경우:\n"
			+ "   - providerType='KAKAO'\n"
			+ "   - providerUserId에 카카오 ID 입력\n"
			+ "   - username은 providerUserId와 동일하게 처리\n"
			+ "   - password는 null 또는 빈 문자열\n\n"
			+ "**주의사항:**\n"
			+ "- role은 자동으로 PROTECTOR로 설정됩니다.\n"
			+ "- 보호자 회원가입 완료 후 시니어 회원가입이 이어집니다.\n"
			+ "- 보호자는 최대 3명의 시니어를 등록할 수 있습니다.\n")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON201", description = "성공적으로 생성되었습니다"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH402", description = "이미 사용중인 아이디입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH403", description = "비밀번호는 필수 입력값입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH404", description = "유효하지 않은 카카오 ID입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH405", description = "이미 등록된 카카오 계정입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER402", description = "이미 사용중인 전화번호입니다.")
	})
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		description = "보호자 회원가입 요청 데이터",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = AuthRequest.ProtectorSignupRequestDto.class),
			examples = {
				@ExampleObject(
					name = "일반 회원가입",
					summary = "일반 보호자 회원가입 예시",
					value = """
                        {
                            "username": "protector1",
                            "password": "password1",
                            "name": "보호자",
                            "phone": "01012345678",
                            "providerType": null,
                            "providerUserId": null
                        }
                        """
				),
				@ExampleObject(
					name = "카카오 회원가입",
					summary = "카카오 보호자 회원가입 예시",
					value = """
                        {
                            "username": "1234567890",
                            "password": null,
                            "name": "보호자",
                            "phone": "01012345678",
                            "providerType": "KAKAO",
                            "providerUserId": "1234567890"
                        }
                        """
				)
			}
		)
	)
	public ApiResponse<TokenResponse.TokenInfoResponseDto> signupProtector(
		@Validated @RequestBody AuthRequest.ProtectorSignupRequestDto request) {

		// 보호자 회원가입 처리
		TokenResponse.TokenInfoResponseDto response = authServiceCommand.signupProtector(request);

		return ApiResponse.onSuccess(response);
	}

	@PostMapping("/signup/senior")
	@SecurityRequirements
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "시니어 회원가입",
		description = "시니어 회원가입을 진행합니다.\n\n"
			+ "**회원가입 절차:**\n"
			+ "1. 아이디(username), 비밀번호(password) 입력 (일반 회원가입)\n"
			+ "2. 카카오 회원가입의 경우:\n"
			+ "   - providerType='KAKAO'\n"
			+ "   - providerUserId에 카카오 ID 입력\n"
			+ "   - username은 providerUserId와 동일하게 처리\n"
			+ "   - password는 null 또는 빈 문자열\n\n"
			+ "**프로필 정보:**\n"
			+ "- 성별(gender): MALE 또는 FEMALE\n"
			+ "- 직업(job): HOUSEWIFE, EMPLOYEE, PUBLIC_OFFICER, PROFESSIONAL, ARTIST, BUSINESS_OWNER, ETC\n"
			+ "- 경력기간(experiencePeriod): LESS_THAN_6_MONTHS, SIX_MONTHS_TO_1_YEAR, ONE_TO_THREE_YEARS, THREE_TO_FIVE_YEARS, FIVE_TO_TEN_YEARS, OVER_TEN_YEARS\n\n"
			+ "**주소 정보:**\n"
			+ "- zipcode: 우편번호 (필수)\n"
			+ "- roadAddress: 도로명 주소 (필수)\n"
			+ "- detailAddress: 상세 주소 (선택)\n\n"
			+ "**보호자 연결:**\n"
			+ "- 독립적인 시니어 회원가입의 경우 protectorId가 null\n"
			+ "- 보호자와 연결일 경우 protectorId가 null 아님.\n"
			+ "- relation: 보호자와의 관계\n")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON201", description = "성공적으로 생성되었습니다"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH402", description = "이미 사용중인 아이디입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH403", description = "비밀번호는 필수 입력값입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH404", description = "유효하지 않은 카카오 ID입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH405", description = "이미 등록된 카카오 계정입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER402", description = "이미 사용중인 전화번호입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER403", description = "존재하지 않는 보호자입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER404", description = "보호자 권한이 없는 사용자입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER405", description = "보호자는 최대 3명의 시니어만 등록할 수 있습니다.")
	})
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		description = "시니어 회원가입 요청 데이터",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = AuthRequest.SeniorSignupRequestDto.class),
			examples = {
				@ExampleObject(
					name = "보호자 연결 시니어 회원가입(일반가입)",
					summary = "보호자와 연결된 시니어 일반 회원가입 예시",
					value = """
                        {
                            "username": "senior1",
                            "password": "password1",
                            "name": "시니어",
                            "age": 75,
                            "gender": "FEMALE",
                            "phoneNum": "01011111111",
                            "zipcode": "13494",
                            "roadAddress": "경기 성남시 분당구 판교역로 235 (에이치스퀘어 엔동)",
                            "detailAddress": "101호",
                            "job": "EMPLOYEE",
                            "experiencePeriod": "OVER_TEN_YEARS",
                            "protectorId": 1,
                            "relation": "모녀",
                            "providerType": null,
                            "providerUserId": null
                        }
                        """
				),
				@ExampleObject(
					name = "보호자 연결 시니어 회원가입(카카오 가입)",
					summary = "보호자와 연결된 시니어 카카오 회원가입 예시",
					value = """
                        {
                          "username": "1234567899",
                          "password": null,
                          "name": "시니어",
                          "age": 75,
                          "gender": "MALE",
                          "phoneNum": "01011111112",
                          "zipcode": "13494",
                          "roadAddress": "경기 성남시 분당구 판교역로 235 (에이치스퀘어 엔동)",
                          "detailAddress": "101호",
                          "job": "EMPLOYEE",
                          "experiencePeriod": "OVER_TEN_YEARS",
                          "protectorId": 1,
                          "relation": "부녀",
                          "providerType": "KAKAO",
                          "providerUserId": "1234567899"
                        }
                        """
				)
			}
		)
	)
	public ApiResponse<TokenResponse.TokenInfoResponseDto> signupSenior(
		@Validated @RequestBody AuthRequest.SeniorSignupRequestDto request) {

		// 시니어 회원가입 처리
		TokenResponse.TokenInfoResponseDto response = authServiceCommand.signupSenior(request);

		return ApiResponse.onSuccess(response);
	}

	@GetMapping("/check-username")
	@SecurityRequirements
	@Operation(summary = "아이디 중복 확인", description = "아이디 중복 여부를 확인합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH402", description = "이미 사용중인 아이디입니다.")
	})
	public ApiResponse<String> checkUsername(@RequestParam String username) {

		// 아이디 사용 가능 여부 확인
		if (!authServiceQuery.isUsernameAvailable(username)) {
			throw new GeneralException(ErrorStatus.USERNAME_DUPLICATED);
		}

		return ApiResponse.onSuccess(SuccessStatus.USERNAME_AVAILABLE.getMessage());
	}

	@PostMapping("/logout")
	@Operation(summary = "로그아웃", description = "사용자 로그아웃 처리입니다.")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH201", description = "로그아웃되었습니다.")
	public ApiResponse<String> logout(@RequestParam Long userId) {

		// 로그아웃 처리
		authServiceCommand.logout(userId);

		return ApiResponse.onSuccess(SuccessStatus.LOGOUT_SUCCESS.getMessage());
	}
}
