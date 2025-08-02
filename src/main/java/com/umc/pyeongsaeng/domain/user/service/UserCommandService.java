package com.umc.pyeongsaeng.domain.user.service;

import com.umc.pyeongsaeng.domain.user.dto.UserRequest;
import com.umc.pyeongsaeng.domain.user.dto.UserResponse;

public interface UserCommandService {
	/**
	 * 사용자 탈퇴 처리
	 * @param userId 사용자 ID
	 * @param confirmed 탈퇴 의사 확인 여부
	 */
	void withdrawUser(Long userId, boolean confirmed);

	/**
	 * 사용자 탈퇴 취소
	 * @param username 사용자 아이디
	 */
	void cancelWithdrawal(String username);

	/**
	 * 보호자 정보 업데이트
	 * @param userId 사용자 ID
	 * @param request 업데이트 요청 정보
	 * @return 업데이트된 보호자 정보
	 */
	UserResponse.ProtectorInfoDto updateProtectorInfo(Long userId, UserRequest.UpdateProtectorDto request);

	/**
	 * 시니어 정보 업데이트
	 * @param userId 사용자 ID
	 * @param request 업데이트 요청 정보
	 * @return 업데이트된 시니어 정보
	 */
	UserResponse.SeniorInfoDto updateSeniorInfo(Long userId, UserRequest.UpdateSeniorDto request);

	/**
	 * 비밀번호 재설정
	 * @param request 비밀번호 변경 요청
	 */
	void resetPassword(UserRequest.PasswordChangeDto request);

	/**
	 * 인증번호를 검증
	 * @param request 인증 요청
	 * @return 사용자 아이디
	 */
	UserResponse.UsernameDto verifyResetPasswordCode(UserRequest.PasswordVerificationDto request);
}
