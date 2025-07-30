package com.umc.pyeongsaeng.domain.user.dto;

import com.umc.pyeongsaeng.domain.senior.enums.ExperiencePeriod;
import com.umc.pyeongsaeng.domain.senior.enums.JobType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
