package com.umc.pyeongsaeng.domain.job.recommendation.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.umc.pyeongsaeng.domain.job.recommendation.service.TravelTimeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/job")
public class RecommendationController {
	private final TravelTimeService travelTimeService;

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
