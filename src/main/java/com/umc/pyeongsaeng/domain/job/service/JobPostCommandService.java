package com.umc.pyeongsaeng.domain.job.service;

import com.umc.pyeongsaeng.domain.company.entity.Company;
import com.umc.pyeongsaeng.domain.job.dto.request.JobPostRequestDTO;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;

public interface JobPostCommandService {
	// 회사에서 채용공고 게시글 생성
	JobPost createJobPost(JobPostRequestDTO.CreateDTO createDTO, Company company);

	// 회사에서 채용공고 업데이트
	JobPost updateJobPost(Long jobPostId, JobPostRequestDTO.UpdateDTO updateDTO);

	// 회사에서 채용공고 상세조회
	JobPost getJobPostDetail(Long jobPostId);

	// 회사 채용공고 삭제
	void deleteJobPost(Long jobPostId);
}
