package com.umc.pyeongsaeng.domain.social.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

	Optional<SocialAccount> findByProviderTypeAndProviderUserId(String providerType, String providerUserId);
	boolean existsByProviderTypeAndProviderUserId(String providerType, String providerUserId);
}
