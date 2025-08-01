package com.umc.pyeongsaeng.domain.job.service;

import com.umc.pyeongsaeng.domain.job.entity.FormField;
import org.springframework.data.domain.Page;

import com.umc.pyeongsaeng.domain.company.entity.Company;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;

import java.util.List;

public interface JobPostQueryService {

	Page<JobPost> getJobPostList(Company company, Integer page);

	List<FormField> getFormFieldList(Long jobPostId);
}
