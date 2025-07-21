package com.umc.pyeongsaeng.domain.user.service;

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
	 * 탈퇴 후 7일 지난 회원 영구 삭제
	 */
	void deleteExpiredWithdrawnUsers();
}
