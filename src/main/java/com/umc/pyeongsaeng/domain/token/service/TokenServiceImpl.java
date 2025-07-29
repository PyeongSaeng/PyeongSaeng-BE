package com.umc.pyeongsaeng.domain.token.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umc.pyeongsaeng.domain.company.entity.Company;
import com.umc.pyeongsaeng.domain.company.repository.CompanyRepository;
import com.umc.pyeongsaeng.domain.token.dto.TokenResponse;
import com.umc.pyeongsaeng.domain.token.entity.RefreshToken;
import com.umc.pyeongsaeng.domain.token.repository.RefreshTokenRepository;
import com.umc.pyeongsaeng.domain.user.entity.User;
import com.umc.pyeongsaeng.domain.user.repository.UserRepository;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;
import com.umc.pyeongsaeng.global.util.CookieUtil;
import com.umc.pyeongsaeng.global.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenServiceImpl implements TokenService {

	private static final String AUTH_CODE_PREFIX = "auth:";
	private static final int AUTH_CODE_EXPIRY_MINUTES = 5;
	private static final int REFRESH_TOKEN_EXPIRY_DAYS = 14;

	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;
	private final CompanyRepository companyRepository;
	private final JwtUtil jwtUtil;
	private final CookieUtil cookieUtil;
	private final RedisTemplate<String, Object> redisTemplate;

	// 기존 거 지우고 Refresh Token 저장
	@Override
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

	// Access Token 갱신
	@Override
	public String refreshAccessToken(String refreshToken) {
		if (!isValidRefreshToken(refreshToken)) {
			throw new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN);
		}

		RefreshToken storedToken = refreshTokenRepository.findByRefreshToken(refreshToken)
			.orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN));

		User user = storedToken.getUser();
		return jwtUtil.generateAccessToken(user.getId(), user.getRole().name());
	}

	// Refresh Token 삭제 (로그아웃)
	@Override
	public void deleteRefreshToken(Long userId) {
		refreshTokenRepository.deleteByUser_Id(userId);
	}

	// Refresh Token 유효성 확인
	@Override
	@Transactional(readOnly = true)
	public boolean isValidRefreshToken(String refreshToken) {
		return refreshTokenRepository.findByRefreshToken(refreshToken)
			.map(token -> token.getExpiresAt().isAfter(LocalDateTime.now()))
			.orElse(false);
	}

	// Authorization Code와 토큰 정보 (Redis에 5분 저장)
	@Override
	public void saveAuthorizationCode(String authCode, TokenResponse.TokenInfoResponseDto tokenInfoResponseDto) {
		Map<String, Object> tokenData = Map.of(
			"accessToken", tokenInfoResponseDto.getAccessToken(),
			"refreshToken", tokenInfoResponseDto.getRefreshToken(),
			"userId", tokenInfoResponseDto.getUserId(),
			"username", tokenInfoResponseDto.getUsername(),
			"role", tokenInfoResponseDto.getRole(),
			"isFirstLogin", tokenInfoResponseDto.isFirstLogin()
		);

		String redisKey = AUTH_CODE_PREFIX + authCode;
		redisTemplate.opsForValue().set(redisKey, tokenData, Duration.ofMinutes(AUTH_CODE_EXPIRY_MINUTES));
	}

	// Authorization Code를 토큰으로 교환
	@Override
	public TokenResponse.TokenInfoResponseDto exchangeAuthorizationCode(String authCode) {
		String redisKey = AUTH_CODE_PREFIX + authCode;
		Map<String, Object> tokenData = (Map<String, Object>) redisTemplate.opsForValue().get(redisKey);

		if (tokenData == null) {
			throw new GeneralException(ErrorStatus.INVALID_AUTH_CODE);
		}

		redisTemplate.delete(redisKey);

		return TokenResponse.TokenInfoResponseDto.builder()
			.accessToken((String) tokenData.get("accessToken"))
			.refreshToken((String) tokenData.get("refreshToken"))
			.userId(((Number) tokenData.get("userId")).longValue())
			.username((String) tokenData.get("username"))
			.role((String) tokenData.get("role"))
			.isFirstLogin((Boolean) tokenData.get("isFirstLogin"))
			.build();
	}

	// JWT 토큰 생성 및 응답 객체 반환
	@Override
	public TokenResponse.TokenInfoResponseDto generateTokenResponse(User user, boolean isFirstLogin) {
		String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole().name());
		String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getRole().name());
		saveRefreshToken(user.getId(), refreshToken);

		return TokenResponse.TokenInfoResponseDto.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.userId(user.getId())
			.username(user.getUsername())
			.role(user.getRole().name())
			.isFirstLogin(isFirstLogin)
			.build();
	}

	@Override
	public ResponseCookie createRefreshTokenCookie(String refreshToken) {
		return cookieUtil.createRefreshTokenCookie(refreshToken);
	}

	@Override
	public ResponseCookie deleteRefreshTokenCookie() {
		return cookieUtil.deleteRefreshTokenCookie();
	}

	// 인증 코드 기반으로 토큰 교환 (쿠키 포함 응답)
	@Override
	public TokenResponse.TokenExchangeResponseDto processTokenExchange(String authCode) {
		TokenResponse.TokenInfoResponseDto tokenInfo = exchangeAuthorizationCode(authCode);

		return TokenResponse.TokenExchangeResponseDto.builder()
			.accessToken(tokenInfo.getAccessToken())
			.userId(tokenInfo.getUserId())
			.username(tokenInfo.getUsername())
			.role(tokenInfo.getRole())
			.isFirstLogin(tokenInfo.isFirstLogin())
			.refreshTokenCookie(createRefreshTokenCookie(tokenInfo.getRefreshToken()).toString())
			.build();
	}

	// 유효한 Refresh 토큰으로 Access 재발급 + 새 Refresh 토큰도 갱신 (쿠키 포함)
	@Override
	public TokenResponse.RefreshTokenResponseDto processTokenRefresh(String refreshToken) {
		if (!isValidRefreshToken(refreshToken)) {
			throw new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN);
		}

		String newAccessToken = refreshAccessToken(refreshToken);

		RefreshToken storedToken = refreshTokenRepository.findByRefreshToken(refreshToken)
			.orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN));

		User user = storedToken.getUser();
		String newRefreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getRole().name());
		saveRefreshToken(user.getId(), newRefreshToken);

		return TokenResponse.RefreshTokenResponseDto.builder()
			.accessToken(newAccessToken)
			.refreshTokenCookie(createRefreshTokenCookie(newRefreshToken).toString())
			.build();
	}

	// Company 전용 기존 거 지우고 Refresh Token 저장
	@Override
	public void saveCompanyRefreshToken(Long companyId, String refreshToken) {
		refreshTokenRepository.deleteByCompany_Id(companyId);

		Company company = companyRepository.findById(companyId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.COMPANY_NOT_FOUND));

		LocalDateTime now = LocalDateTime.now();
		RefreshToken token = RefreshToken.builder()
			.company(company)
			.user(null)
			.refreshToken(refreshToken)
			.issuedAt(now)
			.expiresAt(now.plusDays(REFRESH_TOKEN_EXPIRY_DAYS))
			.build();

		refreshTokenRepository.save(token);
	}
}
