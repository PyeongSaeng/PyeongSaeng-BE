package com.umc.pyeongsaeng.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoSignupRequestDto {

	private Long kakaoId;

	@NotBlank(message = "아이디를 입력해주세요.")
	@Size(min = 4, max = 20)
	private String username;

/*	@NotBlank(message = "비밀번호를 입력해주세요.")
	@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])(.{6,})$",
		message = "비밀번호는 알파벳 소문자 혹은 대문자, 기호로 6자리 이상 입력해야 합니다")
	private String password;*/

	@NotBlank(message = "닉네임을 입력해주세요.")
	private String name;

	@NotBlank(message = "전화번호를 입력해주세요.")
	@Pattern(regexp = "^010[0-9]{8}$", message = "유효한 전화번호 형식(010xxxxxxxx)으로 입력해주세요.")
	private String phone;

	@NotBlank(message = "역할을 선택해주세요.")
	@Pattern(regexp = "^(SENIOR|PROTECTOR)$", message = "SENIOR 또는 PROTECTOR만 선택 가능합니다.")
	private String role;
}
