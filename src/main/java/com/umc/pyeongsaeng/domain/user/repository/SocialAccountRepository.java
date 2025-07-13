package com.umc.pyeongsaeng.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umc.pyeongsaeng.domain.user.entity.SocialAccount;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

	Optional<SocialAccount> findByProviderTypeAndProviderUserId(String providerType, String providerUserId);
	boolean existsByProviderTypeAndProviderUserId(String providerType, String providerUserId);
}
