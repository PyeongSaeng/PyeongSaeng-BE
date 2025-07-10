package com.umc.pyeongsaeng.domain.auth.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
	private String accessToken;
	private String refreshToken;
	private Long userId;
	private String username;
	private String role;
	private boolean isFirstLogin;
	private String tempToken;
}
