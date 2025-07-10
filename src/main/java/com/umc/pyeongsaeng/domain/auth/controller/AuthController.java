package com.umc.pyeongsaeng.domain.auth.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.umc.pyeongsaeng.domain.auth.dto.LoginRequestDto;
import com.umc.pyeongsaeng.domain.auth.dto.LoginResponseDto;
import com.umc.pyeongsaeng.domain.auth.dto.ProtectorSignupRequestDto;
import com.umc.pyeongsaeng.domain.auth.dto.SeniorSignupRequestDto;
import com.umc.pyeongsaeng.domain.auth.dto.SmsVerificationConfirmDto;
import com.umc.pyeongsaeng.domain.auth.dto.SmsVerificationRequestDto;
import com.umc.pyeongsaeng.domain.auth.service.AuthService;
import com.umc.pyeongsaeng.domain.auth.service.SmsService;
import com.umc.pyeongsaeng.domain.social.repository.SocialAccountRepository;
import com.umc.pyeongsaeng.domain.token.service.TokenService;
import com.umc.pyeongsaeng.domain.user.repository.UserRepository;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;
import com.umc.pyeongsaeng.global.apiPayload.code.status.SuccessStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증", description = "회원가입, 로그인, 토큰 관련 API")
public class AuthController {

	private static final String KAKAO_PROVIDER = "KAKAO";

	private final AuthService authService;
	private final TokenService tokenService;
	private final SmsService smsService;
	private final UserRepository userRepository;
	private final SocialAccountRepository socialAccountRepository;

	@PostMapping("/login")
	@Operation(summary = "일반 회원 로그인", description = "아이디와 비밀번호로 로그인합니다.")
	public ResponseEntity<ApiResponse<LoginResponseDto>> login(
		@Validated @RequestBody LoginRequestDto request) {

		LoginResponseDto response = authService.login(request);

		return ResponseEntity.ok(ApiResponse.onSuccess(response));
	}

	@Operation(summary = "카카오 로그인",
		description = "카카오 OAuth 로그인을 시작합니다.\n"
			+ "브라우저에서 직접 접근하시면 됩니다. http://localhost:8080/oauth2/authorization/kakao \n"
			+ "최초 로그인 시 회원가입이 진행됩니다.\n")
	@GetMapping("/kakao/login")
	public ResponseEntity<ApiResponse<String>> kakaoLogin() {
		return ResponseEntity.ok(ApiResponse.onSuccess("접근 URL: http://localhost:8080/oauth2/authorization/kakao"));
	}

	@PostMapping("/signup/protector")
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
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		description = "보호자 회원가입 요청 데이터",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ProtectorSignupRequestDto.class),
			examples = {
				@ExampleObject(
					name = "일반 회원가입",
					summary = "일반 보호자 회원가입 예시",
					value = """
                        {
                            "username": "protector01",
                            "password": "password123!",
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
	public ResponseEntity<ApiResponse<Map<String, String>>> signupProtector(
		@Validated @RequestBody ProtectorSignupRequestDto request) {

		validateProtectorSignup(request);

		LoginResponseDto response = authService.processProtectorSignup(
			extractUsername(request),
			extractPassword(request),
			request.getName(),
			request.getPhone(),
			request.getProviderType(),
			request.getProviderUserId()
		);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.onSuccess(createTempTokenResponse(response)));
	}

	@PostMapping("/signup/senior")
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
			+ "- 성별(gender): MALE 또는 FEMALE\n\n"
			+ "**보호자 연결:**\n"
			+ "- 독립적인 시니어 회원가입의 경우 protectorId가 null\n"
			+ "- 보호자와 연결일 경우 null 아님.\n"
			+ "- relation: 보호자와의 관계\n")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		description = "시니어 회원가입 요청 데이터",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = SeniorSignupRequestDto.class),
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
                            "phone": "01011111111",
                            "address": "서울특별시 강남구",
                            "job": "회사원",
                            "career": "10년 이상",
                            "protectorId": 1,
                            "relation": "어머니",
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
                          "phone": "01011111112",
                          "address": "서울특별시 강남구",
                          "job": "회사원",
                          "career": "10년 이상",
                          "protectorId": 1,
                          "relation": "아버지",
                          "providerType": "KAKAO",
                          "providerUserId": "1234567899"
                        }
                        """
				)
			}
		)
	)
	public ResponseEntity<ApiResponse<Map<String, String>>> signupSenior(
		@Validated @RequestBody SeniorSignupRequestDto request) {

		validateSeniorSignup(request);

		LoginResponseDto response = authService.processSeniorSignup(
			extractUsername(request),
			extractPassword(request),
			request.getName(),
			request.getAge(),
			request.getGender(),
			request.getPhone(),
			request.getAddress(),
			request.getJob(),
			request.getCareer(),
			request.getProtectorId(),
			request.getRelation(),
			request.getProviderType(),
			request.getProviderUserId()
		);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.onSuccess(createTempTokenResponse(response)));
	}

	@PostMapping("/sms/send")
	@Operation(summary = "SMS 인증번호 발송", description = "입력된 전화번호로 인증번호를 발송합니다.")
	public ResponseEntity<ApiResponse<String>> sendSmsVerification(
		@Validated @RequestBody SmsVerificationRequestDto request) {
		smsService.sendVerificationCode(request.getPhone());
		return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus.SMS_SENT.getMessage()));
	}

	@PostMapping("/sms/verify")
	@Operation(summary = "SMS 인증번호 확인",
		description = "SMS 인증번호를 확인합니다. 올바른 인증번호인 경우 성공 응답을 반환합니다.")
	public ResponseEntity<ApiResponse<String>> verifySmsCode(
		@Validated @RequestBody SmsVerificationConfirmDto request) {

		smsService.verifyCode(request.getPhone(), request.getVerificationCode());

		return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus.SMS_VERIFIED.getMessage()));
	}

	@GetMapping("/check-username")
	@Operation(summary = "아이디 중복 확인", description = "아이디 중복 여부를 확인합니다.")
	public ResponseEntity<ApiResponse<String>> checkUsername(@RequestParam String username) {
		if (userRepository.existsByUsername(username)) {
			throw new GeneralException(ErrorStatus.USERNAME_DUPLICATED);
		}
		return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus.USERNAME_AVAILABLE.getMessage()));
	}

	@PostMapping("/refresh")
	@Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 액세스 토큰을 갱신합니다.")
	public ResponseEntity<ApiResponse<String>> refreshToken(@RequestParam String refreshToken) {
		String newAccessToken = tokenService.refreshAccessToken(refreshToken);
		return ResponseEntity.ok(ApiResponse.onSuccess(newAccessToken));
	}

	@PostMapping("/logout")
	@Operation(summary = "로그아웃", description = "사용자 로그아웃 처리입니다.")
	public ResponseEntity<ApiResponse<String>> logout(@RequestParam Long userId) {
		tokenService.deleteRefreshToken(userId);
		return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus.LOGOUT_SUCCESS.getMessage()));
	}

	@GetMapping("/token")
	@Operation(summary = "임시 토큰으로 JWT 조회",
		description = "임시 토큰을 사용하여 실제 액세스, 리프레시 토큰을 받아옵니다.\n"
			+ "(회원 가입의 경우 임시 토큰이 발급되니 리프레시 토큰을 얻으려면 해당 api를 이용해야 합니다. 3분만 유효합니다.)")
	public ResponseEntity<ApiResponse<LoginResponseDto>> getTokenByTempToken(
		@RequestParam("tempToken") String tempToken) {

		LoginResponseDto tokens = tokenService.getTokensByTempToken(tempToken);
		if (tokens == null) {
			throw new GeneralException(ErrorStatus.INVALID_TEMP_TOKEN);
		}

		tokenService.deleteTempToken(tempToken);
		return ResponseEntity.ok(ApiResponse.onSuccess(tokens));
	}

	private void validateProtectorSignup(ProtectorSignupRequestDto request) {
		if (isKakaoSignup(request.getProviderType())) {
			validateKakaoSignup(request.getProviderUserId());
		} else if (!isKakaoSignup(request.getProviderType())) {
			validateGeneralSignup(request.getUsername(), request.getPassword());
		}

		validateDuplicatePhone(request.getPhone());
	}

	private void validateSeniorSignup(SeniorSignupRequestDto request) {
		if (isKakaoSignup(request.getProviderType())) {
			validateKakaoSignup(request.getProviderUserId());
		} else if (!isKakaoSignup(request.getProviderType())) {
			validateGeneralSignup(request.getUsername(), request.getPassword());
		}

		validateDuplicatePhone(request.getPhone());
		validateProtectorExists(request.getProtectorId());
	}

	private boolean isKakaoSignup(String providerType) {
		return KAKAO_PROVIDER.equalsIgnoreCase(providerType);
	}

	private void validateKakaoSignup(String providerUserId) {
		if (providerUserId == null || providerUserId.isEmpty()) {
			throw new GeneralException(ErrorStatus.INVALID_KAKAO_ID);
		}
		if (socialAccountRepository.existsByProviderTypeAndProviderUserId(KAKAO_PROVIDER, providerUserId)) {
			throw new GeneralException(ErrorStatus.KAKAO_ALREADY_REGISTERED);
		}
	}

	private void validateGeneralSignup(String username, String password) {
		if (password == null || password.isEmpty()) {
			throw new GeneralException(ErrorStatus.INVALID_PASSWORD);
		}
		if (userRepository.existsByUsername(username)) {
			throw new GeneralException(ErrorStatus.USERNAME_DUPLICATED);
		}
	}

	private void validateDuplicatePhone(String phone) {
		if (userRepository.existsByPhone(phone)) {
			throw new GeneralException(ErrorStatus.PHONE_DUPLICATED);
		}
	}

	private void validateProtectorExists(Long protectorId) {
		if (protectorId != null && !userRepository.existsById(protectorId)) {
			throw new GeneralException(ErrorStatus.PROTECTOR_NOT_FOUND);
		}
	}

	private String extractUsername(ProtectorSignupRequestDto request) {
		return isKakaoSignup(request.getProviderType()) ?
			request.getProviderUserId() : request.getUsername();
	}

	private String extractUsername(SeniorSignupRequestDto request) {
		return isKakaoSignup(request.getProviderType()) ?
			request.getProviderUserId() : request.getUsername();
	}

	private String extractPassword(ProtectorSignupRequestDto request) {
		return isKakaoSignup(request.getProviderType()) ? null : request.getPassword();
	}

	private String extractPassword(SeniorSignupRequestDto request) {
		return isKakaoSignup(request.getProviderType()) ? null : request.getPassword();
	}

	private Map<String, String> createTempTokenResponse(LoginResponseDto response) {
		return Map.of(
			"tempToken", response.getTempToken(),
			"isFirstLogin", String.valueOf(response.isFirstLogin())
		);
	}
}
