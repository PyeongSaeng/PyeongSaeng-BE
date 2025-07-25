package com.umc.pyeongsaeng.domain.company.client;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.umc.pyeongsaeng.domain.company.dto.NtsRequest;
import com.umc.pyeongsaeng.domain.company.dto.NtsResponse;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NtsApiClient {

	private final RestTemplate restTemplate;

	@Value("${odcloud.api.base-url}")
	private String baseUrl;

	@Value("${odcloud.api.service-key}")
	private String serviceKey;

	private static final String INVALID_BUSINESS_MSG = "국세청에 등록되지 않은 사업자등록번호입니다.";

	@Value("${odcloud.api.status-path}")
	private String statusPath;

	private static final String RETURN_TYPE = "JSON";
	private static final String ACTIVE_BUSINESS_STATUS_CODE = "01";

	public boolean isActiveBusinessNumber(String businessNo) {
		NtsRequest.NtsBusinessmanStatusRequestDto request = new NtsRequest.NtsBusinessmanStatusRequestDto(
			Collections.singletonList(businessNo)
		);

		String url = UriComponentsBuilder.fromHttpUrl(baseUrl + statusPath)
			.queryParam("serviceKey", serviceKey)
			.queryParam("returnType", RETURN_TYPE)
			.build()
			.toUriString();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<NtsRequest.NtsBusinessmanStatusRequestDto> entity = new HttpEntity<>(request, headers);

		try {
			ResponseEntity<NtsResponse.NtsBusinessmanStatusResponseDto> response = restTemplate.exchange(
				url,
				HttpMethod.POST,
				entity,
				NtsResponse.NtsBusinessmanStatusResponseDto.class
			);

			NtsResponse.NtsBusinessmanStatusResponseDto body = response.getBody();

			// API 응답 상태 확인
			if (body == null || !"OK".equals(body.getStatusCode())) {
				log.error("NTS API 호출 실패. statusCode: {}", body != null ? body.getStatusCode() : "null");
				throw new GeneralException(ErrorStatus.EXTERNAL_API_ERROR);
			}

			// 사업자번호가 존재하지 않는 경우
			if (body.getData() == null || body.getData().isEmpty()) {
				log.info("사업자등록번호 {}는 국세청에 등록되지 않은 번호입니다.", businessNo);
				return false;
			}

			NtsResponse.NtsBusinessmanStatusResponseDto.BusinessData businessData = body.getData().get(0);

			// "국세청에 등록되지 않은 사업자등록번호입니다." 메시지 확인
			if (INVALID_BUSINESS_MSG.equals(businessData.getTaxType())) {
				log.info("사업자등록번호 {}는 국세청에 등록되지 않은 번호입니다.", businessNo);
				return false;
			}

			// 사업자 상태 확인
			boolean isActive = ACTIVE_BUSINESS_STATUS_CODE.equals(businessData.getBusinessStatusCode());
			return isActive;

		} catch (GeneralException e) {
			throw e;
		} catch (Exception e) {
			log.error("사업자등록번호 상태 확인 중 오류 발생: {}", e.getMessage(), e);
			throw new GeneralException(ErrorStatus.EXTERNAL_API_ERROR);
		}
	}
}
