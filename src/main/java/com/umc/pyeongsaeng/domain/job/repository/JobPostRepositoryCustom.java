package com.umc.pyeongsaeng.domain.job.repository;

import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobPostRepositoryCustom {
	Page<JobPost> findJobPostTrending(Pageable pageable);
}
