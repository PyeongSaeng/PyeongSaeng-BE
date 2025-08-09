package com.umc.pyeongsaeng.domain.job.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umc.pyeongsaeng.domain.job.entity.JobPostImage;

public interface JobPostImageRepository extends JpaRepository<JobPostImage, Long> {
	Optional<JobPostImage> findFirstByJobPostIdOrderByIdAsc(Long jobPostId);
}
