package com.umc.pyeongsaeng.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.umc.pyeongsaeng.domain.user.entity.SeniorProfile;

public interface SeniorProfileRepository extends JpaRepository<SeniorProfile, Long> {

	@Query("SELECT COUNT(ps) FROM SeniorProfile ps WHERE ps.protector.id = :protectorId")
	long countByProtectorId(@Param("protectorId") Long protectorId);

}
