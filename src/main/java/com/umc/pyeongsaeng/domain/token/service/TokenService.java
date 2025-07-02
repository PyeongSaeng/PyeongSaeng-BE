package com.umc.pyeongsaeng.domain.token.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umc.pyeongsaeng.domain.auth.dto.LoginResponseDto;
import com.umc.pyeongsaeng.domain.token.entity.RefreshToken;
import com.umc.pyeongsaeng.domain.token.repository.RefreshTokenRepository;
import com.umc.pyeongsaeng.domain.user.entity.User;
import com.umc.pyeongsaeng.domain.user.repository.UserRepository;
import com.umc.pyeongsaeng.global.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenService {

	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;
	private final RedisTemplate<String, Object> redisTemplate;

	public void saveRefreshToken(Long userId, String refreshToken) {
		refreshTokenRepository.deleteByUser_Id(userId);

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

		RefreshToken token = RefreshToken.builder()
			.user(user)
			.refreshToken(refreshToken)
			.issuedAt(LocalDateTime.now())
			.expiresAt(LocalDateTime.now().plusDays(14))
			.build();

		refreshTokenRepository.save(token);
	}

	public String refreshAccessToken(String refreshToken) {
		RefreshToken storedToken = refreshTokenRepository.findByRefreshToken(refreshToken)
			.orElseThrow(() -> new RuntimeException("유효하지 않은 리프레시 토큰입니다."));

		if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
			refreshTokenRepository.delete(storedToken);
			throw new RuntimeException("만료된 리프레시 토큰입니다.");
		}

		Long userId = storedToken.getUser().getId();
		return jwtUtil.generateAccessToken(userId);
	}

	public void deleteRefreshToken(Long userId) {
		refreshTokenRepository.deleteByUser_Id(userId);
	}

	public boolean isValidRefreshToken(String refreshToken) {
		return refreshTokenRepository.findByRefreshToken(refreshToken)
			.map(token -> token.getExpiresAt().isAfter(LocalDateTime.now()))
			.orElse(false);
	}

	public void saveTempToken(String tempToken, String accessToken, String refreshToken, Long userId) {
		Map<String, Object> tokenData = Map.of(
			"accessToken", accessToken,
			"refreshToken", refreshToken,
			"userId", userId
		);
		redisTemplate.opsForValue().set(tempToken, tokenData, Duration.ofMinutes(3));
	}

	public LoginResponseDto getTokensByTempToken(String tempToken) {
		Map<String, Object> tokenData = (Map<String, Object>) redisTemplate.opsForValue().get(tempToken);
		if (tokenData == null) return null;

		User user = userRepository.findById(((Number) tokenData.get("userId")).longValue())
			.orElse(null);
		if (user == null) return null;

		return LoginResponseDto.builder()
			.accessToken((String) tokenData.get("accessToken"))
			.refreshToken((String) tokenData.get("refreshToken"))
			.userId(user.getId())
			.username(user.getUsername())
			.role(user.getRole().name())
			.isFirstLogin(false)
			.build();
	}

	public void deleteTempToken(String tempToken) {
		redisTemplate.delete(tempToken);
	}
}
