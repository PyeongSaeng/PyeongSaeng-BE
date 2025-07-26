package com.umc.pyeongsaeng.domain.job.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.umc.pyeongsaeng.domain.company.entity.Company;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;

public interface JobPostRepository extends JpaRepository<JobPost, Long> {
	Page<JobPost> findAllByCompany(Company company, PageRequest pageRequest);
}

