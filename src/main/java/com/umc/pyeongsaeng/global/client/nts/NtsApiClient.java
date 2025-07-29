package com.umc.pyeongsaeng.global.client.nts;

import java.net.URI;
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
	private String apiBaseUrl;

	@Value("${odcloud.api.service-key}")
	private String serviceKey;

	@Value("${odcloud.api.return-type:JSON}")
	private String returnType;

	private static final String INVALID_BUSINESS_MSG = "국세청에 등록되지 않은 사업자등록번호입니다.";
	private static final String ACTIVE_BUSINESS_STATUS_CODE = "01";

	// 사업자등록번호 활성화 상태 확인
	public boolean isActiveBusinessNumber(String businessNo) {
		NtsRequest.NtsBusinessmanStatusRequestDto request = new NtsRequest.NtsBusinessmanStatusRequestDto(
			Collections.singletonList(businessNo)
		);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<NtsRequest.NtsBusinessmanStatusRequestDto> entity = new HttpEntity<>(request, headers);

		try {
			URI uri = UriComponentsBuilder.fromHttpUrl(apiBaseUrl)
				.queryParam("serviceKey", serviceKey)
				.queryParam("returnType", returnType)
				.build(true)
				.toUri();

			ResponseEntity<NtsResponse.NtsBusinessmanStatusResponseDto> response = restTemplate.exchange(
				uri,
				HttpMethod.POST,
				entity,
				NtsResponse.NtsBusinessmanStatusResponseDto.class
			);

			NtsResponse.NtsBusinessmanStatusResponseDto body = response.getBody();

			if (body == null || !"OK".equals(body.getStatusCode())) {
				log.error("NTS API 호출 실패. statusCode: {}", body != null ? body.getStatusCode() : "null");
				throw new GeneralException(ErrorStatus.EXTERNAL_API_ERROR);
			}

			if (body.getData() == null || body.getData().isEmpty()) {
				log.warn("NTS API 응답에 사업자 데이터가 없습니다. (사업자등록번호: {})", businessNo);
				return false;
			}

			NtsResponse.NtsBusinessmanStatusResponseDto.BusinessData businessData = body.getData().get(0);

			// "국세청에 등록되지 않은 사업자등록번호입니다." 메시지 확인
			if (INVALID_BUSINESS_MSG.equals(businessData.getTaxType())) {
				return false;
			}

			// 사업자 상태 확인
			boolean isActive = ACTIVE_BUSINESS_STATUS_CODE.equals(businessData.getBusinessStatusCode());

			log.info("사업자등록번호 {} 상태: {} (활성화: {})",
				businessNo,
				businessData.getBusinessStatus(),
				isActive);

			return isActive;

		} catch (Exception e) {
			log.error("사업자등록번호 상태 확인 중 오류 발생: {}", e.getMessage(), e);
			throw new GeneralException(ErrorStatus.EXTERNAL_API_ERROR);
		}
	}
}
