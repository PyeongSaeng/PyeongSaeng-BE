package com.umc.pyeongsaeng.domain.job.repository;

import com.umc.pyeongsaeng.domain.company.entity.Company;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JobPostRepository extends JpaRepository<JobPost, Long> {
	Page<JobPost> findAllByCompany(Company company, PageRequest pageRequest);
	void deleteByCompanyId(Long companyId);

	@Query("SELECT jp FROM JobPost jp WHERE jp.company = :company AND (jp.deadline > CURRENT_TIMESTAMP AND jp.state = 'RECRUITING')")
	Page<JobPost> findActiveJobPostsByCompany(Company company, PageRequest pageRequest);

	@Query("SELECT jp FROM JobPost jp WHERE jp.company = :company AND (jp.deadline <= CURRENT_TIMESTAMP OR jp.state = 'CLOSED')")
	Page<JobPost> findClosedJobPostsByCompany(Company company, PageRequest pageRequest);

}

