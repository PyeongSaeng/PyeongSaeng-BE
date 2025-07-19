package com.umc.pyeongsaeng.domain.token.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umc.pyeongsaeng.domain.token.dto.TokenRequest;
import com.umc.pyeongsaeng.domain.token.dto.TokenResponse;
import com.umc.pyeongsaeng.domain.token.service.TokenService;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;
import com.umc.pyeongsaeng.global.util.CookieUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
@Tag(name = "토큰", description = "토큰 관련 API")
public class TokenController {

	private final TokenService tokenService;
	private final CookieUtil cookieUtil;

	@PostMapping("/exchange")
	@SecurityRequirements
	@Operation(summary = "Authorization Code로 토큰 교환",
		description = "OAuth 로그인 후 받은 Authorization Code(5분 유효)를 실제 토큰으로 교환합니다. " +
			"리프레시 토큰은 HttpOnly 쿠키로 설정되고, 액세스 토큰은 응답 body에 포함됩니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "TOKEN405", description = "유효하지 않은 인증 코드입니다.")
	})
	public ResponseEntity<ApiResponse<TokenResponse.TokenExchangeResponseDto>> exchangeToken(
		@RequestBody @Validated TokenRequest.AuthCodeExchangeRequestDto request) {

		TokenResponse.TokenExchangeResponseDto response = tokenService.processTokenExchange(request.getCode());

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.SET_COOKIE, response.getRefreshTokenCookie());

		return ResponseEntity.ok()
			.headers(headers)
			.body(ApiResponse.onSuccess(response));
	}

	@PostMapping("/refresh")
	@SecurityRequirements
	@Operation(summary = "토큰 갱신",
		description = "HttpOnly 쿠키의 Refresh Token을 사용하여 Access / Refresh 토큰을 갱신합니다. ")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "TOKEN404", description = "유효하지 않은 리프레시 토큰입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "TOKEN406", description = "리프레시 토큰이 없습니다.")
	})
	public ResponseEntity<ApiResponse<TokenResponse.RefreshTokenResponseDto>> refreshToken(HttpServletRequest request) {
		String refreshToken = cookieUtil.getRefreshTokenFromCookie(request);

		if (refreshToken == null) {
			throw new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN);
		}

		TokenResponse.RefreshTokenResponseDto response = tokenService.processTokenRefresh(refreshToken);

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.SET_COOKIE, response.getRefreshTokenCookie());

		return ResponseEntity.ok()
			.headers(headers)
			.body(ApiResponse.onSuccess(response));
	}
}
