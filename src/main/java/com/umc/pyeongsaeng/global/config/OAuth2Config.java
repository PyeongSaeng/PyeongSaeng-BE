package com.umc.pyeongsaeng.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.umc.pyeongsaeng.domain.auth.service.OAuth2AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class OAuth2Config {
	private final OAuth2AuthService OAuth2AuthService;

	/**
	 * OAuth2 사용자 서비스 커스터마이징
	 * @return 커스텀 OAuth2UserService
	 */
	@Bean
	public DefaultOAuth2UserService customOAuth2UserService() {
		return OAuth2AuthService;
	}

	/**
	 * OAuth2 로그인 성공 핸들러
	 * @return 성공 핸들러
	 */
	@Bean
	public AuthenticationSuccessHandler oauth2SuccessHandler() {
		return OAuth2AuthService;
	}

	/**
	 * OAuth2 로그인 실패 핸들러
	 * @return 실패 핸들러
	 */
	@Bean
	public AuthenticationFailureHandler oauth2FailureHandler() {
		return OAuth2AuthService;
	}
}
