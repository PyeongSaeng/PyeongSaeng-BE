package com.umc.pyeongsaeng.domain.job.recommendation.service;

import org.springframework.stereotype.Service;

import com.umc.pyeongsaeng.domain.job.recommendation.util.TravelTimeUtil;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TravelTimeService {

	private final TravelTimeUtil travelTimeUtil;

	public String getTravelTime(double originLat, double originLng, double destLat, double destLng) {
		return travelTimeUtil.getTravelModeSummary(originLat, originLng, destLat, destLng)
			.orElseThrow(() -> new GeneralException(ErrorStatus.GOOGLE_DIRECTIONS_API_FAILED));
	}
}
