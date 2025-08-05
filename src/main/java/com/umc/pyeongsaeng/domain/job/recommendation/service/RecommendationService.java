package com.umc.pyeongsaeng.domain.job.recommendation.service;

import com.umc.pyeongsaeng.domain.job.recommendation.dto.response.RecommendationResponseDTO;

import java.util.List;

public interface RecommendationService {
	List<RecommendationResponseDTO> recommendJobsByDistance(Long userId);

	List<RecommendationResponseDTO> recommendJobsByJobTypeAndDistance(Long userId);
}
