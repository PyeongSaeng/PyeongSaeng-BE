package com.umc.pyeongsaeng.domain.user.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umc.pyeongsaeng.domain.application.repository.ApplicationRepository;
import com.umc.pyeongsaeng.domain.senior.entity.SeniorProfile;
import com.umc.pyeongsaeng.domain.senior.repository.SeniorProfileRepository;
import com.umc.pyeongsaeng.domain.terms.repository.UserTermsRepository;
import com.umc.pyeongsaeng.domain.token.repository.RefreshTokenRepository;
import com.umc.pyeongsaeng.domain.token.service.TokenService;
import com.umc.pyeongsaeng.domain.user.entity.User;
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

	// confirmed로 의도 확인 후 UserStatus WITHDRAWN으로 변경
	@Override
	public void withdrawUser(Long userId, boolean confirmed) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		if (user.getStatus() == UserStatus.WITHDRAWN) {
			throw new GeneralException(ErrorStatus.ALREADY_WITHDRAWN_USER);
		}

		validateWithdrawalIntent(confirmed);

		user.setStatus(UserStatus.WITHDRAWN);
		user.setWithdrawnAt(LocalDateTime.now());

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

		user.setStatus(UserStatus.ACTIVE);
		user.setWithdrawnAt(null);
	}

	// 연관 데이터, 사용자 데이터 모두 삭제
	@Scheduled(cron = "0 30 0 * * *")
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
			profile.setProtector(null);
			seniorProfileRepository.save(profile);
		}
	}
}
