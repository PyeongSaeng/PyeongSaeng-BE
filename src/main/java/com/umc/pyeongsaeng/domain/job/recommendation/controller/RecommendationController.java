package com.umc.pyeongsaeng.domain.job.recommendation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.umc.pyeongsaeng.domain.job.recommendation.dto.request.TravelTimeRequestDTO;
import com.umc.pyeongsaeng.domain.job.recommendation.dto.response.RecommendationResponseDTO;
import com.umc.pyeongsaeng.domain.job.recommendation.dto.response.TravelTimeResponseDTO;
import com.umc.pyeongsaeng.domain.job.recommendation.service.RecommendationService;
import com.umc.pyeongsaeng.domain.job.recommendation.service.TravelTimeService;
import com.umc.pyeongsaeng.domain.job.search.elkoperation.ElasticOperationService;
import com.umc.pyeongsaeng.domain.user.entity.User;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;
import com.umc.pyeongsaeng.global.apiPayload.code.status.SuccessStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/job")
public class RecommendationController {
	private final TravelTimeService travelTimeService;
	private final RecommendationService recommendationService;
	private final ElasticOperationService elasticOperationService;


	@GetMapping("/recommend/test")
	public ResponseEntity<?> testRecommendation(@RequestParam Long userId) {
		List<RecommendationResponseDTO> result = recommendationService.recommendJobsByJobTypeAndDistance(userId);
		return ResponseEntity.ok(result);
	}

	/**
	// 직선 거리 기반 추천
	@GetMapping("/recommendations")
	public ApiResponse<List<RecommendationResponseDTO>> recommendJobsByDistance(
		@AuthenticationPrincipal User user
	) {
		Long userId = user.getId();
		List<RecommendationResponseDTO> recommendations = recommendationService.recommendJobsByDistance(userId);
		return ApiResponse.of(SuccessStatus._OK, recommendations);
	}
	 **/

	@PostMapping("/travel-time")
	public ResponseEntity<ApiResponse<TravelTimeResponseDTO>> getTravelTime(
		@RequestBody TravelTimeRequestDTO request
	) {
		String travelSummary = travelTimeService.getTravelTime(
			request.originLat(),
			request.originLng(),
			request.destLat(),
			request.destLng()
		);

		TravelTimeResponseDTO response = new TravelTimeResponseDTO(travelSummary);
		return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, response));
	}

	@GetMapping("/test/es-index")
	public ResponseEntity<?> checkIndex() {
		boolean exists = elasticOperationService.checkIfExistIndex("jobposts");
		return ResponseEntity.ok("jobposts exists? → " + exists);
	}
}
