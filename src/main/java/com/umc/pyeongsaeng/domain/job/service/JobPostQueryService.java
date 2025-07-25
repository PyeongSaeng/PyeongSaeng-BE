package com.umc.pyeongsaeng.domain.job.service;

import org.springframework.data.domain.Page;

import com.umc.pyeongsaeng.domain.company.entity.Company;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;

public interface JobPostQueryService {

	Page<JobPost> getJobPostList(Company company, Integer page);
}
