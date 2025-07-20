package com.umc.pyeongsaeng.domain.job.converter;

import com.umc.pyeongsaeng.domain.job.dto.response.JobPostImageResponseDTO;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import com.umc.pyeongsaeng.domain.job.entity.JobPostImage;


public class JobPostImageConverter {

	// 출력을 위한 DTO
	public static JobPostImageResponseDTO.JobPostImagePreviewDTO toJobPostImagePreViewDTO(JobPostImage jobPostImage) {
		return JobPostImageResponseDTO.JobPostImagePreviewDTO.builder()
			.keyName(jobPostImage.getKeyName())
			.jobPostId(jobPostImage.getJobPost().getId())
			.build();
	}

	// DB에 저장을 위한 Converter
	public static JobPostImage toJobPostImage(String keyName, JobPost jobPost) {
		return JobPostImage.builder()
			.keyName(keyName)
			.jobPost(jobPost)
			.build();
	}
}
