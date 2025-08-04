package com.umc.pyeongsaeng.domain.job.recommendation.converter;

import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import com.umc.pyeongsaeng.domain.job.recommendation.dto.response.RecommendationResponseDTO;

public class RecommendationConverter {
	public static RecommendationResponseDTO toRecommendationResponseDTO(JobPost jobPost, double distance, String imageUrl) {
		return RecommendationResponseDTO.builder()
			.jobPostId(jobPost.getId())
			.workplaceName(jobPost.getTitle())
			.description(jobPost.getDescription())
			.imageUrl(imageUrl)
			.distanceKm(distance)
			.build();
	}
}
