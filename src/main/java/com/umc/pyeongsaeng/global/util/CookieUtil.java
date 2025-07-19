package com.umc.pyeongsaeng.global.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CookieUtil {

	private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
	private static final String COOKIE_PATH = "/";
	private static final String SAME_SITE = "Lax";

	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration;

	/**
	 * Refresh Token을 HttpOnly 쿠키로 생성
	 * @param refreshToken 리프레시 토큰
	 * @return ResponseCookie 객체
	 */
	public ResponseCookie createRefreshTokenCookie(String refreshToken) {
		return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
			.httpOnly(true)
			.secure(false)
			.sameSite(SAME_SITE)
			.path(COOKIE_PATH)
			.maxAge(refreshTokenExpiration / 1000)
			.build();
	}

	/**
	 * Refresh Token 쿠키 삭제
	 * @return 삭제용 ResponseCookie 객체
	 */
	public ResponseCookie deleteRefreshTokenCookie() {
		return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
			.httpOnly(true)
			.secure(false)
			.sameSite(SAME_SITE)
			.path(COOKIE_PATH)
			.maxAge(0)
			.build();
	}

	/**
	 * 요청에서 Refresh Token 쿠키 추출
	 * @param request HttpServletRequest
	 * @return Refresh Token 값 또는 null
	 */
	public String getRefreshTokenFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
}
