package com.umc.pyeongsaeng.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthResponse {

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class KakaoUserInfoResponseDto {
		private Long id;
		private String email;
		private String nickname;
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class LoginResponseDto {
		private String accessToken;
		private Long userId;
		private String username;
		private String role;
		private boolean isFirstLogin;
		private String refreshTokenCookie;
	}
}
