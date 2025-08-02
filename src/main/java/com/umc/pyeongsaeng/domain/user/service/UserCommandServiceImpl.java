package com.umc.pyeongsaeng.domain.user.service;

import java.time.*;
import java.util.*;

import org.springframework.scheduling.annotation.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import com.umc.pyeongsaeng.domain.application.repository.*;
import com.umc.pyeongsaeng.domain.senior.entity.*;
import com.umc.pyeongsaeng.domain.senior.repository.*;
import com.umc.pyeongsaeng.domain.sms.service.*;
import com.umc.pyeongsaeng.domain.terms.repository.*;
import com.umc.pyeongsaeng.domain.token.repository.*;
import com.umc.pyeongsaeng.domain.token.service.*;
import com.umc.pyeongsaeng.domain.user.dto.*;
import com.umc.pyeongsaeng.domain.user.entity.*;
import com.umc.pyeongsaeng.domain.user.enums.*;
import com.umc.pyeongsaeng.domain.user.repository.*;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.*;
import com.umc.pyeongsaeng.global.apiPayload.code.status.*;

import lombok.*;
import lombok.extern.slf4j.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserCommandServiceImpl implements UserCommandService {

	private static final int WITHDRAWAL_GRACE_DAYS = 7;

	private final UserRepository userRepository;
	private final TokenService tokenService;
	private final ApplicationRepository applicationRepository;
	private final SocialAccountRepository socialAccountRepository;
	private final UserTermsRepository userTermsRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final SeniorProfileRepository seniorProfileRepository;
	private final PasswordEncoder passwordEncoder;
	private final SmsService smsService;

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

	// 보호자 정보 업데이트
	@Override
	public UserResponse.ProtectorInfoDto updateProtectorInfo(Long userId, UserRequest.UpdateProtectorDto request) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		validateActiveUser(user);
		validateProtectorRole(user);

		user.updateBasicInfo(request.getName(), request.getPhone());

		if (request.isPasswordChangeRequested()) {
			validateCurrentPassword(user, request.getCurrentPassword());
			user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
		}

		return UserResponse.ProtectorInfoDto.from(user);
	}

	// 시니어 정보 업데이트
	@Override
	public UserResponse.SeniorInfoDto updateSeniorInfo(Long userId, UserRequest.UpdateSeniorDto request) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		validateActiveUser(user);
		validateSeniorRole(user);

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
			.ifPresent(profile -> seniorProfileRepository.delete(profile));

		List<SeniorProfile> protectedProfiles = seniorProfileRepository.findByProtectorId(userId);
		for (SeniorProfile profile : protectedProfiles) {
			profile.removeProtector();
			seniorProfileRepository.save(profile);
		}
	}

	// 비밀번호 찾기 (새 비밀번호 변경) 전 인증단계
	@Override
	public UserResponse.UsernameDto verifyResetPasswordCode(UserRequest.PasswordVerificationDto request) {
		smsService.verifyCode(request.getPhone(), request.getVerificationCode());

		User user = userRepository.findByUsernameAndPhone(request.getUsername(), request.getPhone())
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		validateActiveUser(user);

		return UserResponse.UsernameDto.from(user);
	}

	// 비밀번호 찾기 (새 비밀번호 변경)
	@Override
	public void resetPassword(UserRequest.PasswordChangeDto request) {
		User user = userRepository.findByUsername(request.getUsername())
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		validateActiveUser(user);

		user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
	}

	private void validateWithdrawalIntent(boolean confirmed) {
		if (!confirmed) {
			throw new GeneralException(ErrorStatus.USER_WITHDRAWAL_NOT_CONFIRMED);
		}
	}

	private void validateActiveUser(User user) {
		if (user.getStatus() != UserStatus.ACTIVE) {
			throw new GeneralException(ErrorStatus.ALREADY_WITHDRAWN_USER);
		}
	}

	private void validateProtectorRole(User user) {
		if (user.getRole() != Role.PROTECTOR) {
			throw new GeneralException(ErrorStatus.INVALID_USER_ROLE);
		}
	}

	private void validateSeniorRole(User user) {
		if (user.getRole() != Role.SENIOR) {
			throw new GeneralException(ErrorStatus.INVALID_USER_ROLE);
		}
	}

	private void validateCurrentPassword(User user, String currentPassword) {
		if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
			throw new GeneralException(ErrorStatus.INVALID_PASSWORD);
		}
	}
}
