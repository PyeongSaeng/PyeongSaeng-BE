package com.umc.pyeongsaeng.domain.company.dto;

import com.umc.pyeongsaeng.domain.company.enums.CompanyStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class CompanyResponse {

	@Getter
	@Builder
	@AllArgsConstructor
	public static class CompanySignUpResponseDto {
		private Long companyId;
		private String username;
		private String businessNo;
		private String companyName;
		private String name;
		private String phone;
	}

	@Getter
	@Builder
	@AllArgsConstructor
	public static class LoginResponseDto {
		private Long companyId;
		private String username;
		private String businessNo;
		private String accessToken;
		private String refreshTokenCookie;
	}

	@Getter
	@Builder
	@AllArgsConstructor
	public static class CompanyInfoDto {
		private Long companyId;
		private String username;
		private String businessNo;
		private String name;
		private String companyName;
	}

	@Getter
	@Builder
	@AllArgsConstructor
	public static class CompanyDetailDto {
		private Long companyId;
		private String username;
		private String businessNo;
		private String companyName;
		private String name;
		private String email;
		private String phone;
		private CompanyStatus status;
	}
}
