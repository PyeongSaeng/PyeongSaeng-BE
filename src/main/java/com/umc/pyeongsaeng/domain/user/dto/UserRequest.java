package com.umc.pyeongsaeng.domain.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequest {

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class WithdrawRequestDto {

		@NotNull
		private boolean confirmed;

		public boolean isConfirmed() {
			return confirmed;
		}
	}
}
