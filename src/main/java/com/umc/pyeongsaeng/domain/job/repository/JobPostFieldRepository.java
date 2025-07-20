package com.umc.pyeongsaeng.domain.job.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umc.pyeongsaeng.domain.job.entity.JobPostField;

public interface JobPostFieldRepository extends JpaRepository<JobPostField, Long> {
}
