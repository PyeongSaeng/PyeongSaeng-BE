package com.umc.pyeongsaeng.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SmsVerificationRequestDto {

	@NotBlank(message = "전화번호를 입력해주세요.")
	@Pattern(regexp = "^010[0-9]{8}$", message = "유효한 전화번호 형식(010xxxxxxxx)으로 입력해주세요.")
	private String phone;
}
