package com.umc.pyeongsaeng.domain.auth.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umc.pyeongsaeng.domain.auth.dto.KakaoUserInfoDto;
import com.umc.pyeongsaeng.domain.auth.dto.LoginRequestDto;
import com.umc.pyeongsaeng.domain.auth.dto.LoginResponseDto;
import com.umc.pyeongsaeng.domain.senior.entity.SeniorProfile;
import com.umc.pyeongsaeng.domain.senior.enums.ExperiencePeriod;
import com.umc.pyeongsaeng.domain.senior.enums.Gender;
import com.umc.pyeongsaeng.domain.senior.enums.JobType;
import com.umc.pyeongsaeng.domain.senior.repository.SeniorProfileRepository;
import com.umc.pyeongsaeng.domain.user.entity.SocialAccount;
import com.umc.pyeongsaeng.domain.user.entity.User;
import com.umc.pyeongsaeng.domain.user.enums.Role;
import com.umc.pyeongsaeng.domain.user.enums.UserStatus;
import com.umc.pyeongsaeng.domain.user.repository.SocialAccountRepository;
import com.umc.pyeongsaeng.domain.user.repository.UserRepository;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;
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
public class AuthService extends DefaultOAuth2UserService
	implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

	private static final String KAKAO_PROVIDER = "KAKAO";
	private static final String REDIRECT_URL_FORMAT = "http://localhost:3000/auth/callback?tempToken=%s&isFirstLogin=%s";
	private static final String SIGNUP_REDIRECT_URL_FORMAT = "http://localhost:3000/auth/signup/kakao?kakaoId=%s&nickname=%s";
	private static final String ERROR_REDIRECT_URL = "http://localhost:3000/login?error=oauth_failed";
	private static final int MAX_SENIOR_COUNT = 3;

	private final UserRepository userRepository;
	private final SocialAccountRepository socialAccountRepository;
	private final JwtUtil jwtUtil;
	private final TokenService tokenService;
	private final SeniorProfileRepository seniorProfileRepository;
	private final PasswordEncoder passwordEncoder;

	public LoginResponseDto login(LoginRequestDto request) {
		User user = validateAndGetUser(request.getUsername(), request.getPassword());
		return generateTokenResponse(user, false);
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);

		KakaoUserInfoDto kakaoUserInfo = extractKakaoUserInfo(oAuth2User);
		log.info("카카오 사용자 정보 - ID: {}, 이메일: {}, 닉네임: {}",
			kakaoUserInfo.getKakaoId(), kakaoUserInfo.getEmail(), kakaoUserInfo.getNickname());

		return oAuth2User;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
		KakaoUserInfoDto kakaoUserInfo = extractKakaoUserInfo(oAuth2User);

		Optional<SocialAccount> existingSocialAccount = socialAccountRepository
			.findByProviderTypeAndProviderUserId(KAKAO_PROVIDER, kakaoUserInfo.getKakaoId().toString());

		if (existingSocialAccount.isPresent()) {
			handleExistingUser(response, existingSocialAccount.get().getUser());
		} else if (existingSocialAccount.isEmpty()) {
			handleNewUser(request, response, kakaoUserInfo);
		}
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		org.springframework.security.core.AuthenticationException exception)
		throws IOException, ServletException {

		log.error("카카오 로그인 실패: {}", exception.getMessage());
		response.sendRedirect(ERROR_REDIRECT_URL);
	}

	@Transactional
	public LoginResponseDto processProtectorSignup(String username, String password, String name,
		String phone, String providerType, String providerUserId) {

		User savedUser = createUser(username, password, name, phone, Role.PROTECTOR);

		if (isKakaoProvider(providerType) && providerUserId != null) {
			createSocialAccount(savedUser, providerUserId);
		}

		return generateTokenResponse(savedUser, true);
	}

	@Transactional
	public LoginResponseDto processSeniorSignup(String username, String password, String name,
		Integer age, String gender, String phoneNum, String zipcode, String roadAddress,
		String detailAddress, String job, String experiencePeriod, Long protectorId,
		String relation, String providerType, String providerUserId) {

		User savedUser = createUser(username, password, name, phoneNum, Role.SENIOR);

		if (isKakaoProvider(providerType) && providerUserId != null) {
			createSocialAccount(savedUser, providerUserId);
		}

		User protector = validateProtectorIfExists(protectorId);
		createSeniorProfileWithDetails(savedUser, protector, relation, age, gender,
			phoneNum, zipcode, roadAddress, detailAddress, job, experiencePeriod);

		return generateTokenResponse(savedUser, true);
	}

	private User validateAndGetUser(String username, String password) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(ErrorStatus.LOGIN_FAILED));

		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new GeneralException(ErrorStatus.LOGIN_FAILED);
		}

		return user;
	}

	private KakaoUserInfoDto extractKakaoUserInfo(OAuth2User oAuth2User) {
		Map<String, Object> attributes = oAuth2User.getAttributes();
		Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>)kakaoAccount.get("profile");

		return KakaoUserInfoDto.builder()
			.kakaoId(Long.parseLong(attributes.get("id").toString()))
			.email((String)kakaoAccount.get("email"))
			.nickname((String)profile.get("nickname"))
			.build();
	}

	private void handleExistingUser(HttpServletResponse response, User user) throws IOException {
		LoginResponseDto loginResponse = generateTokenResponse(user, false);

		String redirectUrl = String.format(REDIRECT_URL_FORMAT,
			URLEncoder.encode(loginResponse.getAccessToken(), StandardCharsets.UTF_8),
			URLEncoder.encode(loginResponse.getRefreshToken(), StandardCharsets.UTF_8),
			loginResponse.getUserId(),
			loginResponse.isFirstLogin());

		response.sendRedirect(redirectUrl);
	}

	private void handleNewUser(HttpServletRequest request, HttpServletResponse response,
		KakaoUserInfoDto kakaoUserInfo) throws IOException {

		String nickname = kakaoUserInfo.getNickname() != null ?
			URLEncoder.encode(kakaoUserInfo.getNickname(), StandardCharsets.UTF_8) : "";

		String redirectUrl = String.format(SIGNUP_REDIRECT_URL_FORMAT,
			kakaoUserInfo.getKakaoId(),
			nickname);

		response.sendRedirect(redirectUrl);
	}

	private void validateSeniorLimit(Long protectorId) {
		long currentSeniorCount = seniorProfileRepository.countByProtectorId(protectorId);
		if (currentSeniorCount >= MAX_SENIOR_COUNT) {
			throw new GeneralException(ErrorStatus.PROTECTOR_SENIOR_LIMIT_EXCEEDED);
		}
	}

	private User createUser(String username, String password, String name,
		String phone, Role role) {
		User.UserBuilder userBuilder = User.builder()
			.username(username)
			.name(name)
			.phone(phone)
			.role(role)
			.status(UserStatus.ACTIVE);

		if (password != null) {
			userBuilder.password(passwordEncoder.encode(password));
		}

		return userRepository.save(userBuilder.build());
	}

	private void createSocialAccount(User user, String providerUserId) {
		SocialAccount socialAccount = SocialAccount.builder()
			.user(user)
			.providerType(KAKAO_PROVIDER)
			.providerUserId(providerUserId)
			.build();
		socialAccountRepository.save(socialAccount);
	}

	private User validateProtectorIfExists(Long protectorId) {
		if (protectorId == null) {
			return null;
		}

		User protector = userRepository.findById(protectorId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.PROTECTOR_NOT_FOUND));

		if (!protector.getRole().equals(Role.PROTECTOR)) {
			throw new GeneralException(ErrorStatus.INVALID_PROTECTOR_ROLE);
		}

		validateSeniorLimit(protectorId);
		return protector;
	}

	private void createSeniorProfileWithDetails(User senior, User protector, String relation,
		Integer age, String gender, String phone, String zipcode, String roadAddress,
		String detailAddress, String job, String experiencePeriod) {

		SeniorProfile seniorProfile = SeniorProfile.builder()
			.senior(senior)
			.protector(protector)
			.relation(relation)
			.age(age)
			.gender(gender != null ? Gender.valueOf(gender) : null)
			.phoneNum(phone)
			.zipcode(zipcode)
			.roadAddress(roadAddress)
			.detailAddress(detailAddress)
			.job(job != null ? JobType.valueOf(job) : null)
			.experiencePeriod(experiencePeriod != null ? ExperiencePeriod.valueOf(experiencePeriod) : null)
			.build();
		seniorProfileRepository.save(seniorProfile);
	}

	private LoginResponseDto generateTokenResponse(User user, boolean isFirstLogin) {
		String accessToken = jwtUtil.generateAccessToken(user.getId());
		String refreshToken = jwtUtil.generateRefreshToken(user.getId());

		tokenService.saveRefreshToken(user.getId(), refreshToken);

		return LoginResponseDto.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.userId(user.getId())
			.username(user.getUsername())
			.role(user.getRole().name())
			.isFirstLogin(isFirstLogin)
			.build();
	}

	private boolean isKakaoProvider(String providerType) {
		return KAKAO_PROVIDER.equalsIgnoreCase(providerType);
	}
}
