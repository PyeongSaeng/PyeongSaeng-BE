package com.umc.pyeongsaeng.global.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.umc.pyeongsaeng.domain.user.entity.User;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;
import com.umc.pyeongsaeng.global.security.CustomUserDetails;

@Component
public class AuthUtil {

	/**
	 * 현재 인증된 사용자 ID 조회
	 * @return 사용자 ID
	 */
	public Long getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new GeneralException(ErrorStatus._UNAUTHORIZED);
		}

		if (authentication.getPrincipal() instanceof CustomUserDetails) {
			CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
			return userDetails.getUserId();
		}

		throw new GeneralException(ErrorStatus.INVALID_AUTH_TOKEN);
	}

	/**
	 * 현재 인증된 사용자 정보 조회
	 * @return CustomUserDetails
	 */
	public CustomUserDetails getCurrentUserDetails() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new GeneralException(ErrorStatus._UNAUTHORIZED);
		}

		if (authentication.getPrincipal() instanceof CustomUserDetails) {
			return (CustomUserDetails) authentication.getPrincipal();
		}

		throw new GeneralException(ErrorStatus.INVALID_AUTH_TOKEN);
	}

	/**
	 * 현재 인증된 사용자 엔티티 조회
	 * @return User 엔티티
	 */
	public User getCurrentUser() {
		CustomUserDetails userDetails = getCurrentUserDetails();
		return userDetails.getUser();
	}

	/**
	 * 현재 인증된 사용자의 권한 확인
	 * @param role 확인할 권한
	 * @return 권한 보유 여부
	 */
	public boolean hasRole(String role) {
		try {
			CustomUserDetails userDetails = getCurrentUserDetails();
			return userDetails.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
		} catch (Exception e) {
			return false;
		}
	}
}
