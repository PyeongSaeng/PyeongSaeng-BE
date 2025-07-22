package com.umc.pyeongsaeng.global.client.google;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleGeocodingClient {

	@Value("${google.geocoding.api-key}")
	private String googleApiKey;

	private final RestTemplate restTemplate;
	private static final String API_URL = "https://maps.googleapis.com/maps/api/geocode/json";

	public GoogleGeocodingResult convert(String roadAddress) {
		try {
			String url = API_URL + "?address=" + roadAddress + "&language=ko&key=" + googleApiKey;

			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode root = objectMapper.readTree(response.getBody());

			JsonNode results = root.get("results");
			if (results.isEmpty()) {
				log.error("[GoogleAPI][NO_RESULT] 주소 변환 실패 - roadAddress={}", roadAddress);
				throw new GeneralException(ErrorStatus.ADDRESS_CONVERSION_FAILED);
			}

			JsonNode result = results.get(0);
			JsonNode location = result.get("geometry").get("location");
			double lat = location.get("lat").asDouble();
			double lon = location.get("lng").asDouble();

			String sido = null; // 시/도
			String si = null; // 시
			String gungu = null; // 군/구
			String bname = null; // 동

			for (JsonNode component : result.get("address_components")) {
				Set<String> types = new HashSet<>();
				component.get("types").forEach(type -> types.add(type.asText()));

				if (types.contains("administrative_area_level_1")) {
					sido = component.get("long_name").asText();
				} else if (types.contains("locality")) {
					si = component.get("long_name").asText();
				} else if (types.contains("sublocality_level_1")) {
					gungu = component.get("long_name").asText();
				} else if (types.contains("sublocality_level_2") || types.contains("neighborhood")) {
					bname = component.get("long_name").asText();
				}
			}
			String sigungu = Stream.of(si, gungu)
				.filter(Objects::nonNull)
				.collect(Collectors.joining(" "));

			return new GoogleGeocodingResult(sido, sigungu, bname, new GeoPoint(lat, lon));

		} catch (HttpClientErrorException | HttpServerErrorException e) {
			log.error("[GoogleAPI][GEOCODING_FAILED] 외부 API 호출 오류 - roadAddress={}", roadAddress, e);
			throw new GeneralException(ErrorStatus.GOOGLE_API_ERROR);
		} catch (Exception e) {
			log.error("[GoogleAPI][ADDRESS_CONVERT_FAILED] 주소 변환 실패 - roadAddress={}", roadAddress, e);
			throw new GeneralException(ErrorStatus.ADDRESS_CONVERSION_FAILED);
		}
	}
}

