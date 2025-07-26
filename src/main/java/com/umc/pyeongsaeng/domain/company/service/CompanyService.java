package com.umc.pyeongsaeng.domain.company.service;

import com.umc.pyeongsaeng.domain.company.dto.CompanyRequest;
import com.umc.pyeongsaeng.domain.company.dto.CompanyResponse;

public interface CompanyService {
	void logout(Long companyId);
	void withdrawCompany(Long companyId, boolean confirmed);
	void cancelWithdrawal(String username);
	String getLogoutCookie();

	/**
	 * 기업 회원가입을 처리합니다.
	 * @param request 기업 회원가입 요청 DTO
	 * @return 회원가입 결과 응답 DTO
	 */
	CompanyResponse.CompanySignUpResponseDto signUp(CompanyRequest.CompanySignUpRequestDto request);

	/**
	 * 기업 로그인 처리.
	 * @param request 로그인 요청 DTO
	 * @return 로그인 결과 응답 DTO
	 */
	CompanyResponse.LoginResponseDto login(CompanyRequest.LoginRequestDto request);

	/**
	 * 기업 프로필 수정.
	 * @param companyId 기업 ID
	 * @param request 프로필 수정 요청 DTO
	 * @return 수정된 기업 정보 DTO
	 */
	CompanyResponse.CompanyInfoDto updateProfile(Long companyId, CompanyRequest.UpdateProfileRequestDto request);

	/**
	 * 기업 상세 정보 조회
	 * @param companyId 기업 ID
	 * @return 기업 상세 정보
	 */
	CompanyResponse.CompanyDetailDto getCompanyDetail(Long companyId);

	/**
	 * 아이디 중복 확인
	 * @param username 확인할 아이디
	 */
	void checkUsernameAvailability(String username);

}
