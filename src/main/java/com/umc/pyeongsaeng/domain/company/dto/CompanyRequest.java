package com.umc.pyeongsaeng.domain.company.dto;

import jakarta.validation.constraints.*;
import lombok.*;

public class CompanyRequest {

	@Getter
	@NoArgsConstructor
	public static class CompanySignUpRequestDto {
		@NotBlank
		@Pattern(regexp = "^\\d{10}$", message = "사업자등록번호는 10자리 숫자여야 합니다.")
		private String businessNo;

		@NotBlank
		@Size(min = 4, max = 50)
		private String username;

		@NotBlank
		@Size(min = 6, max = 100)
		private String password;

		@NotBlank
		@Size(max = 100)
		private String companyName;

		@NotBlank
		@Size(max = 100)
		private String ownerName;

		@NotBlank
		@Pattern(regexp = "^010\\d{8}$", message = "올바른 전화번호 형식이 아닙니다.")
		private String phone;

		@NotBlank
		@Size(min = 6, max = 6)
		private String verificationCode;
	}

	@Getter
	@NoArgsConstructor
	public static class LoginRequestDto {
		@NotBlank
		private String username;

		@NotBlank
		private String password;
	}

	@Getter
	@NoArgsConstructor
	public static class UpdateProfileRequestDto {
		@Size(max = 100)
		private String companyName;

		@Size(max = 100)
		private String ownerName;

		@Pattern(regexp = "^010\\d{8}$", message = "올바른 전화번호 형식이 아닙니다.")
		private String phone;
		private String currentPassword;

		@Size(min = 6, max = 100)
		private String newPassword;

		public boolean isPasswordChangeRequested() {
			return currentPassword != null && newPassword != null;
		}
	}

	@Getter
	@NoArgsConstructor
	public static class WithdrawRequestDto {
		@NotNull
		private boolean confirmed;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FindCompanyUsernameDto {
		@NotBlank
		@Pattern(regexp = "^010\\d{8}$", message = "올바른 전화번호 형식이 아닙니다.")
		private String phone;

		@NotBlank
		private String ownerName;

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
