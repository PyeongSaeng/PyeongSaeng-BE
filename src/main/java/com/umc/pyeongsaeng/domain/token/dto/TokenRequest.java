package com.umc.pyeongsaeng.domain.token.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TokenRequest {

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@Schema(description = "Authorization Code 교환 요청")
	public static class AuthCodeExchangeRequestDto {
		@NotBlank
		@Schema(description = "OAuth 로그인 후 받은 Authorization Code")
		private String code;
	}
}
