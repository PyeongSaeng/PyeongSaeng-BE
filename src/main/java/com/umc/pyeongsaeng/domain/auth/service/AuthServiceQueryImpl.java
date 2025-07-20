package com.umc.pyeongsaeng.domain.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umc.pyeongsaeng.domain.user.repository.UserRepository;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceQueryImpl implements AuthServiceQuery {

	private final UserRepository userRepository;

	// 아이디 중복 확인
	@Override
	public void checkUsernameAvailability(String username) {
		if (userRepository.existsByUsername(username)) {
			throw new GeneralException(ErrorStatus.USERNAME_DUPLICATED);
		}
	}
}
