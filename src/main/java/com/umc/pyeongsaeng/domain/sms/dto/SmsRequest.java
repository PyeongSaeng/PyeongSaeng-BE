package com.umc.pyeongsaeng.domain.sms.dto;

import io.swagger.v3.oas.annotations.media.*;
import jakarta.validation.constraints.*;
import lombok.*;

public class SmsRequest {

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@Schema(description = "SMS 인증번호 발송 요청")
	public static class SmsVerificationRequestDto {
		@NotBlank
		@Pattern(regexp = "^010\\d{8}$", message = "올바른 전화번호 형식이 아닙니다.")
		private String phone;
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@Schema(description = "SMS 인증번호 확인 요청")
	public static class SmsVerificationConfirmRequestDto {
		@NotBlank
		@Pattern(regexp = "^010\\d{8}$", message = "올바른 전화번호 형식이 아닙니다.")
		private String phone;

		@NotBlank
		private String verificationCode;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AccountSmsRequestDto {
		@NotBlank
		@Pattern(regexp = "^010\\d{8}$", message = "올바른 전화번호 형식이 아닙니다.")
		private String phone;
	}
}
