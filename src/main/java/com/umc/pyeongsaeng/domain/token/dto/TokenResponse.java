
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
}
