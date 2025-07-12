package com.umc.pyeongsaeng.domain.auth.service;

public interface AuthServiceQuery {

	/**
	 * 아이디 중복 확인
	 * @param username 확인할 아이디
	 * @return 사용 가능 여부
	 */
	boolean isUsernameAvailable(String username);
}
