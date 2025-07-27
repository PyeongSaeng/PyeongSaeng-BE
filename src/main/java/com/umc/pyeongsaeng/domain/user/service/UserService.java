package com.umc.pyeongsaeng.domain.user.service;

import java.util.List;

import com.umc.pyeongsaeng.domain.user.dto.UserRequest;
import com.umc.pyeongsaeng.domain.user.dto.UserResponse;

public interface UserService {

	/**
	 * 회원 탈퇴
	 * @param userId 사용자 ID
	 * @param confirmed 탈퇴 의도 확인
	 */
	void withdrawUser(Long userId, boolean confirmed);

	/**
	 * 회원 탈퇴 취소 (복구)
	 * @param username 사용자명
	 */
	void cancelWithdrawal(String username);

	/**
	 * 보호자 회원 정보를 업데이트
	 * @param userId 업데이트할 보호자 ID
	 * @param request 업데이트할 보호자 정보
	 * @return 업데이트된 보호자 정보
	 */
	UserResponse.ProtectorInfoDto updateProtectorInfo(Long userId, UserRequest.UpdateProtectorDto request);

	/**
	 * 시니어 회원 정보를 업데이트
	 * @param userId 업데이트할 시니어 ID
	 * @param request 업데이트할 시니어 정보
	 * @return 업데이트된 시니어 정보
	 */
	UserResponse.SeniorInfoDto updateSeniorInfo(Long userId, UserRequest.UpdateSeniorDto request);

	/**
	 * 특정 보호자와 연결된 시니어 목록을 조회
	 * @param protectorId 보호자 회원 ID
	 * @return 연결된 시니어 정보 목록
	 */
	List<UserResponse.ConnectedSeniorDto> getConnectedSeniors(Long protectorId);

	UserResponse.ProtectorInfoDto getProtectorInfo(Long userId);

	UserResponse.SeniorInfoDto getSeniorInfo(Long userId);
}
