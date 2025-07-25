package com.umc.pyeongsaeng.domain.job.recommendation.dto.response;

import com.umc.pyeongsaeng.domain.job.entity.JobPost;

public record RecommendationResponse(
	Long jobPostId,
	String title,
	String companyName,
	String roadAddress,
	Integer hourlyWage,
	String workingTime,
	Integer monthlySalary,
	Double distanceKm,
	String imageUrl
) {
	public static RecommendationResponse from(JobPost jobPost, double distanceKm) {
		String imageUrl = jobPost.getImages().isEmpty() ? null : jobPost.getImages().get(0).getKeyName();

		return new RecommendationResponse(
			jobPost.getId(),
			jobPost.getTitle(),
			jobPost.getCompany().getName(),
			jobPost.getRoadAddress(),
			jobPost.getHourlyWage(),
			jobPost.getWorkingTime(),
			jobPost.getMonthlySalary(),
			distanceKm,
			imageUrl
		);
	}
}
