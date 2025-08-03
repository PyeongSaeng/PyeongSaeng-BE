package com.umc.pyeongsaeng.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;
import com.umc.pyeongsaeng.global.filter.JwtAuthenticationFilter;
import com.umc.pyeongsaeng.global.filter.JwtExceptionFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final JwtExceptionFilter jwtExceptionFilter;
	private final OAuth2Config oAuth2Config;
	private final ObjectMapper objectMapper;

	public static final String[] PUBLIC_ENDPOINTS = {
		"/api/auth/login",
		"/api/auth/signup/**",
		"/api/auth/kakao/login",
		"/api/auth/check-username",
		"/api/sms/**",
		"/api/token/**",
		"/api/companies/sign-up",
		"/api/companies/login",
		"/api/companies/check-username",
		"/api/companies/withdraw/cancel",
		"/actuator/**",
		"/oauth2/authorization/**",
		"/login/oauth2/**",
		"/swagger-ui/**",
		"/v3/api-docs/**",
		"/swagger-resources/**",
		"/webjars/**",
		"/login",
		"/error",
		"/favicon.ico"
	};

	// CORS 설정
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedOrigin("http://localhost:3000");
		configuration.addAllowedOrigin("http://localhost:5174");
		configuration.addAllowedOrigin("http://localhost:5173");
		configuration.addAllowedOrigin("https://api.pyeongsaeng.site");
		configuration.addAllowedMethod("*");
		configuration.addAllowedHeader("*");
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	// Security 설정
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			// CORS 설정
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))

			.csrf(csrf -> csrf.disable())

			.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)

			.exceptionHandling(exception -> exception
				.authenticationEntryPoint((request, response, authException) -> {
					response.setStatus(HttpStatus.UNAUTHORIZED.value());
					response.setContentType(MediaType.APPLICATION_JSON_VALUE);
					response.setCharacterEncoding("UTF-8");

					ApiResponse<Object> errorResponse = ApiResponse.onFailure(
						ErrorStatus._UNAUTHORIZED.getCode(),
						ErrorStatus._UNAUTHORIZED.getMessage(),
						null
					);

					response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
				})
				.accessDeniedHandler((request, response, accessDeniedException) -> {
					response.setStatus(HttpStatus.FORBIDDEN.value());
					response.setContentType(MediaType.APPLICATION_JSON_VALUE);
					response.setCharacterEncoding("UTF-8");

					ApiResponse<Object> errorResponse = ApiResponse.onFailure(
						ErrorStatus._FORBIDDEN.getCode(),
						ErrorStatus._FORBIDDEN.getMessage(),
						null
					);

					response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
				})
			)

			// 요청별 인증 설정
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(PUBLIC_ENDPOINTS).permitAll()
				.requestMatchers("/api/user/withdraw/cancel").permitAll()
				.requestMatchers("/api/user/protector/**").hasRole("PROTECTOR")
				.requestMatchers("/api/user/senior/**").hasRole("SENIOR")
				.requestMatchers("/api/protector/**").hasRole("PROTECTOR")
				.requestMatchers("/api/senior/**").hasRole("SENIOR")
				.requestMatchers("/api/companies/**").hasRole("COMPANY")
				.requestMatchers("/api/ai/**").permitAll()
				.anyRequest().authenticated()
			)

			// OAuth2 로그인 설정
			.oauth2Login(oauth2 -> oauth2
				.loginPage("/login")
				.redirectionEndpoint(redirection -> redirection
					.baseUri("/login/oauth2/code/*")
				)
				.userInfoEndpoint(userInfo -> userInfo
					.userService(oAuth2Config.customOAuth2UserService())
				)
				.successHandler(oAuth2Config.oauth2SuccessHandler())
				.failureHandler(oAuth2Config.oauth2FailureHandler())
			)

			// JWT 필터
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);

		return http.build();
	}
}
