package com.umc.pyeongsaeng.domain.company.service;

import com.umc.pyeongsaeng.domain.company.dto.CompanyRequest;
import com.umc.pyeongsaeng.domain.company.dto.CompanyResponse;

public interface CompanyCommandService {
	/**
	 * 기업 회원가입을 처리.
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
	 * 기업 로그아웃 처리.
	 * @param companyId 기업 ID
	 */
	void logout(Long companyId);

	/**
	 * 기업 프로필 수정.
	 * @param companyId 기업 ID
	 * @param request 프로필 수정 요청 DTO
	 * @return 수정된 기업 정보 DTO
	 */
	CompanyResponse.CompanyInfoDto updateProfile(Long companyId, CompanyRequest.UpdateProfileRequestDto request);

	/**
	 * 기업 탈퇴 처리.
	 * @param companyId 기업 ID
	 * @param confirmed 탈퇴 의사 확인 여부
	 */
	void withdrawCompany(Long companyId, boolean confirmed);

	/**
	 * 기업 탈퇴 취소.
	 * @param username 기업 아이디
	 */
	void cancelWithdrawal(String username);

	/**
	 * 로그아웃 시 리프레시 토큰 쿠키 제거 명령 생성.
	 * @return 쿠키 제거 명령 문자열
	 */
	String getLogoutCookie();

	/**
	 * 비밀번호 재설정 전 인증
	 * @param request 인증 요청
	 * @return 사용자 아이디
	 */
	CompanyResponse.UsernameDto verifyResetPasswordCode(CompanyRequest.PasswordVerificationDto request);

	/**
	 * 비밀번호 재설정
	 * @param request 비밀번호 변경 요청
	 */
	void resetPassword(CompanyRequest.PasswordChangeDto request);
}
