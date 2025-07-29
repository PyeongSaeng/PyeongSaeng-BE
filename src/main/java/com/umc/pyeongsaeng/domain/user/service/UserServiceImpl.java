package com.umc.pyeongsaeng.domain.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umc.pyeongsaeng.domain.application.repository.ApplicationRepository;
import com.umc.pyeongsaeng.domain.senior.entity.SeniorProfile;
import com.umc.pyeongsaeng.domain.senior.repository.SeniorProfileRepository;
import com.umc.pyeongsaeng.domain.terms.repository.UserTermsRepository;
import com.umc.pyeongsaeng.domain.token.repository.RefreshTokenRepository;
import com.umc.pyeongsaeng.domain.token.service.TokenService;
import com.umc.pyeongsaeng.domain.user.dto.UserRequest;
import com.umc.pyeongsaeng.domain.user.dto.UserResponse;
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
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

	private static final int WITHDRAWAL_GRACE_DAYS = 7;

	private final UserRepository userRepository;
	private final TokenService tokenService;
	private final ApplicationRepository applicationRepository;
	private final SocialAccountRepository socialAccountRepository;
	private final UserTermsRepository userTermsRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final SeniorProfileRepository seniorProfileRepository;
	private final PasswordEncoder passwordEncoder;

	// confirmed로 의도 확인 후 UserStatus WITHDRAWN으로 변경
	@Override
	public void withdrawUser(Long userId, boolean confirmed) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		if (user.getStatus() == UserStatus.WITHDRAWN) {
			throw new GeneralException(ErrorStatus.ALREADY_WITHDRAWN_USER);
		}

		validateWithdrawalIntent(confirmed);

		user.withdraw();
		tokenService.deleteRefreshToken(userId);
	}

	// UserStatus를 Active로 변경
	@Override
	public void cancelWithdrawal(String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		if (user.getStatus() != UserStatus.WITHDRAWN) {
			throw new GeneralException(ErrorStatus.NOT_WITHDRAWN_USER);
		}

		if (user.getWithdrawnAt() != null &&
			user.getWithdrawnAt().plusDays(WITHDRAWAL_GRACE_DAYS).isBefore(LocalDateTime.now())) {
			throw new GeneralException(ErrorStatus.WITHDRAWAL_PERIOD_EXPIRED);
		}

		user.cancelWithdrawal();
	}

	// 연관 데이터, 사용자 데이터 모두 삭제
	@Scheduled(cron = "0 0 3 * * *")
	public void deleteExpiredWithdrawnUsers() {
		LocalDateTime expiryDate = LocalDateTime.now().minusDays(WITHDRAWAL_GRACE_DAYS);

		List<User> expiredUsers = userRepository.findByStatusAndWithdrawnAtBefore(
			UserStatus.WITHDRAWN, expiryDate);

		if (!expiredUsers.isEmpty()) {
			for (User user : expiredUsers) {
				deleteAllRelatedData(user.getId());

				userRepository.delete(user);
				log.info("Permanently deleted expired withdrawn user {}", user.getId());
			}
		}
	}

	// 탈퇴 의도 확인
	private void validateWithdrawalIntent(boolean confirmed) {
		if (!confirmed) {
			throw new GeneralException(ErrorStatus.USER_WITHDRAWAL_NOT_CONFIRMED);
		}
	}

	// 연관 데이터 삭제
	private void deleteAllRelatedData(Long userId) {
		applicationRepository.deleteByApplicantId(userId);
		applicationRepository.deleteBySeniorId(userId);
		socialAccountRepository.deleteByUserId(userId);
		userTermsRepository.deleteByUserId(userId);
		refreshTokenRepository.deleteByUser_Id(userId);
		handleSeniorProfileDeletion(userId);
	}

	// role이 시니어인 경우, 보호자인 경우
	private void handleSeniorProfileDeletion(Long userId) {
		seniorProfileRepository.findBySeniorId(userId)
			.ifPresent(profile -> {
				seniorProfileRepository.delete(profile);
			});

		List<SeniorProfile> protectedProfiles = seniorProfileRepository.findByProtectorId(userId);
		for (SeniorProfile profile : protectedProfiles) {
			profile.removeProtector();
			seniorProfileRepository.save(profile);
		}
	}

	// 보호자 정보 조회
	@Override
	public UserResponse.ProtectorInfoDto getProtectorInfo(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		if (user.getRole() != Role.PROTECTOR) {
			throw new GeneralException(ErrorStatus.INVALID_USER_ROLE);
		}

		return UserResponse.ProtectorInfoDto.from(user);
	}

	// 시니어 정보 조회
	@Override
	public UserResponse.SeniorInfoDto getSeniorInfo(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		if (user.getRole() != Role.SENIOR) {
			throw new GeneralException(ErrorStatus.INVALID_USER_ROLE);
		}

		SeniorProfile seniorProfile = seniorProfileRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.SENIOR_PROFILE_NOT_FOUND));

		return UserResponse.SeniorInfoDto.of(user, seniorProfile);
	}

	// 보호자 정보 업데이트
	@Override
	@Transactional
	public UserResponse.ProtectorInfoDto updateProtectorInfo(Long userId, UserRequest.UpdateProtectorDto request) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		if (user.getStatus() != UserStatus.ACTIVE) {
			throw new GeneralException(ErrorStatus.ALREADY_WITHDRAWN_USER);
		}

		if (user.getRole() != Role.PROTECTOR) {
			throw new GeneralException(ErrorStatus.INVALID_USER_ROLE);
		}

		user.updateBasicInfo(request.getName(), request.getPhone());

		if (request.isPasswordChangeRequested()) {
			validateCurrentPassword(user, request.getCurrentPassword());
			user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
		}
		return UserResponse.ProtectorInfoDto.from(user);
	}

	// 시니어 정보 업데이트
	@Override
	@Transactional
	public UserResponse.SeniorInfoDto updateSeniorInfo(Long userId, UserRequest.UpdateSeniorDto request) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		if (user.getStatus() != UserStatus.ACTIVE) {
			throw new GeneralException(ErrorStatus.ALREADY_WITHDRAWN_USER);
		}

		if (user.getRole() != Role.SENIOR) {
			throw new GeneralException(ErrorStatus.INVALID_USER_ROLE);
		}

		SeniorProfile seniorProfile = seniorProfileRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.SENIOR_PROFILE_NOT_FOUND));

		user.updateBasicInfo(request.getName(), request.getPhone());

		if (request.isPasswordChangeRequested()) {
			validateCurrentPassword(user, request.getCurrentPassword());
			user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
		}

		seniorProfile.updateProfileInfo(
			request.getRoadAddress(),
			request.getDetailAddress(),
			request.getJob(),
			request.getExperiencePeriod()
		);

		return UserResponse.SeniorInfoDto.of(user, seniorProfile);
	}

	// 특정 보호자와 연결된 시니어 목록 조회
	@Override
	public List<UserResponse.ConnectedSeniorDto> getConnectedSeniors(Long protectorId) {
		User protector = userRepository.findById(protectorId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		if (protector.getRole() != Role.PROTECTOR) {
			throw new GeneralException(ErrorStatus.INVALID_USER_ROLE);
		}

		List<SeniorProfile> seniorProfiles = seniorProfileRepository.findByProtectorId(protectorId);

		return seniorProfiles.stream()
			.map(profile -> UserResponse.ConnectedSeniorDto.of(
				profile.getSenior(),
				profile.getPhoneNum(),
				profile.getRelation()
			))
			.collect(Collectors.toList());
	}

	private void validateCurrentPassword(User user, String currentPassword) {
		if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
			throw new GeneralException(ErrorStatus.INVALID_PASSWORD);
		}
	}
}
