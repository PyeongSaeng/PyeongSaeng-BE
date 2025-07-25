package com.umc.pyeongsaeng.domain.token.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umc.pyeongsaeng.domain.token.entity.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByRefreshToken(String refreshToken);
	void deleteByUser_Id(Long userId);
	void deleteByCompany_Id(Long companyId);
}
