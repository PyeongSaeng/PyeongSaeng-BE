package com.umc.pyeongsaeng.domain.job.recommendation.controller;

import com.umc.pyeongsaeng.domain.job.recommendation.dto.request.TravelTimeRequestDTO;
import com.umc.pyeongsaeng.domain.job.recommendation.dto.response.RecommendationResponseDTO;
import com.umc.pyeongsaeng.domain.job.recommendation.dto.response.TravelTimeResponseDTO;
import com.umc.pyeongsaeng.domain.job.recommendation.service.RecommendationService;
import com.umc.pyeongsaeng.domain.job.recommendation.service.TravelTimeService;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;
import com.umc.pyeongsaeng.global.apiPayload.code.status.SuccessStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/job")
@Tag(name = "맞춤 추천 API", description = "시니어의 직무 선호도 및 위치를 바탕으로 한 채용 공고 추천")
public class RecommendationController {

	private final TravelTimeService travelTimeService;
	private final RecommendationService recommendationService;

	@Operation(
		summary = "맞춤 채용공고 추천",
		description = "시니어의 경력 직무와 위치를 기준으로 맞춤형 채용공고를 추천합니다.\n" +
			"직무 기반 결과가 없을 경우, 위치 기반 거리순으로 fallback 추천합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "추천 성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(
					name = "SuccessExample",
					value = """
						{
						      "isSuccess": true,
						      "code": "COMMON200",
						      "message": "성공입니다.",
						      "result": [
						          {
						              "jobPostId": 116,
						              "workplaceName": "시니어 돌보미 채용 주부 돌봄 요양",
						              "description": "어르신과 함께 즐거운 시간을 보내실 분을 찾습니다. 주 3회, 오후 시간에 근무하며, 식사 준비 및 말벗이 주된 업무입니다.",
						              "imageUrl": "https://pyeongsaeng-bucket.s3.amazonaws.com/file5123?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=",
						              "distanceKm": 12.6
						          },
						          {
						              "jobPostId": 117,
						              "workplaceName": "시니어 돌보미 채용 주부 돌봄 요양",
						              "description": "어르신과 함께 즐거운 시간을 보내실 분을 찾습니다. 주 3회, 오후 시간에 근무하며, 식사 준비 및 말벗이 주된 업무입니다.",
						              "imageUrl": "https://pyeongsaeng-bucket.s3.amazonaws.com/file5123?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=",
						              "distanceKm": 21.9
						          }
						      ]
						  }
					"""
				)
			)
		)
	})
	@GetMapping("/recommendations")
	public ResponseEntity<ApiResponse<List<RecommendationResponseDTO>>> recommendJobs(
		@Parameter(name = "userId", description = "시니어 유저 ID", example = "1")
		@RequestParam Long userId
	) {
		List<RecommendationResponseDTO> result = recommendationService.recommendJobsByJobTypeAndDistance(userId);
		return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, result));
	}

	@Operation(summary = "출발지-도착지 교통 시간 계산", description = "도보/버스를 포함한 예상 이동시간을 계산합니다.")
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
}
