package com.umc.pyeongsaeng.domain.senior.service;

import org.springframework.stereotype.Service;

import com.umc.pyeongsaeng.domain.senior.entity.SeniorProfile;
import com.umc.pyeongsaeng.domain.senior.repository.SeniorProfileRepository;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;
import com.umc.pyeongsaeng.global.client.google.GoogleGeocodingClient;
import com.umc.pyeongsaeng.global.client.google.GoogleGeocodingResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeniorProfileCommandServiceImpl implements SeniorProfileCommandService {

	private final SeniorProfileRepository seniorProfileRepository;
	private final GoogleGeocodingClient googleGeocodingClient;

	@Override
	public void updateLocation(Long seniorId, String roadAddress) {
		SeniorProfile profile = seniorProfileRepository.findById(seniorId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.ADDRESS_CONVERSION_FAILED));

		GoogleGeocodingResult result = googleGeocodingClient.convert(roadAddress);
		profile.updateLocation(result.geoPoint().getLat(), result.geoPoint().getLon());
	}
}
