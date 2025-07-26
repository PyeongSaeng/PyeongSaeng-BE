package com.umc.pyeongsaeng.global.filter;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.umc.pyeongsaeng.global.config.SecurityConfig;
import com.umc.pyeongsaeng.global.security.CustomUserDetailsService;
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
	private final CustomUserDetailsService customUserDetailsService;
	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String token = resolveToken(request);

		if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
			try {
				Long id = jwtUtil.getUserIdFromToken(token);
				String role = jwtUtil.getRoleFromToken(token);

				UserDetails userDetails;

				// role에 따라 User 또는 Company 조회
				if ("COMPANY".equals(role)) {
					userDetails = customUserDetailsService.loadCompanyById(id);
				} else {
					userDetails = customUserDetailsService.loadUserById(id);
				}

				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					userDetails,
					null,
					userDetails.getAuthorities()
				);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (Exception e) {
				log.error("인증 정보 설정 실패: {}", e.getMessage());
				SecurityContextHolder.clearContext();
			}
		}

		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(BEARER_PREFIX.length());
		}

		return null;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getRequestURI();

		return Arrays.stream(SecurityConfig.PUBLIC_ENDPOINTS)
			.anyMatch(pattern -> pathMatcher.match(pattern, path));
	}
}
