package com.umc.pyeongsaeng.domain.job.recommendation.service;

import com.umc.pyeongsaeng.domain.job.recommendation.dto.response.RecommendationResponse;

import java.util.List;

public interface RecommendationService {
	List<RecommendationResponse> recommendJobsByDistance(Long userId);
}
