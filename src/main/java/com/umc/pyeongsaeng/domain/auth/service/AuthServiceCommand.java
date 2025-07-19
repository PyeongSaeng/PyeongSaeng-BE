package com.umc.pyeongsaeng.domain.auth.service;

import com.umc.pyeongsaeng.domain.auth.dto.AuthRequest;
import com.umc.pyeongsaeng.domain.auth.dto.AuthResponse;

public interface AuthServiceCommand {

	/**
	 * 일반 로그인
	 * @param request 로그인 요청 정보
	 * @return 로그인 응답 (토큰 + 쿠키)
	 */
	AuthResponse.LoginResponseDto login(AuthRequest.LoginRequestDto request);

	/**
	 * 보호자 회원가입
	 * @param request 회원가입 요청 정보
	 * @return 로그인 응답 (토큰 + 쿠키)
	 */
	AuthResponse.LoginResponseDto signupProtector(AuthRequest.ProtectorSignupRequestDto request);

	/**
	 * 시니어 회원가입
	 * @param request 회원가입 요청 정보
	 * @return 로그인 응답 (토큰 + 쿠키)
	 */
	AuthResponse.LoginResponseDto signupSenior(AuthRequest.SeniorSignupRequestDto request);

	/**
	 * 로그아웃 처리
	 * @param userId 사용자 ID
	 */
	void logout(Long userId);

	/**
	 * 로그아웃 쿠키 생성
	 * @return 쿠키 문자열
	 */
	String getLogoutCookie();
}
