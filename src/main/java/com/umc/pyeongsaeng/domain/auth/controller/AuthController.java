package com.umc.pyeongsaeng.domain.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.umc.pyeongsaeng.domain.auth.dto.AuthRequest;
import com.umc.pyeongsaeng.domain.auth.dto.AuthResponse;
import com.umc.pyeongsaeng.domain.auth.service.AuthCommandService;
import com.umc.pyeongsaeng.domain.auth.service.AuthQueryService;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;
import com.umc.pyeongsaeng.global.apiPayload.code.status.SuccessStatus;
import com.umc.pyeongsaeng.global.security.CustomUserDetails;

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
@Tag(name = "User 인증", description = "user 회원가입, 로그인 관련 API")
public class AuthController {

	private final AuthCommandService authCommandService;
	private final AuthQueryService authQueryService;

	@PostMapping("/login")
	@SecurityRequirements
	@Operation(summary = "일반 회원 로그인", description = "아이디와 비밀번호로 로그인합니다. \n"
			+ "Access와 Refresh 토큰을 발급받습니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH401", description = "아이디 또는 비밀번호가 올바르지 않습니다.")
	})
	public ResponseEntity<ApiResponse<AuthResponse.LoginResponseDto>> login(
		@Validated @RequestBody AuthRequest.LoginRequestDto request) {

		AuthResponse.LoginResponseDto response = authCommandService.login(request);

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.SET_COOKIE, response.getRefreshTokenCookie());

		return ResponseEntity.ok()
			.headers(headers)
			.body(ApiResponse.of(SuccessStatus._OK, response));
	}

	@GetMapping("/kakao/login")
	@SecurityRequirements
	@Operation(summary = "카카오 로그인",
		description = """
    카카오 OAuth 로그인을 시작합니다.

    브라우저에서 직접 아래 URL로 접근하시면 됩니다:
    ➤ http://localhost:8080/oauth2/authorization/kakao

    로그인 이후:
    http://localhost:3000/auth/callback?code=6518a349-5129-4989-918e-f26be5a428e3&isFirstLogin=false
    code 뒤에 허가코드가 나옵니다. 교환 api를 이용하여 로그인하시면 됩니다.

    최초 로그인 시 자동으로 회원가입이 진행됩니다. (Kakao ID 기반)
    """)
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다")
	public ApiResponse<String> kakaoLogin() {
		return ApiResponse.of(SuccessStatus._OK, null);
	}

	@PostMapping("/signup/protector")
	@SecurityRequirements
	@Operation(summary = "보호자 회원가입",
		description = """
    보호자 회원가입을 진행합니다. 일반 가입 또는 카카오 가입이 가능합니다.

    [일반 회원가입]
    - username, password, name, phone만 입력
    - providerType, providerUserId는 null

    [카카오 회원가입]
    - providerType: 'KAKAO'
    - providerUserId==username: 카카오 사용자 ID (필수)
    - password는 null
    - 접근 링크 ➤ http://localhost:8080/oauth2/authorization/kakao
    - 카카오 회원가입 이후: http://localhost:3000/auth/signup/kakao?kakaoId=1234567890&nickname=이수진(%EC%9D%B4%EC%88%98%EC%A7%84)
    - 카카오 아이디와 nickname을 활용하여 정상적인 회원가입 진행 (시니어 회원가입과 동일합니다.)
    """)
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
	public ResponseEntity<ApiResponse<AuthResponse.LoginResponseDto>> signupProtector(
		@Validated @RequestBody AuthRequest.ProtectorSignupRequestDto request) {

		AuthResponse.LoginResponseDto response = authCommandService.signupProtector(request);

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.SET_COOKIE, response.getRefreshTokenCookie());

		return ResponseEntity.status(HttpStatus.CREATED)
			.headers(headers)
			.body(ApiResponse.of(SuccessStatus.CREATED, response));
	}

	@PostMapping("/signup/senior")
	@SecurityRequirements
	@Operation(summary = "시니어 회원가입",
		description = """
    시니어 회원가입을 진행합니다. 보호자 연결 회원가입, 독립적인 회원가입 가능합니다.

    > 일반 회원가입:
    - username, password, name, age, gender 기본 정보 입력

    > 카카오 회원가입
    - providerType: 'KAKAO'
    - providerUserId: 카카오 사용자 고유 ID
    - username: providerUserId로 자동 설정
    - password: null

    > 프로필 정보:
    - gender: MALE, FEMALE
    - job: HOUSEWIFE, EMPLOYEE, PUBLIC_OFFICER, PROFESSIONAL, ARTIST, BUSINESS_OWNER, ETC
    - experiencePeriod: LESS_THAN_6_MONTHS, SIX_MONTHS_TO_1_YEAR, ONE_TO_THREE_YEARS, THREE_TO_FIVE_YEARS, FIVE_TO_TEN_YEARS, OVER_TEN_YEARS

    > 주소 정보:
    - zipcode: 필수
    - roadAddress: 필수
    - detailAddress: 선택

    > 보호자 연결:
    - 독립 가입: protectorId = null
    - 보호자 연결 가입: protectorId 필수, relation 입력 필요
    """)
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
	public ResponseEntity<ApiResponse<AuthResponse.LoginResponseDto>> signupSenior(
		@Validated @RequestBody AuthRequest.SeniorSignupRequestDto request) {

		AuthResponse.LoginResponseDto response = authCommandService.signupSenior(request);

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.SET_COOKIE, response.getRefreshTokenCookie());

		return ResponseEntity.status(HttpStatus.CREATED)
			.headers(headers)
			.body(ApiResponse.of(SuccessStatus.CREATED, response));
	}

	@GetMapping("/check-username")
	@SecurityRequirements
	@Operation(summary = "아이디 중복 확인", description = "아이디 중복 여부를 확인합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH402", description = "이미 사용중인 아이디입니다.")
	})
	public ApiResponse<String> checkUsername(@RequestParam String username) {
		authQueryService.checkUsernameAvailability(username);
		return ApiResponse.onSuccess(SuccessStatus.USERNAME_AVAILABLE.getMessage());
	}

	@PostMapping("/logout")
	@Operation(summary = "로그아웃", description = "사용자의 리프레시 토큰을 삭제하여 로그아웃 처리합니다.")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH201", description = "로그아웃되었습니다.")
	public ResponseEntity<ApiResponse<String>> logout(
		@AuthenticationPrincipal CustomUserDetails currentUser) {

		Long userId = currentUser.getId();

		authCommandService.logout(userId);

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.SET_COOKIE, authCommandService.getLogoutCookie());

		return ResponseEntity.ok()
			.headers(headers)
			.body(ApiResponse.of(SuccessStatus.LOGOUT_SUCCESS, SuccessStatus.LOGOUT_SUCCESS.getMessage()));
	}
}
