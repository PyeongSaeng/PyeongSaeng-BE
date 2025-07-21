package com.umc.pyeongsaeng.global.client.kakao;

import org.springframework.data.elasticsearch.core.geo.GeoPoint;

public record KakaoGeocodingResult(
	String sido,
	String sigungu,
	String bname,
	String locCode,
	GeoPoint geoPoint
) {}
