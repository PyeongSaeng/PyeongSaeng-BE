package com.umc.pyeongsaeng.domain.job.service;

import com.umc.pyeongsaeng.domain.company.entity.Company;
import com.umc.pyeongsaeng.domain.job.dto.response.FormFieldResponseDTO;
import com.umc.pyeongsaeng.domain.job.dto.response.JobPostResponseDTO;
import com.umc.pyeongsaeng.domain.job.enums.JobPostState;
import com.umc.pyeongsaeng.domain.user.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface JobPostQueryService {

	Page<JobPostResponseDTO.JobPostPreviewByCompanyDTO> getJobPostPreViewPageByCompany(Company company, Integer page, JobPostState jobPostState);

	Page<JobPostResponseDTO.JobPostPreviewByCompanyDTO> getJobPostPreViewPageByCompanyByPopularity(Company company, Integer page);

	List<FormFieldResponseDTO.FormFieldPreview> getFormFieldListDirect(Long jobPostId, User senior);
	JobPostResponseDTO.JobPostDetailDTO getJobPostDetail(Long jobPostId, Long userId);
}
