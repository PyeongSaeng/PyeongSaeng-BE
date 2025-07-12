package com.umc.pyeongsaeng.domain.sms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SmsRequest {

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@Schema(description = "SMS 인증번호 발송 요청")
	public static class SmsVerification {
		@NotBlank(message = "전화번호는 필수입니다.")
		@Pattern(regexp = "^010\\d{8}$", message = "올바른 전화번호 형식이 아닙니다.")
		@Schema(description = "전화번호", example = "01012345678")
		private String phone;
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@Schema(description = "SMS 인증번호 확인 요청")
	public static class SmsVerificationConfirm {
		@NotBlank(message = "전화번호는 필수입니다.")
		@Pattern(regexp = "^010\\d{8}$", message = "올바른 전화번호 형식이 아닙니다.")
		@Schema(description = "전화번호", example = "01012345678")
		private String phone;

		@NotBlank(message = "인증번호는 필수입니다.")
		@Pattern(regexp = "^\\d{6}$", message = "인증번호는 6자리 숫자여야 합니다.")
		@Schema(description = "인증번호", example = "123456")
		private String verificationCode;
	}
}
