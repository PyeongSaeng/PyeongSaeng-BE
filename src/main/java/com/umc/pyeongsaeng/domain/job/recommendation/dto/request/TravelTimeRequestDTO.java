package com.umc.pyeongsaeng.domain.job.recommendation.dto.request;

public record TravelTimeRequestDTO(
	double originLat,
	double originLng,
	double destLat,
	double destLng
) {}
