package com.umc.pyeongsaeng.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "보호자 회원가입 요청 DTO")
public class ProtectorSignupRequestDto {

	@Schema(description = "아이디", required = true)
	@NotBlank
	@Size(min = 4, max = 20)
	private String username;

	@Schema(description = "비밀번호 (카카오 로그인 시 null)", required = false)
	@Size(min = 6, max = 100)
	private String password;

	@Schema(description = "이름", required = true)
	@NotBlank
	@Size(max = 50)
	private String name;

	@Schema(description = "전화번호", example = "01012341234", required = true)
	@Pattern(regexp = "^01[0-9]{8,9}$", message = "올바른 전화번호 형식이 아닙니다.")
	private String phone;

	@Schema(description = "소셜 로그인 타입", example = "KAKAO", required = false)
	private String providerType;

	@Schema(description = "소셜 로그인 사용자 ID", required = false)
	private String providerUserId;
}
