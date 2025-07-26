package com.umc.pyeongsaeng.domain.job.recommendation.dto.request;

public record TravelTimeRequest(
	double originLat,
	double originLng,
	double destLat,
	double destLng
) {}
