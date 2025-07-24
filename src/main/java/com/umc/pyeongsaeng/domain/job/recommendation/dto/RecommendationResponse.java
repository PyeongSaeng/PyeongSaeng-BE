package com.umc.pyeongsaeng.domain.job.recommendation.dto;

import com.umc.pyeongsaeng.domain.job.entity.JobPost;

public record RecommendationResponse(
	Long jobPostId,
	String title,
	String companyName,
	String address,
	Integer hourlyWage,
	String workingTime,
	Integer monthlySalary,
	Double distanceKm
) {
	public static RecommendationResponse from(JobPost jobPost, double distanceKm) {
		return new RecommendationResponse(
			jobPost.getId(),
			jobPost.getTitle(),
			jobPost.getCompany().getName(),
			jobPost.getRoadAddress(),
			jobPost.getHourlyWage(),
			jobPost.getWorkingTime(),
			jobPost.getMonthlySalary(),
			distanceKm
		);
	}
}
