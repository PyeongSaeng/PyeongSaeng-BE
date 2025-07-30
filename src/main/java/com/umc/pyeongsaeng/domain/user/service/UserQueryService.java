package com.umc.pyeongsaeng.domain.user.service;

import java.util.List;

import com.umc.pyeongsaeng.domain.user.dto.UserResponse;

public interface UserQueryService {
	/**
	 * 보호자 정보 조회
	 * @param userId 사용자 ID
	 * @return 보호자 정보
	 */
	UserResponse.ProtectorInfoDto getProtectorInfo(Long userId);

	/**
	 * 시니어 정보 조회
	 * @param userId 사용자 ID
	 * @return 시니어 정보
	 */
	UserResponse.SeniorInfoDto getSeniorInfo(Long userId);

	/**
	 * 특정 보호자와 연결된 시니어 목록 조회
	 * @param protectorId 보호자 ID
	 * @return 연결된 시니어 목록
	 */
	List<UserResponse.ConnectedSeniorDto> getConnectedSeniors(Long protectorId);
}
