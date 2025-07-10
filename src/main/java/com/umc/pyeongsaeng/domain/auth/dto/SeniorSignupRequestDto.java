package com.umc.pyeongsaeng.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SeniorSignupRequestDto {

	@Schema(description = "아이디", example = "senior01", required = true)
	@NotBlank
	@Size(min = 4, max = 20)
	private String username;

	@Schema(description = "비밀번호 (카카오 로그인 시 null)", example = "password123!", required = false)
	@Size(min = 6, max = 100)
	private String password;

	@Schema(description = "이름", example = "김시니어", required = true)
	@NotBlank
	@Size(max = 10, message = "이름은 10자 이하여야 합니다.")
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
	private String phone;

	@Schema(description = "거주지", required = false)
	@Size(max = 100)
	private String address;

	@Schema(description = "직무", required = false)
	@Size(max = 50)
	private String job;

	@Schema(description = "경력", required = false)
	@Size(max = 50)
	private String career;

	@Schema(description = "보호자 ID (보호자와 연결할 경우에 사용)", example = "1", required = false)
	private Long protectorId;

	@Schema(description = "보호자와의 관계", required = false)
	@Size(max = 20)
	private String relation;

	@Schema(description = "소셜 로그인 타입", example = "KAKAO", required = false)
	private String providerType;

	@Schema(description = "소셜 로그인 사용자 ID", required = false)
	private String providerUserId;
}
