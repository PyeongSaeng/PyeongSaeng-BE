package com.umc.pyeongsaeng.domain.job.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umc.pyeongsaeng.domain.job.entity.JobPost;

public interface JobPostRepository extends JpaRepository<JobPost, Long> {

}

