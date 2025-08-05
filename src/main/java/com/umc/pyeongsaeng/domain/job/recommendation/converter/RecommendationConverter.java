package com.umc.pyeongsaeng.domain.job.recommendation.converter;

import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import com.umc.pyeongsaeng.domain.job.recommendation.dto.response.RecommendationResponseDTO;
import com.umc.pyeongsaeng.domain.job.search.dto.response.JobSearchResponse;

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

	public static RecommendationResponseDTO fromJobSearchResponse(JobSearchResponse response) {
		return RecommendationResponseDTO.builder()
			.jobPostId(Long.valueOf(response.getId()))
			.workplaceName(response.getTitle())
			.distanceKm(Double.valueOf(response.getDisplayDistance()))
			.build();
	}
}
