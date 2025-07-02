package com.umc.pyeongsaeng.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.umc.pyeongsaeng.domain.auth.dto.KakaoSignupRequestDto;
import com.umc.pyeongsaeng.domain.auth.dto.LoginResponseDto;
import com.umc.pyeongsaeng.domain.auth.dto.SmsVerificationConfirmDto;
import com.umc.pyeongsaeng.domain.auth.dto.SmsVerificationRequestDto;
import com.umc.pyeongsaeng.domain.auth.repository.SocialAccountRepository;
import com.umc.pyeongsaeng.domain.auth.service.KakaoAuthService;
import com.umc.pyeongsaeng.domain.token.service.TokenService;
import com.umc.pyeongsaeng.domain.user.repository.UserRepository;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증", description = "회원가입, 로그인, 토큰 관련 API")
public class AuthController {

	private final KakaoAuthService kakaoAuthService;
	private final TokenService tokenService;
	private final UserRepository userRepository;
	private final SocialAccountRepository socialAccountRepository;

	@Operation(summary = "카카오 로그인", description = "카카오 OAuth 로그인을 시작합니다. 브라우저에서 직접 접근하시면 됩니다. http://localhost:8080/oauth2/authorization/kakao 최초 로그인 시 회원가입이 진행됩니다."
		+ "회원가입 후 얻은 사용자 정보 ID를 이용하여 카카오 회원가입을 진행하면 됩니다.")
	@GetMapping("/kakao/login")
	public ResponseEntity<ApiResponse<String>> kakaoLogin() {
		return ResponseEntity.ok(ApiResponse.onSuccess("접근 URL: http://localhost:8080/oauth2/authorization/kakao"));
	}

	@PostMapping("/kakao/signup")
	@Operation(summary = "카카오 회원가입", description = "카카오 OAuth 후 추가 정보를 입력하여 회원가입을 완료합니다.")
	public ResponseEntity<ApiResponse<LoginResponseDto>> kakaoSignup(
		@Validated @RequestBody KakaoSignupRequestDto request
	) {

		if (request.getKakaoId() == null || request.getKakaoId() <= 0) {
			return ResponseEntity.badRequest()
				.body(ApiResponse.onFailure("INVALID_KAKAO_ID", "유효한 카카오 ID가 필요합니다.", null));
		}

		if (socialAccountRepository.existsByProviderTypeAndProviderUserId("KAKAO", request.getKakaoId().toString())) {
			return ResponseEntity.badRequest()
				.body(ApiResponse.onFailure("KAKAO_ALREADY_REGISTERED", "이미 가입된 카카오 계정입니다.", null));
		}

		if (userRepository.existsByUsername(request.getUsername())) {
			return ResponseEntity.badRequest()
				.body(ApiResponse.onFailure("USERNAME_DUPLICATED", "이미 사용 중인 아이디입니다.", null));
		}

		LoginResponseDto response = kakaoAuthService.processKakaoSignup(
			request.getKakaoId(),
			request.getUsername(),
			request.getName(),
			request.getPhone(),
			request.getRole()
		);

		return ResponseEntity.ok(ApiResponse.onSuccess(response));
	}

	@PostMapping("/sms/send")
	@Operation(summary = "SMS 인증번호 발송", description = "시니어 전화번호로 인증번호를 발송합니다.")
	public ResponseEntity<ApiResponse<String>> sendSmsVerification(
		@Validated @RequestBody SmsVerificationRequestDto request) {

		try {
			kakaoAuthService.sendSmsForProtector(request.getPhone());
			return ResponseEntity.ok(ApiResponse.onSuccess("인증번호가 발송되었습니다."));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
				.body(ApiResponse.onFailure("SMS_SEND_FAILED", e.getMessage(), null));
		}
	}

	@PostMapping("/sms/verify")
	@Operation(summary = "SMS 인증 확인 및 보호자 회원가입 완료", description = "SMS 인증번호를 확인하고 보호자-시니어 관계를 생성합니다.")
	public ResponseEntity<ApiResponse<LoginResponseDto>> verifySmsAndCompleteSignup(
		@Validated @RequestBody SmsVerificationConfirmDto request) {

		try {
			LoginResponseDto response = kakaoAuthService.confirmSmsAndCompleteSignup(request);
			return ResponseEntity.ok(ApiResponse.onSuccess(response));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
				.body(ApiResponse.onFailure("SMS_VERIFICATION_FAILED", e.getMessage(), null));
		}
	}

	@GetMapping("/check-username")
	@Operation(summary = "아이디 중복 확인", description = "아이디 중복 여부를 확인합니다.")
	public ResponseEntity<ApiResponse<String>> checkUsername(@RequestParam String username) {
		boolean exists = userRepository.existsByUsername(username);

		if (exists) {
			return ResponseEntity.ok(ApiResponse.onSuccess("이미 사용 중인 아이디입니다."));
		} else {
			return ResponseEntity.ok(ApiResponse.onSuccess("사용 가능한 아이디입니다."));
		}
	}

	@PostMapping("/refresh")
	@Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 액세스 토큰을 갱신합니다.")
	public ResponseEntity<ApiResponse<String>> refreshToken(@RequestParam String refreshToken) {
		try {
			String newAccessToken = tokenService.refreshAccessToken(refreshToken);
			return ResponseEntity.ok(ApiResponse.onSuccess(newAccessToken));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
				.body(ApiResponse.onFailure("INVALID_REFRESH_TOKEN", e.getMessage(), null));
		}
	}

	@PostMapping("/logout")
	@Operation(summary = "로그아웃", description = "사용자 로그아웃 처리입니다.")
	public ResponseEntity<ApiResponse<String>> logout(@RequestParam Long userId) {
		tokenService.deleteRefreshToken(userId);
		return ResponseEntity.ok(ApiResponse.onSuccess("로그아웃되었습니다."));
	}

	@GetMapping("/token")
	@Operation(summary = "임시 토큰으로 JWT 조회", description = "임시 토큰을 사용하여 실제 액세스, 리프레시 토큰을 받아옵니다.")
	public ResponseEntity<ApiResponse<LoginResponseDto>> getTokenByTempToken(
		@RequestParam("tempToken") String tempToken) {

		LoginResponseDto tokens = tokenService.getTokensByTempToken(tempToken);
		if (tokens == null) {
			return ResponseEntity.badRequest()
				.body(ApiResponse.onFailure("INVALID_TEMP_TOKEN", "유효하지 않은 임시 토큰입니다.", null));
		}

		tokenService.deleteTempToken(tempToken);
		return ResponseEntity.ok(ApiResponse.onSuccess(tokens));
	}
}
