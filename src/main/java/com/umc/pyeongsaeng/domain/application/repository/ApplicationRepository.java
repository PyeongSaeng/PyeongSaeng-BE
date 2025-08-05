package com.umc.pyeongsaeng.domain.application.repository;

import com.umc.pyeongsaeng.domain.application.entity.Application;
import com.umc.pyeongsaeng.domain.application.enums.ApplicationStatus;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long>, ApplicationRepositoryCustom {
	void deleteByApplicantId(Long applicantId);
	void deleteBySeniorId(Long seniorId);
	Page<Application> findAllByJobPostAndApplicationStatusNot(JobPost jobPost, ApplicationStatus status, Pageable pageable);


	long countByJobPostId(Long jobPostId);
}
