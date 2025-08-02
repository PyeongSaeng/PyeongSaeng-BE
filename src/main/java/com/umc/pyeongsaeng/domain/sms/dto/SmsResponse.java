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

		public static SmsResultDto kakaoUser() {
			return SmsResultDto.builder()
				.isKakaoUser(true)
				.message("카카오 회원은 아이디와 비밀번호를 찾을 수 없습니다.")
				.build();
		}
	}
}
