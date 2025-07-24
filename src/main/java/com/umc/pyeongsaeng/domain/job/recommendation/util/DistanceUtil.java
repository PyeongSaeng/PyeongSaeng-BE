package com.umc.pyeongsaeng.domain.job.recommendation.util;

import org.springframework.stereotype.Component;

@Component
public class DistanceUtil {

	private static final double EARTH_RADIUS_KM = 6371.0; // 지구 반지름 (단위: km)

	/**
	 * 위도/경도를 이용한 두 지점 간 거리 계산 (단위: km)
	 */
	public double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
			+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
			* Math.sin(dLng / 2) * Math.sin(dLng / 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return EARTH_RADIUS_KM * c;
	}
}
