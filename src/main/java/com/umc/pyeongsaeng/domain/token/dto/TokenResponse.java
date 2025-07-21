
package com.umc.pyeongsaeng.domain.token.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TokenResponse {

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TokenInfoResponseDto {
		private String accessToken;
		private String refreshToken;
		private Long userId;
		private String username;
		private String role;
		private boolean isFirstLogin;
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TokenExchangeResponseDto {
		private String accessToken;
		private Long userId;
		private String username;
		private String role;
		private boolean isFirstLogin;
		private String refreshTokenCookie;
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AccessTokenResponseDto {
		private String accessToken;
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RefreshTokenResponseDto {
		private String accessToken;
		private String refreshTokenCookie;
	}
}
