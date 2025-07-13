package com.umc.pyeongsaeng.domain.auth.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umc.pyeongsaeng.domain.auth.dto.AuthResponse;
import com.umc.pyeongsaeng.domain.token.dto.TokenResponse;
import com.umc.pyeongsaeng.domain.token.service.TokenService;
import com.umc.pyeongsaeng.domain.user.entity.SocialAccount;
import com.umc.pyeongsaeng.domain.user.entity.User;
import com.umc.pyeongsaeng.domain.user.repository.SocialAccountRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService extends DefaultOAuth2UserService
	implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

	private static final String KAKAO_PROVIDER = "KAKAO";
	private static final String REDIRECT_URL_FORMAT = "http://localhost:3000/auth/callback?code=%s&isFirstLogin=%s";
	private static final String SIGNUP_REDIRECT_URL_FORMAT = "http://localhost:3000/auth/signup/kakao?kakaoId=%s&nickname=%s";
	private static final String ERROR_REDIRECT_URL = "http://localhost:3000/login?error=oauth_failed";

	private final SocialAccountRepository socialAccountRepository;
	private final TokenService tokenService;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		return super.loadUser(userRequest);
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		AuthResponse.KakaoUserInfoResponseDto kakaoUserInfoResponseDto = extractKakaoUserInfo(oAuth2User);

		Optional<SocialAccount> existingSocialAccount = socialAccountRepository
			.findByProviderTypeAndProviderUserId(KAKAO_PROVIDER, kakaoUserInfoResponseDto.getId().toString());

		if (existingSocialAccount.isPresent()) {
			handleExistingUser(response, existingSocialAccount.get().getUser());
		} else {
			handleNewUser(response, kakaoUserInfoResponseDto);
		}
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		org.springframework.security.core.AuthenticationException exception)
		throws IOException, ServletException {

		log.error("OAuth authentication failed: {}", exception.getMessage());
		response.sendRedirect(ERROR_REDIRECT_URL);
	}

	// 카카오 사용자 정보 추출
	private AuthResponse.KakaoUserInfoResponseDto extractKakaoUserInfo(OAuth2User oAuth2User) {
		Map<String, Object> attributes = oAuth2User.getAttributes();
		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

		return AuthResponse.KakaoUserInfoResponseDto.builder()
			.id(Long.parseLong(attributes.get("id").toString()))
			.email((String) kakaoAccount.get("email"))
			.nickname((String) profile.get("nickname"))
			.build();
	}

	// 기존 소셜 계정이 있으면 기존 사용자 로그인 처리
	private void handleExistingUser(HttpServletResponse response, User user) throws IOException {
		TokenResponse.TokenInfoResponseDto loginResponse = tokenService.generateTokenResponse(user, false);

		String authCode = UUID.randomUUID().toString();
		tokenService.saveAuthorizationCode(authCode, loginResponse);

		String redirectUrl = String.format(REDIRECT_URL_FORMAT, authCode, false);
		response.sendRedirect(redirectUrl);
	}

	// 신규 소셜 계정이면 회원가입 페이지로 리다이렉트
	private void handleNewUser(HttpServletResponse response, AuthResponse.KakaoUserInfoResponseDto kakaoUserInfoResponseDto)
		throws IOException {

		String nickname = kakaoUserInfoResponseDto.getNickname() != null ?
			URLEncoder.encode(kakaoUserInfoResponseDto.getNickname(), StandardCharsets.UTF_8) : "";

		String redirectUrl = String.format(SIGNUP_REDIRECT_URL_FORMAT,
			kakaoUserInfoResponseDto.getId(), nickname);

		response.sendRedirect(redirectUrl);
	}
}
