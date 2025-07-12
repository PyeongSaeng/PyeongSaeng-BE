package com.umc.pyeongsaeng.domain.auth.service;

import com.umc.pyeongsaeng.domain.auth.dto.AuthRequest;
import com.umc.pyeongsaeng.domain.token.dto.TokenResponse;

public interface AuthServiceCommand {

	/**
	 * 일반 로그인
	 * @param request 로그인 요청 정보
	 * @return 토큰 정보
	 */
	TokenResponse.TokenInfo login(AuthRequest.Login request);

	/**
	 * 보호자 회원가입
	 * @param request 회원가입 요청 정보
	 * @return 토큰 정보
	 */
	TokenResponse.TokenInfo signupProtector(AuthRequest.ProtectorSignup request);

	/**
	 * 시니어 회원가입
	 * @param request 회원가입 요청 정보
	 * @return 토큰 정보
	 */
	TokenResponse.TokenInfo signupSenior(AuthRequest.SeniorSignup request);

	/**
	 * 로그아웃 처리
	 * @param userId 사용자 ID
	 */
	void logout(Long userId);
}
