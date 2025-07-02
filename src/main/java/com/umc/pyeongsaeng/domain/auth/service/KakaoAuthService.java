package com.umc.pyeongsaeng.domain.auth.service;

import java.io.IOException;
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

import com.umc.pyeongsaeng.domain.auth.dto.KakaoUserInfoDto;
import com.umc.pyeongsaeng.domain.auth.dto.LoginResponseDto;
import com.umc.pyeongsaeng.domain.auth.dto.SmsVerificationConfirmDto;
import com.umc.pyeongsaeng.domain.auth.entity.SocialAccount;
import com.umc.pyeongsaeng.domain.auth.repository.SocialAccountRepository;
import com.umc.pyeongsaeng.domain.token.service.TokenService;
import com.umc.pyeongsaeng.domain.user.entity.ProtectorSenior;
import com.umc.pyeongsaeng.domain.user.entity.User;
import com.umc.pyeongsaeng.domain.user.entity.enums.Role;
import com.umc.pyeongsaeng.domain.user.entity.enums.Status;
import com.umc.pyeongsaeng.domain.user.repository.ProtectorSeniorRepository;
import com.umc.pyeongsaeng.domain.user.repository.UserRepository;
import com.umc.pyeongsaeng.global.util.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class KakaoAuthService extends DefaultOAuth2UserService implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

	private final UserRepository userRepository;
	private final SocialAccountRepository socialAccountRepository;
	private final ProtectorSeniorRepository protectorSeniorRepository;
	private final JwtUtil jwtUtil;
	private final TokenService tokenService;
	private final SmsService smsService;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);

		KakaoUserInfoDto kakaoUserInfo = extractKakaoUserInfo(oAuth2User);
		log.info("카카오 사용자 정보 - ID: {}, 이메일: {}, 닉네임: {}",
			kakaoUserInfo.getId(), kakaoUserInfo.getEmail(), kakaoUserInfo.getNickname());

		return oAuth2User;
	}

	private KakaoUserInfoDto extractKakaoUserInfo(OAuth2User oAuth2User) {
		Map<String, Object> attributes = oAuth2User.getAttributes();
		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

		return KakaoUserInfoDto.builder()
			.id(Long.parseLong(attributes.get("id").toString()))
			.email((String) kakaoAccount.get("email"))
			.nickname((String) profile.get("nickname"))
			.build();
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		KakaoUserInfoDto kakaoUserInfo = extractKakaoUserInfo(oAuth2User);

		Optional<SocialAccount> existingSocialAccount = socialAccountRepository
			.findByProviderTypeAndProviderUserId("KAKAO", kakaoUserInfo.getId().toString());

		if (existingSocialAccount.isPresent()) {
			User user = existingSocialAccount.get().getUser();
			String accessToken = jwtUtil.generateAccessToken(user.getId());
			String refreshToken = jwtUtil.generateRefreshToken(user.getId());

			tokenService.saveRefreshToken(user.getId(), refreshToken);

			String tempToken = UUID.randomUUID().toString();
			tokenService.saveTempToken(tempToken, accessToken, refreshToken, user.getId());

			String redirectUrl = String.format(
				"http://localhost:3000/auth/callback?tempToken=%s&isFirstLogin=false", tempToken);
			response.sendRedirect(redirectUrl);
		} else {
			request.getSession().setAttribute("kakaoUserInfo", kakaoUserInfo);
			response.sendRedirect("http://localhost:3000/auth/signup/kakao");
		}
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		org.springframework.security.core.AuthenticationException exception)
		throws IOException, ServletException {
		log.error("카카오 로그인 실패: {}", exception.getMessage());
		response.sendRedirect("http://localhost:3000/login?error=oauth_failed");
	}

	@Transactional
	public LoginResponseDto processKakaoSignup(Long kakaoId, String username, String name, String phone, String role) {

		User user = User.builder()
			.username(username)
			.password(null)
			.name(name)
			.phone(phone)
			.role(Role.valueOf(role.toUpperCase()))
			.status(Status.ACTIVE)
			.build();

		User savedUser = userRepository.save(user);

		SocialAccount socialAccount = SocialAccount.builder()
			.user(savedUser)
			.providerType("KAKAO")
			.providerUserId(kakaoId.toString())
			.build();

		socialAccountRepository.save(socialAccount);

		String accessToken = jwtUtil.generateAccessToken(savedUser.getId());
		String refreshToken = jwtUtil.generateRefreshToken(savedUser.getId());
		tokenService.saveRefreshToken(savedUser.getId(), refreshToken);

		return LoginResponseDto.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.userId(savedUser.getId())
			.username(savedUser.getUsername())
			.role(savedUser.getRole().name())
			.isFirstLogin(true)
			.build();
	}

	public void sendSmsForProtector(String seniorPhone) {
		smsService.sendVerificationCode(seniorPhone);
	}

	public LoginResponseDto confirmSmsAndCompleteSignup(SmsVerificationConfirmDto confirmDto) {
		if (!smsService.verifyCode(confirmDto.getPhone(), confirmDto.getVerificationCode())) {
			throw new RuntimeException("인증번호가 올바르지 않습니다.");
		}

		User protector = userRepository.findById(confirmDto.getUserId())
			.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

		if (!protector.getRole().equals(Role.PROTECTOR)) {
			throw new RuntimeException("보호자만 시니어를 추가할 수 있습니다.");
		}

		long currentSeniorCount = protectorSeniorRepository.countByProtectorId(protector.getId());
		if (currentSeniorCount >= 2) {
			throw new RuntimeException("보호자는 최대 2명의 시니어만 추가할 수 있습니다.");
		}

		User senior = userRepository.findByPhone(confirmDto.getPhone())
			.orElseGet(() -> {
				User newSenior = User.builder()
					.username("senior_" + confirmDto.getPhone())
					.name("시니어")
					.phone(confirmDto.getPhone())
					.role(Role.SENIOR)
					.status(Status.ACTIVE)
					.build();
				return userRepository.save(newSenior);
			});

		ProtectorSenior protectorSenior = ProtectorSenior.builder()
			.protector(protector)
			.senior(senior)
			.relation(confirmDto.getRelation())
			.build();

		protectorSeniorRepository.save(protectorSenior);

		String accessToken = jwtUtil.generateAccessToken(protector.getId());
		String refreshToken = jwtUtil.generateRefreshToken(protector.getId());

		tokenService.saveRefreshToken(protector.getId(), refreshToken);

		return LoginResponseDto.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.userId(protector.getId())
			.username(protector.getUsername())
			.role(protector.getRole().name())
			.isFirstLogin(true)
			.build();
	}
}
