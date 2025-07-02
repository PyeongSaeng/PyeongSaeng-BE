package com.umc.pyeongsaeng.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.umc.pyeongsaeng.domain.user.entity.ProtectorSenior;

public interface ProtectorSeniorRepository extends JpaRepository<ProtectorSenior, Long> {

	@Query("SELECT COUNT(ps) FROM ProtectorSenior ps WHERE ps.protector.id = :protectorId")
	long countByProtectorId(@Param("protectorId") Long protectorId);

}
