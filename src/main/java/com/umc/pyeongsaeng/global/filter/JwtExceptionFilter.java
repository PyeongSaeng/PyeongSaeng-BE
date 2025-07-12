package com.umc.pyeongsaeng.global.filter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException e) {
			log.error("JWT 토큰 만료: {}", e.getMessage());
			setErrorResponse(response, ErrorStatus.EXPIRED_ACCESS_TOKEN);
		} catch (MalformedJwtException e) {
			log.error("잘못된 JWT 토큰 형식: {}", e.getMessage());
			setErrorResponse(response, ErrorStatus.INVALID_TOKEN_FORMAT);
		} catch (SignatureException e) {
			log.error("JWT 서명 검증 실패: {}", e.getMessage());
			setErrorResponse(response, ErrorStatus.INVALID_TOKEN_SIGNATURE);
		} catch (Exception e) {
			log.error("JWT 처리 중 오류 발생: {}", e.getMessage());
			setErrorResponse(response, ErrorStatus.JWT_PROCESSING_ERROR);
		}
	}

	// JWT 인증 실패 시 에러 응답 설정
	private void setErrorResponse(HttpServletResponse response, ErrorStatus errorStatus) throws IOException {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json;charset=UTF-8");

		ApiResponse<Object> errorResponse = ApiResponse.onFailure(
			errorStatus.getCode(),
			errorStatus.getMessage(),
			null
		);

		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}
