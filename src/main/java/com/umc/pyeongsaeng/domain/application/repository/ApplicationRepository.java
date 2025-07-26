package com.umc.pyeongsaeng.domain.application.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.umc.pyeongsaeng.domain.application.entity.Application;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
	void deleteByApplicantId(Long applicantId);
	void deleteBySeniorId(Long seniorId);
	Page<Application> findAllByJobPost(JobPost jobPost, Pageable pageable);
}
