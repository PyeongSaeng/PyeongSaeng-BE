package com.umc.pyeongsaeng.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umc.pyeongsaeng.domain.auth.dto.AuthRequest;
import com.umc.pyeongsaeng.domain.auth.dto.AuthResponse;
import com.umc.pyeongsaeng.domain.senior.entity.SeniorProfile;
import com.umc.pyeongsaeng.domain.senior.enums.ExperiencePeriod;
import com.umc.pyeongsaeng.domain.senior.enums.Gender;
import com.umc.pyeongsaeng.domain.senior.enums.JobType;
import com.umc.pyeongsaeng.domain.senior.repository.SeniorProfileRepository;
import com.umc.pyeongsaeng.domain.token.dto.TokenResponse;
import com.umc.pyeongsaeng.domain.token.service.TokenService;
import com.umc.pyeongsaeng.domain.user.entity.SocialAccount;
import com.umc.pyeongsaeng.domain.user.entity.User;
import com.umc.pyeongsaeng.domain.user.enums.Role;
import com.umc.pyeongsaeng.domain.user.enums.UserStatus;
import com.umc.pyeongsaeng.domain.user.repository.SocialAccountRepository;
import com.umc.pyeongsaeng.domain.user.repository.UserRepository;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceCommandImpl implements AuthServiceCommand {

	private static final String KAKAO_PROVIDER = "KAKAO";
	private static final int MAX_SENIOR_COUNT = 3;

	private final UserRepository userRepository;
	private final SocialAccountRepository socialAccountRepository;
	private final TokenService tokenService;
	private final SeniorProfileRepository seniorProfileRepository;
	private final PasswordEncoder passwordEncoder;

	// 로그인
	@Override
	public AuthResponse.LoginResponseDto login(AuthRequest.LoginRequestDto request) {
		User user = validateAndGetUser(request.getUsername(), request.getPassword());
		return createLoginResponse(user, false);
	}

	// 보호자 회원가입
	@Override
	public AuthResponse.LoginResponseDto signupProtector(AuthRequest.ProtectorSignupRequestDto request) {
		validateProtectorSignup(request);

		User savedUser = createUser(
			extractUsername(request),
			extractPassword(request),
			request.getName(),
			request.getPhone(),
			Role.PROTECTOR
		);

		// 카카오면 소셜 계정 추가
		if (isKakaoProvider(request.getProviderType()) && request.getProviderUserId() != null) {
			createSocialAccount(savedUser, request.getProviderUserId());
		}

		return createLoginResponse(savedUser, true);
	}

	// 시니어 회원가입
	@Override
	public AuthResponse.LoginResponseDto signupSenior(AuthRequest.SeniorSignupRequestDto request) {
		validateSeniorSignup(request);

		User savedUser = createUser(
			extractUsername(request),
			extractPassword(request),
			request.getName(),
			request.getPhoneNum(),
			Role.SENIOR
		);

		// 카카오면 소셜 계정 추가
		if (isKakaoProvider(request.getProviderType()) && request.getProviderUserId() != null) {
			createSocialAccount(savedUser, request.getProviderUserId());
		}

		// protectorId가 있다면 보호자 유효성 검증 및 연결
		User protector = validateProtectorIfExists(request.getProtectorId());
		createSeniorProfile(savedUser, protector, request);

		return createLoginResponse(savedUser, true);
	}

	// 로그아웃
	@Override
	public void logout(Long userId) {
		tokenService.deleteRefreshToken(userId);
	}

	// 로그아웃 시 전달할 쿠키 제거 명령 문자열 생성
	@Override
	public String getLogoutCookie() {
		return tokenService.deleteRefreshTokenCookie().toString();
	}

	// 로그인 응답 생성 (토큰 + 쿠키)
	private AuthResponse.LoginResponseDto createLoginResponse(User user, boolean isFirstLogin) {
		TokenResponse.TokenInfoResponseDto tokenInfo = tokenService.generateTokenResponse(user, isFirstLogin);

		return AuthResponse.LoginResponseDto.builder()
			.accessToken(tokenInfo.getAccessToken())
			.userId(tokenInfo.getUserId())
			.username(tokenInfo.getUsername())
			.role(tokenInfo.getRole())
			.isFirstLogin(tokenInfo.isFirstLogin())
			.refreshTokenCookie(tokenService.createRefreshTokenCookie(tokenInfo.getRefreshToken()).toString())
			.build();
	}

	// 보호자 회원가입 유효성 검증
	private void validateProtectorSignup(AuthRequest.ProtectorSignupRequestDto request) {
		if (isKakaoProvider(request.getProviderType())) {
			validateKakaoSignup(request.getProviderUserId());
		} else {
			validateGeneralSignup(request.getUsername(), request.getPassword());
		}
		validateDuplicatePhone(request.getPhone());
	}

	// 시니어 회원가입 유효성 검증
	private void validateSeniorSignup(AuthRequest.SeniorSignupRequestDto request) {
		if (isKakaoProvider(request.getProviderType())) {
			validateKakaoSignup(request.getProviderUserId());
		} else {
			validateGeneralSignup(request.getUsername(), request.getPassword());
		}
		validateDuplicatePhone(request.getPhoneNum());
	}

	// 카카오 회원가입 유효성 검증
	private void validateKakaoSignup(String providerUserId) {
		if (providerUserId == null || providerUserId.isEmpty()) {
			throw new GeneralException(ErrorStatus.INVALID_KAKAO_ID);
		}
		if (socialAccountRepository.existsByProviderTypeAndProviderUserId(KAKAO_PROVIDER, providerUserId)) {
			throw new GeneralException(ErrorStatus.KAKAO_ALREADY_REGISTERED);
		}
	}

	// 일반 회원가입 유효성 검증
	private void validateGeneralSignup(String username, String password) {
		if (password == null || password.isEmpty()) {
			throw new GeneralException(ErrorStatus.INVALID_PASSWORD);
		}
		if (userRepository.existsByUsername(username)) {
			throw new GeneralException(ErrorStatus.USERNAME_DUPLICATED);
		}
	}

	// 전화번호 중복 검증
	private void validateDuplicatePhone(String phone) {
		if (userRepository.existsByPhone(phone)) {
			throw new GeneralException(ErrorStatus.PHONE_DUPLICATED);
		}
	}

	// 사용자 인증 검증
	private User validateAndGetUser(String username, String password) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(ErrorStatus.LOGIN_FAILED));

		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new GeneralException(ErrorStatus.LOGIN_FAILED);
		}

		return user;
	}

	// 보호자 존재 및 시니어 수 제한 검증
	private User validateProtectorIfExists(Long protectorId) {
		if (protectorId == null) {
			return null;
		}

		User protector = userRepository.findById(protectorId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.PROTECTOR_NOT_FOUND));

		if (!protector.getRole().equals(Role.PROTECTOR)) {
			throw new GeneralException(ErrorStatus.INVALID_PROTECTOR_ROLE);
		}

		long currentSeniorCount = seniorProfileRepository.countByProtectorId(protectorId);
		if (currentSeniorCount >= MAX_SENIOR_COUNT) {
			throw new GeneralException(ErrorStatus.PROTECTOR_SENIOR_LIMIT_EXCEEDED);
		}

		return protector;
	}

	// 사용자 생성
	private User createUser(String username, String password, String name, String phone, Role role) {
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

	// 소셜 계정 생성 및 저장
	private void createSocialAccount(User user, String providerUserId) {
		SocialAccount socialAccount = SocialAccount.builder()
			.user(user)
			.providerType(KAKAO_PROVIDER)
			.providerUserId(providerUserId)
			.build();
		socialAccountRepository.save(socialAccount);
	}

	// 시니어 프로필 생성 및 저장
	private void createSeniorProfile(User senior, User protector, AuthRequest.SeniorSignupRequestDto request) {
		SeniorProfile seniorProfile = SeniorProfile.builder()
			.senior(senior)
			.protector(protector)
			.relation(request.getRelation())
			.age(request.getAge())
			.gender(request.getGender() != null ? Gender.valueOf(request.getGender()) : null)
			.phoneNum(request.getPhoneNum())
			.zipcode(request.getZipcode())
			.roadAddress(request.getRoadAddress())
			.detailAddress(request.getDetailAddress())
			.job(request.getJob() != null ? JobType.valueOf(request.getJob()) : null)
			.experiencePeriod(request.getExperiencePeriod() != null ?
				ExperiencePeriod.valueOf(request.getExperiencePeriod()) : null)
			.build();
		seniorProfileRepository.save(seniorProfile);
	}

	// providerType이 KAKAO인지 확인
	private boolean isKakaoProvider(String providerType) {
		return KAKAO_PROVIDER.equalsIgnoreCase(providerType);
	}

	// 회원가입 요청에서 username 추출 (위는 보호자, 아래는 시니어)
	private String extractUsername(AuthRequest.ProtectorSignupRequestDto request) {
		return isKakaoProvider(request.getProviderType()) ?
			request.getProviderUserId() : request.getUsername();
	}

	private String extractUsername(AuthRequest.SeniorSignupRequestDto request) {
		return isKakaoProvider(request.getProviderType()) ?
			request.getProviderUserId() : request.getUsername();
	}

	// 회원가입 요청에서 password 추출 (위는 보호자, 아래는 시니어)
	private String extractPassword(AuthRequest.ProtectorSignupRequestDto request) {
		return isKakaoProvider(request.getProviderType()) ? null : request.getPassword();
	}

	private String extractPassword(AuthRequest.SeniorSignupRequestDto request) {
		return isKakaoProvider(request.getProviderType()) ? null : request.getPassword();
	}
}
