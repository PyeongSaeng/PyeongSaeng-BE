package com.umc.pyeongsaeng.global.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umc.pyeongsaeng.domain.company.entity.Company;
import com.umc.pyeongsaeng.domain.company.repository.CompanyRepository;
import com.umc.pyeongsaeng.domain.user.entity.User;
import com.umc.pyeongsaeng.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;
	private final CompanyRepository companyRepository;

	/**
	 * 아이디(username)로 사용자 또는 회사 정보 로드
	 * @param username 사용자 또는 회사의 이름
	 * @return UserDetails
	 * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
	 */
	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username).orElse(null);
		if (user != null) {
			return CustomUserDetails.from(user);
		}

		Company company = companyRepository.findByUsername(username).orElse(null);
		if (company != null) {
			return CustomUserDetails.from(company);
		}

		throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
	}

	/**
	 * userId로 사용자 조회
	 * @param userId 사용자 ID
	 * @return UserDetails
	 */
	@Transactional(readOnly = true)
	public UserDetails loadUserById(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

		return CustomUserDetails.from(user);
	}

	/**
	 * companyId로 회사 조회
	 * @param companyId 회사 ID
	 * @return UserDetails
	 */
	@Transactional(readOnly = true)
	public UserDetails loadCompanyById(Long companyId) {
		Company company = companyRepository.findById(companyId)
			.orElseThrow(() -> new UsernameNotFoundException("회사를 찾을 수 없습니다: " + companyId));

		return CustomUserDetails.from(company);
	}
}
