package com.umc.pyeongsaeng.domain.terms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umc.pyeongsaeng.domain.terms.entity.UserTerms;

public interface UserTermsRepository extends JpaRepository<UserTerms, Long> {
	void deleteByUserId(Long userId);
}
