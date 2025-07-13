package com.umc.pyeongsaeng.domain.token.service;

import com.umc.pyeongsaeng.domain.token.dto.TokenResponse;
import com.umc.pyeongsaeng.domain.user.entity.User;

public interface TokenService {

	/**
	 * Refresh Token 저장
	 * @param userId 사용자 ID
	 * @param refreshToken 저장할 토큰
	 */
	void saveRefreshToken(Long userId, String refreshToken);

	/**
	 * Access Token 갱신
	 * @param refreshToken Refresh Token
	 * @return 새로운 Access Token
	 */
	String refreshAccessToken(String refreshToken);

	/**
	 * Refresh Token 삭제 (로그아웃)
	 * @param userId 사용자 ID
	 */
	void deleteRefreshToken(Long userId);

	/**
	 * Authorization Code 저장
	 * @param authCode 인증 코드
	 * @param tokenInfoResponseDto 토큰 정보
	 */
	void saveAuthorizationCode(String authCode, TokenResponse.TokenInfoResponseDto tokenInfoResponseDto);

	/**
	 * Authorization Code로 토큰 교환
	 * @param authCode 인증 코드
	 * @return 토큰 정보
	 */
	TokenResponse.TokenInfoResponseDto exchangeAuthorizationCode(String authCode);

	/**
	 * JWT 토큰 생성 및 응답 객체 반환
	 * @param user 사용자 정보
	 * @param isFirstLogin 최초 로그인 여부
	 * @return 토큰 정보
	 */
	TokenResponse.TokenInfoResponseDto generateTokenResponse(User user, boolean isFirstLogin);

	/**
	 * Refresh Token 유효성 확인
	 * @param refreshToken 확인할 토큰
	 * @return 유효 여부
	 */
	boolean isValidRefreshToken(String refreshToken);
}
