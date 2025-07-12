package com.umc.pyeongsaeng.domain.token.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.umc.pyeongsaeng.domain.token.dto.TokenRequest;
import com.umc.pyeongsaeng.domain.token.dto.TokenResponse;
import com.umc.pyeongsaeng.domain.token.service.TokenService;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
@Tag(name = "토큰", description = "토큰 관련 API")
public class TokenController {

	private final TokenService tokenService;

	@PostMapping("/exchange")
	@Operation(summary = "Authorization Code로 토큰 교환",
		description = "OAuth 로그인 후 받은 Authorization Code(5분 유효)를 실제 토큰(access, refresh)으로 교환합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "TOKEN405", description = "유효하지 않은 인증 코드입니다.")
	})
	public ApiResponse<TokenResponse.TokenInfo> exchangeToken(
		@RequestBody @Validated TokenRequest.AuthCodeExchange request) {

		// Authorization Code로 토큰 교환
		TokenResponse.TokenInfo response = tokenService.exchangeAuthorizationCode(request.getCode());

		return ApiResponse.onSuccess(response);
	}

	@PostMapping("/refresh")
	@Operation(summary = "토큰 갱신", description = "Refresh Token을 사용하여 Access Token을 갱신합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "TOKEN404", description = "유효하지 않은 리프레시 토큰입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "TOKEN402", description = "만료된 액세스 토큰입니다.")
	})
	public ApiResponse<String> refreshToken(
		@RequestParam String refreshToken) {

		// Access Token 갱신
		String newAccessToken = tokenService.refreshAccessToken(refreshToken);

		return ApiResponse.onSuccess(newAccessToken);
	}
}
