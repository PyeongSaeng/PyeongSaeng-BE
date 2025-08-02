package com.umc.pyeongsaeng.domain.user.dto;

import com.umc.pyeongsaeng.domain.senior.enums.*;

import jakarta.validation.constraints.*;
import lombok.*;

public class UserRequest {

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class WithdrawRequestDto {
		@NotNull
		private boolean confirmed;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class UpdateProtectorDto {
		private String name;

		@Pattern(regexp = "^010\\d{8}$", message = "올바른 전화번호 형식이 아닙니다.")
		private String phone;

		private boolean passwordChangeRequested;
		private String currentPassword;

		@Size(min = 6, max = 100)
		private String newPassword;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class UpdateSeniorDto {
		private String name;

		@Pattern(regexp = "^010\\d{8}$", message = "올바른 전화번호 형식이 아닙니다.")
		private String phone;

		private String roadAddress;
		private String detailAddress;
		private JobType job;
		private ExperiencePeriod experiencePeriod;
		private boolean passwordChangeRequested;
		private String currentPassword;

		@Size(min = 6, max = 100)
		private String newPassword;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FindUsernameDto {
		@NotBlank
		private String name;

		@NotBlank
		@Pattern(regexp = "^010\\d{8}$", message = "올바른 전화번호 형식이 아닙니다.")
		private String phone;

		@NotBlank
		private String verificationCode;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PasswordVerificationDto {
		@NotBlank
		private String username;

		@NotBlank
		@Pattern(regexp = "^010\\d{8}$", message = "올바른 전화번호 형식이 아닙니다.")
		private String phone;

		@NotBlank
		private String verificationCode;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PasswordChangeDto {
		@NotBlank
		private String username;

		@NotBlank
		@Size(min = 6, max = 100)
		private String newPassword;
	}
}
