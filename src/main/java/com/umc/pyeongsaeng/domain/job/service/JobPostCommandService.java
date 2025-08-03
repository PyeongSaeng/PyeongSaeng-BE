package com.umc.pyeongsaeng.domain.job.service;

import com.umc.pyeongsaeng.domain.job.dto.request.JobPostRequestDTO;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;

public interface JobPostCommandService {
	// 회사에서 채용공고 게시글 생성
	JobPost createJobPost(JobPostRequestDTO.CreateDTO createDTO, Long companyId);

	// 회사에서 채용공고 업데이트
	JobPost updateJobPost(Long jobPostId, JobPostRequestDTO.UpdateDTO updateDTO);
}
