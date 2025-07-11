package com.umc.pyeongsaeng.domain.auth.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umc.pyeongsaeng.domain.auth.entity.RefreshToken;
import com.umc.pyeongsaeng.domain.auth.repository.RefreshTokenRepository;
import com.umc.pyeongsaeng.domain.user.entity.User;
import com.umc.pyeongsaeng.domain.user.repository.UserRepository;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;
import com.umc.pyeongsaeng.global.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenService {
	private static final int REFRESH_TOKEN_EXPIRY_DAYS = 14;

	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;

	public void saveRefreshToken(Long userId, String refreshToken) {
		refreshTokenRepository.deleteByUser_Id(userId);

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		LocalDateTime now = LocalDateTime.now();
		RefreshToken token = RefreshToken.builder()
			.user(user)
			.refreshToken(refreshToken)
			.issuedAt(now)
			.expiresAt(now.plusDays(REFRESH_TOKEN_EXPIRY_DAYS))
			.build();

		refreshTokenRepository.save(token);
	}

	public String refreshAccessToken(String refreshToken) {
		RefreshToken storedToken = refreshTokenRepository.findByRefreshToken(refreshToken)
			.orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN));

		if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
			refreshTokenRepository.delete(storedToken);
			throw new GeneralException(ErrorStatus.EXPIRED_REFRESH_TOKEN);
		}

		return jwtUtil.generateAccessToken(storedToken.getUser().getId());
	}

	public void deleteRefreshToken(Long userId) {
		refreshTokenRepository.deleteByUser_Id(userId);
	}

	public boolean isValidRefreshToken(String refreshToken) {
		return refreshTokenRepository.findByRefreshToken(refreshToken)
			.map(token -> token.getExpiresAt().isAfter(LocalDateTime.now()))
			.orElse(false);
	}

}
