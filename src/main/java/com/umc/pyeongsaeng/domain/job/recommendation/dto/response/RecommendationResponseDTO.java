package com.umc.pyeongsaeng.domain.job.recommendation.dto.response;

import com.umc.pyeongsaeng.domain.job.entity.JobPost;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationResponseDTO {
	private Long jobPostId;
	private String workplaceName;
	private String description;
	private String imageUrl;
	private Double distanceKm;

	public static RecommendationResponseDTO of(JobPost jobPost, double distance, String imageUrl) {
		return RecommendationResponseDTO.builder()
			.jobPostId(jobPost.getId())
			.workplaceName(jobPost.getTitle())
			.description(jobPost.getDescription())
			.imageUrl(imageUrl)
			.distanceKm(distance)
			.build();
	}

	public static double distanceKm(RecommendationResponseDTO dto) {
		return dto.getDistanceKm();
	}
}
