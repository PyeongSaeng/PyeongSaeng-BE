package com.umc.pyeongsaeng.domain.job.service;

import com.umc.pyeongsaeng.domain.job.entity.FormField;
import org.springframework.data.domain.Page;

import com.umc.pyeongsaeng.domain.company.entity.Company;
import com.umc.pyeongsaeng.domain.job.dto.response.JobPostResponseDTO;
import com.umc.pyeongsaeng.domain.job.enums.JobPostState;
import org.springframework.data.domain.Page;

import java.util.List;

public interface JobPostQueryService {

	Page<JobPostResponseDTO.JobPostPreviewByCompanyDTO> getJobPostPreViewPageByCompany(Company company, Integer page, JobPostState jobPostState);
	Page<JobPost> getJobPostList(Company company, Integer page);

	List<FormField> getFormFieldList(Long jobPostId);
	JobPostResponseDTO.JobPostDetailDTO getJobPostDetail(Long jobPostId, Long userId);
}
