package com.umc.pyeongsaeng.domain.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umc.pyeongsaeng.domain.application.entity.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
	void deleteByApplicantId(Long applicantId);
	void deleteBySeniorId(Long seniorId);
}
