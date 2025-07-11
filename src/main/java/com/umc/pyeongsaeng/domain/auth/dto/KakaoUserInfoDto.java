package com.umc.pyeongsaeng.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoUserInfoDto {
	private Long kakaoId;
	private String email;
	private String nickname;
}
