package com.umc.pyeongsaeng.global.util;

import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.umc.pyeongsaeng.global.config.JwtConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUtil {

	private final JwtConfig jwtConfig;

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
	}

	public String generateAccessToken(Long userId) {
		Date now = new Date();
		Date expiration = new Date(now.getTime() + jwtConfig.getAccessTokenExpiration());

		return Jwts.builder()
			.subject(userId.toString())
			.issuedAt(now)
			.expiration(expiration)
			.signWith(getSigningKey(), SignatureAlgorithm.HS256)
			.compact();
	}

	public String generateRefreshToken(Long userId) {
		Date now = new Date();
		Date expiration = new Date(now.getTime() + jwtConfig.getRefreshTokenExpiration());

		return Jwts.builder()
			.subject(userId.toString())
			.issuedAt(now)
			.expiration(expiration)
			.signWith(getSigningKey(), SignatureAlgorithm.HS256)
			.compact();
	}

	public Claims parseClaims(String token) {
		return Jwts.parser()
			.setSigningKey(getSigningKey())
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	public boolean isTokenValid(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Long getUserIdFromToken(String token) {
		Claims claims = parseClaims(token);
		return Long.parseLong(claims.getSubject());
	}

	public boolean isTokenExpired(String token) {
		try {
			Claims claims = parseClaims(token);
			return claims.getExpiration().before(new Date());
		} catch (Exception e) {
			return true;
		}
	}
}
