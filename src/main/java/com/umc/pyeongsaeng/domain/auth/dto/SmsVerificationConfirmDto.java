package com.umc.pyeongsaeng.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SmsVerificationConfirmDto {

	@NotBlank(message = "전화번호를 입력해주세요.")
	@Pattern(regexp = "^010[0-9]{8}$", message = "유효한 전화번호 형식(010xxxxxxxx)으로 입력해주세요.")
	private String phone;

	@NotBlank(message = "인증번호를 입력해주세요.")
	@Size(min = 6, max = 6, message = "인증번호는 6자리입니다.")
	private String verificationCode;

	@NotNull(message = "사용자 ID는 필수입니다.")
	private Long userId;

	@NotBlank(message = "관계를 입력해주세요.")
	private String relation;
}
