package com.umc.pyeongsaeng.global.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.umc.pyeongsaeng.global.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String token = resolveToken(request);

		// 토큰이 유효한 경우 인증 정보 설정
		if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
			try {
				// 토큰에서 사용자 정보 추출
				Long userId = jwtUtil.getUserIdFromToken(token);
				String role = jwtUtil.getRoleFromToken(token);

				// Spring Security 인증 정보 생성
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					userId,
					null,
					List.of(new SimpleGrantedAuthority("ROLE_" + role))
				);

				// SecurityContext에 인증 정보 저장
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (Exception e) {
				SecurityContextHolder.clearContext();
			}
		}

		filterChain.doFilter(request, response);
	}

	// HTTP 요청에서 JWT 토큰 추출
	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(BEARER_PREFIX.length());
		}

		return null;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		// 인증이 필요 없는 경로는 필터를 적용하지 않음
		String path = request.getRequestURI();

		List<String> excludedPaths = Arrays.asList(
			"/api/auth/login",
			"/api/auth/signup",
			"/api/auth/refresh",
			"/api/auth/exchange-token",
			"/api/auth/check-username",
			"/api/auth/sms",
			"/api/auth/kakao",
			"/oauth2",
			"/login",
			"/swagger-ui",
			"/v3/api-docs"
		);

		return excludedPaths.stream()
			.anyMatch(excludePath -> path.startsWith(excludePath));
	}
}
