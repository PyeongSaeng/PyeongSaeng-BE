package com.umc.pyeongsaeng.global.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.umc.pyeongsaeng.domain.company.entity.Company;
import com.umc.pyeongsaeng.domain.user.entity.User;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;
import com.umc.pyeongsaeng.global.security.CustomUserDetails;

@Component
public class AuthUtil {

	/**
	 * 현재 인증된 계정 ID 조회 (User 또는 Company)
	 * @return ID
	 */
	public Long getCurrentId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new GeneralException(ErrorStatus._UNAUTHORIZED);
		}

		if (authentication.getPrincipal() instanceof CustomUserDetails) {
			CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
			return userDetails.getId();
		}

		throw new GeneralException(ErrorStatus.INVALID_AUTH_TOKEN);
	}

	/**
	 * 현재 인증된 회사 ID 조회 (Company만)
	 * @return 회사 ID
	 * @throws GeneralException User 계정인 경우
	 */
	public Long getCurrentCompanyId() {
		CustomUserDetails userDetails = getCurrentUserDetails();
		if (!userDetails.isCompany()) {
			throw new GeneralException(ErrorStatus.INVALID_COMPANY_TYPE);
		}
		return userDetails.getId();
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
	 * @throws GeneralException Company 계정인 경우
	 */
	public User getCurrentUser() {
		CustomUserDetails userDetails = getCurrentUserDetails();
		if (!userDetails.isUser()) {
			throw new GeneralException(ErrorStatus.INVALID_USER_TYPE);
		}
		return userDetails.getUser();
	}

	/**
	 * 현재 인증된 회사 엔티티 조회
	 * @return Company 엔티티
	 * @throws GeneralException User 계정인 경우
	 */
	public Company getCurrentCompany() {
		CustomUserDetails userDetails = getCurrentUserDetails();
		if (!userDetails.isCompany()) {
			throw new GeneralException(ErrorStatus.INVALID_COMPANY_TYPE);
		}
		return userDetails.getCompany();
	}

	/**
	 * 현재 인증된 계정의 권한 확인
	 * @param role 확인할 권한
	 * @return 권한 보유 여부
	 */
	public boolean hasRole(String role) {
		CustomUserDetails userDetails = getCurrentUserDetails();
		return userDetails.getAuthorities().stream()
			.anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
	}

	/**
	 * 현재 계정이 User인지 확인
	 * @return User 여부
	 */
	public boolean isUser() {
		CustomUserDetails userDetails = getCurrentUserDetails();
		return userDetails.isUser();
	}

	/**
	 * 현재 계정이 Company인지 확인
	 * @return Company 여부
	 */
	public boolean isCompany() {
		CustomUserDetails userDetails = getCurrentUserDetails();
		return userDetails.isCompany();
	}
}
