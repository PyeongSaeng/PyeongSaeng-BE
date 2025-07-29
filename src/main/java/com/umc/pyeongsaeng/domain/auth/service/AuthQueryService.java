package com.umc.pyeongsaeng.domain.auth.service;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;


public interface AuthQueryService {

	/**
	 * 아이디 중복 확인
	 * @param username 확인할 아이디
	 * @throws GeneralException 이미 사용중인 아이디인 경우
	 */
	void checkUsernameAvailability(String username);
}
