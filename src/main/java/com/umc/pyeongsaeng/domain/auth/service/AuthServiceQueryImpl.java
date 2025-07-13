package com.umc.pyeongsaeng.domain.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umc.pyeongsaeng.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceQueryImpl implements AuthServiceQuery {

	private final UserRepository userRepository;

	// 아이디 중복 확인
	@Override
	public boolean isUsernameAvailable(String username) {
		return !userRepository.existsByUsername(username);
	}

}
