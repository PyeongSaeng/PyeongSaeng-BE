package com.umc.pyeongsaeng.domain.senior.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.umc.pyeongsaeng.domain.senior.entity.SeniorProfile;

public interface SeniorProfileRepository extends JpaRepository<SeniorProfile, Long> {

	@Query("SELECT COUNT(ps) FROM SeniorProfile ps WHERE ps.protector.id = :protectorId")
	long countByProtectorId(@Param("protectorId") Long protectorId);

	Optional<SeniorProfile> findBySeniorId(Long seniorId);
	List<SeniorProfile> findByProtectorId(Long protectorId);
}
