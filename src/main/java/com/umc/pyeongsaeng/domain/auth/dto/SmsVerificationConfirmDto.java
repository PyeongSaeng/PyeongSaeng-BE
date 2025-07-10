package com.umc.pyeongsaeng.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsVerificationConfirmDto {

	@NotBlank(message = "전화번호는 필수입니다.")
	@Pattern(regexp = "^010\\d{8}$", message = "올바른 전화번호 형식이 아닙니다.")
	private String phone;

	@NotBlank(message = "인증번호는 필수입니다.")
	@Pattern(regexp = "^\\d{6}$", message = "인증번호는 6자리 숫자여야 합니다.")
	private String verificationCode;
}
