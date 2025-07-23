package com.umc.pyeongsaeng.domain.job.recommendation.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TravelTimeUtil {

	@Value("${google.maps.api-key}")
	private String googleApiKey;

	public Optional<String> getTravelModeSummary(double originLat, double originLng, double destLat, double destLng) {
		try {
			String url = String.format(
				"https://maps.googleapis.com/maps/api/directions/json?origin=%f,%f&destination=%f,%f&mode=transit&key=%s",
				originLat, originLng, destLat, destLng, URLEncoder.encode(googleApiKey, StandardCharsets.UTF_8)
			);

			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.GET()
				.build();

			HttpClient client = HttpClient.newHttpClient();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			JSONObject json = new JSONObject(response.body());
			JSONArray routes = json.optJSONArray("routes");

			if (routes != null && routes.length() > 0) {
				JSONObject leg = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0);
				String durationText = leg.getJSONObject("duration").getString("text");

				// 분 단위 한글로 변경
				durationText = durationText
					.replaceAll("mins", "분")
					.replaceAll("min", "분")
					.replaceAll("hours", "시간")
					.replaceAll("hour", "시간");

				JSONArray steps = leg.getJSONArray("steps");
				Set<String> modes = new LinkedHashSet<>();

				for (int i = 0; i < steps.length(); i++) {
					JSONObject step = steps.getJSONObject(i);
					String travelMode = step.optString("travel_mode");

					if ("WALKING".equals(travelMode)) {
						modes.add("도보");
					} else if ("TRANSIT".equals(travelMode)) {
						String type = step.getJSONObject("transit_details")
							.getJSONObject("line")
							.getJSONObject("vehicle")
							.getString("type");

						if ("SUBWAY".equals(type)) {
							modes.add("지하철");
						} else if ("BUS".equals(type)) {
							modes.add("버스");
						} else {
							modes.add("대중교통");
						}
					}
				}

				String summary = String.join(" + ", modes) + " " + durationText;
				return Optional.of(summary);
			}

		} catch (Exception e) {
			log.error("[TravelTimeUtil] API 호출 실패: {}", e.getMessage());
		}

		return Optional.empty();
	}
}
