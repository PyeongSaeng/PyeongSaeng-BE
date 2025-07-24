package com.umc.pyeongsaeng.domain.job.recommendation.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.umc.pyeongsaeng.domain.job.recommendation.dto.RecommendationResponse;
import com.umc.pyeongsaeng.domain.job.recommendation.service.JobPostRecommendationService;
import com.umc.pyeongsaeng.domain.job.recommendation.service.TravelTimeService;
import com.umc.pyeongsaeng.domain.user.entity.User;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;
import com.umc.pyeongsaeng.global.apiPayload.code.status.SuccessStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/job")
public class RecommendationController {
	private final TravelTimeService travelTimeService;
	private final JobPostRecommendationService jobpostRecommendationService;

	//직선 거리 기반 추천
	@GetMapping("/recommendations")
	public ApiResponse<List<RecommendationResponse>> recommendJobsByDistance(
		@AuthenticationPrincipal User user
	) {
		Long userId = user.getId();
		List<RecommendationResponse> recommendations = jobpostRecommendationService.recommendJobsByDistance(userId);
		return ApiResponse.of(SuccessStatus._OK, recommendations);
	}

	/** 테스트용
	@GetMapping("/recommendations/test")
	public ApiResponse<List<RecommendationResponse>> recommendJobsByDistanceTest(@RequestParam Long userId) {
		List<RecommendationResponse> recommendations = jobpostRecommendationService.recommendJobsByDistance(userId);
		return ApiResponse.of(SuccessStatus._OK, recommendations);
	}
	 **/

	@GetMapping("/travel-time")
	public ResponseEntity<Map<String, String>> getTravelTime(
		@RequestParam double originLat,
		@RequestParam double originLng,
		@RequestParam double destLat,
		@RequestParam double destLng
	) {
		String travelSummary = travelTimeService.getTravelTime(originLat, originLng, destLat, destLng);
		return ResponseEntity.ok(Map.of("travelSummary", travelSummary));
	}
}
