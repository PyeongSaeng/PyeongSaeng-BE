package com.umc.pyeongsaeng.domain.job.service;

import com.umc.pyeongsaeng.domain.company.entity.Company;
import com.umc.pyeongsaeng.domain.job.dto.response.JobPostResponseDTO;
import com.umc.pyeongsaeng.domain.job.enums.JobPostState;
import org.springframework.data.domain.Page;

public interface JobPostQueryService {

	Page<JobPostResponseDTO.JobPostPreviewByCompanyDTO> getJobPostPreViewPageByCompany(Company company, Integer page, JobPostState jobPostState);
	JobPostResponseDTO.JobPostDetailDTO getJobPostDetail(Long jobPostId, Long userId);
}
