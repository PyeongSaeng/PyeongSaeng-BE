package com.umc.pyeongsaeng.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthRequest {

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@Schema(description = "로그인 요청")
	public static class LoginRequestDto {
		@NotBlank
		@Schema(description = "사용자 아이디")
		private String username;

		@NotBlank
		@Schema(description = "비밀번호")
		private String password;
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@Schema(description = "보호자 회원가입 요청")
	public static class ProtectorSignupRequestDto {
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

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@Schema(description = "시니어 회원가입 요청")
	public static class SeniorSignupRequestDto {
		@Schema(description = "아이디", required = true)
		@NotBlank
		@Size(min = 4, max = 20)
		private String username;

		@Schema(description = "비밀번호 (카카오 로그인 시 null)", required = false)
		@Size(min = 6, max = 100)
		private String password;

		@Schema(description = "이름", example = "김시니어", required = true)
		@NotBlank
		@Size(max = 10)
		private String name;

		@Schema(description = "나이", example = "75", required = true)
		@NotNull
		@Min(value = 1)
		@Max(value = 150)
		private Integer age;

		@Schema(description = "성별", example = "FEMALE", allowableValues = {"MALE", "FEMALE"}, required = true)
		@NotBlank
		@Pattern(regexp = "^(MALE|FEMALE)$", message = "성별은 MALE 또는 FEMALE이어야 합니다.")
		private String gender;

		@Schema(description = "전화번호", example = "01012341234", required = true)
		@NotBlank
		@Pattern(regexp = "^01[0-9]{8,9}$", message = "올바른 전화번호 형식이 아닙니다.")
		private String phoneNum;

		@Schema(description = "우편번호", example = "01234", required = true)
		@NotBlank
		@Size(max = 10)
		private String zipcode;

		@Schema(description = "도로명 주소", required = true)
		@NotBlank
		@Size(max = 255)
		private String roadAddress;

		@Schema(description = "상세 주소", required = false)
		@Size(max = 255)
		private String detailAddress;

		@Schema(description = "직무", required = true)
		@NotBlank
		private String job;

		@Schema(description = "경력 기간", required = true)
		@NotBlank
		private String experiencePeriod;

		@Schema(description = "보호자 ID (보호자와 연결할 경우에 사용)", required = false)
		private Long protectorId;

		@Schema(description = "소셜 로그인 타입", example = "KAKAO", required = false)
		private String providerType;

		@Schema(description = "소셜 로그인 사용자 ID", required = false)
		private String providerUserId;
	}

}
