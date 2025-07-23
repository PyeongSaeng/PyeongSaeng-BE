package com.umc.pyeongsaeng.global.client.google;

import org.springframework.data.elasticsearch.core.geo.GeoPoint;

public record GoogleGeocodingResult(
	String sido,
	String sigungu,
	String bname,
	GeoPoint geoPoint
) {}



