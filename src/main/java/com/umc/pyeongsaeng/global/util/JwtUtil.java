package com.umc.pyeongsaeng.global.util;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {

	private final Key key;
	private final long accessTokenExpiration;
	private final long refreshTokenExpiration;

	public JwtUtil(
		@Value("${jwt.secret}") String secretKey,
		@Value("${jwt.access-token-expiration}") long accessTokenExpiration,
		@Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
	) {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);


		this.accessTokenExpiration = accessTokenExpiration;
		this.refreshTokenExpiration = refreshTokenExpiration;
	}

	/**
	 * Access Token 생성
	 * @param userId 사용자 ID
	 * @param role 사용자 역할
	 * @return Access Token
	 */
	public String generateAccessToken(Long userId, String role) {
		return generateToken(userId, role, accessTokenExpiration);
	}

	/**
	 * Refresh Token 생성
	 * @param userId 사용자 ID
	 * @param role 사용자 역할
	 * @return Refresh Token
	 */
	public String generateRefreshToken(Long userId, String role) {
		return generateToken(userId, role, refreshTokenExpiration);
	}

	// JWT 토큰 생성
	private String generateToken(Long userId, String role, long expiration) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + expiration);

		return Jwts.builder()
			.setSubject(String.valueOf(userId))
			.claim("role", role)
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	/**
	 * 토큰 유효성 검증
	 * @param token JWT 토큰
	 * @return 유효 여부
	 */
	public boolean validateToken(String token) {
		if (token == null || token.trim().isEmpty()) {
			log.debug("JWT 토큰이 비어있습니다.");
			return false;
		}

		try {
			Jwts.parser()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);
			return true;
		} catch (ExpiredJwtException e) {
			log.debug("만료된 JWT 토큰입니다.");
			return false;
		} catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
			log.debug("유효하지 않은 JWT 토큰입니다: {}", e.getClass().getSimpleName());
			return false;
		} catch (Exception e) {
			log.error("JWT 토큰 검증 중 예상치 못한 오류: {}", e.getMessage());
			return false;
		}
	}

	/**
	 * 토큰에서 사용자 ID 추출
	 * @param token JWT 토큰
	 * @return 사용자 ID
	 */
	public Long getUserIdFromToken(String token) {
		try {
			Claims claims = getClaims(token);
			return Long.parseLong(claims.getSubject());
		} catch (NumberFormatException e) {
			log.error("토큰에서 유효하지 않은 사용자 ID를 추출했습니다: {}", e.getMessage());
			throw new IllegalArgumentException("Invalid user ID in token", e);
		}
	}

	/**
	 * 토큰에서 사용자 권한 추출
	 * @param token JWT 토큰
	 * @return 사용자 권한
	 */
	public String getRoleFromToken(String token) {
		Claims claims = getClaims(token);
		return claims.get("role", String.class);
	}

	// JWT 토큰에서 Claims 추출
	private Claims getClaims(String token) {
		try {
			return Jwts.parser()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
		} catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
			log.debug("토큰에서 Claims 추출 실패: {}", e.getClass().getSimpleName());
			throw e;
		}
	}
}
