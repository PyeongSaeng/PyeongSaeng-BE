package com.umc.pyeongsaeng.global.client.kakao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoGeocodingClient {

	@Value("${kakao.local.rest-api-key}")
	private String kakaoApiKey;
	private final RestTemplate restTemplate;
	private static final String API_URL = "https://dapi.kakao.com/v2/local/search/address.json";

	public KakaoGeocodingResult convert(String roadAddress) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "KakaoAK " + kakaoApiKey);

			String url = API_URL + "?query=" + roadAddress;
			log.info("[KakaoAPI][REQUEST_URL] {}", url);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			log.info("[KakaoAPI][REQUEST_HEADER] {}", headers);

			ResponseEntity<String> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				entity,
				String.class
			);

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode root = objectMapper.readTree(response.getBody());
			log.info(root.toString());

			JsonNode document = root.get("documents").get(0);
			JsonNode address = document.get("address");

			String sido = address.get("region_1depth_name").asText(); // 시/도
			String sigungu = address.get("region_2depth_name").asText(); // 시/군/구
			String bname = address.get("region_3depth_name").asText(); // 동/읍/면
			String locCode = address.get("b_code").asText();

			double lat = document.get("y").asDouble(); // 위도
			double lon = document.get("x").asDouble(); // 경도
			GeoPoint geoPoint = new GeoPoint(lat, lon);

			return new KakaoGeocodingResult(sido, sigungu, bname, locCode, geoPoint);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			log.error("[KakaoAPI][GEOCODING_FAILED] REST API 호출 오류 - roadAddress={}", roadAddress, e);
			throw new GeneralException(ErrorStatus.KAKAO_API_ERROR);

		} catch (Exception e) {
			log.error("[KakaoAPI][ADDRESS_CONVERT_FAILED] 카카오 주소 파싱 오류 - roadAddress={}", roadAddress, e);
			throw new GeneralException(ErrorStatus.ADDRESS_CONVERSION_FAILED);
		}
	}
}
