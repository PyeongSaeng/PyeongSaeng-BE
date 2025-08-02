package com.umc.pyeongsaeng.domain.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SmsResponse {

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SmsResultDto {
		private boolean isKakaoUser;
		private String message;

		public static SmsResultDto success() {
			return SmsResultDto.builder()
				.isKakaoUser(false)
				.message("인증번호가 발송되었습니다.")
				.build();
		}
	}
}
