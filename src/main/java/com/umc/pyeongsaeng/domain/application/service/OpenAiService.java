package com.umc.pyeongsaeng.domain.application.service;

import com.umc.pyeongsaeng.domain.application.util.OpenAiPromptBuilder;
import com.umc.pyeongsaeng.domain.application.dto.request.OpenAiRequestDTO;
import com.umc.pyeongsaeng.domain.application.dto.response.OpenAiResponseDTO;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiService {

	@Qualifier("openAiRestTemplate")
	private final RestTemplate restTemplate;

	@Value("${openai.api.url}")
	private String openAiUrl;

	@Value("${openai.model}")
	private String model;

	@Value("${openai.api.key}")
	private String apiKey;

	public OpenAiResponseDTO generateAnswer(OpenAiRequestDTO request) {

		String prompt = OpenAiPromptBuilder.buildPrompt(
			request.getExperience(),
			request.getJobDescription(),
			request.getQuestion()
		);

		Map<String, Object> requestBody = Map.of(
			"model", model,
			"messages", new Object[]{
				Map.of("role", "system", "content", "너는 노인을 위한 지원서 작성 도우미야. 친절하고 따뜻하게 대답해줘."),
				Map.of("role", "user", "content", prompt)
			}
		);

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Bearer " + apiKey);

			HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, headers);

			ResponseEntity<Map> responseEntity = restTemplate.postForEntity(openAiUrl, httpEntity, Map.class);

			if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
				Map responseBody = responseEntity.getBody();
				String answer = ((Map)((Map)((java.util.List)responseBody.get("choices")).get(0)).get("message")).get("content").toString();

				return new OpenAiResponseDTO(answer.trim());
			} else {
				log.error("[OpenAI] 응답 실패: {}", responseEntity);
				throw new GeneralException(ErrorStatus.AI_RESPONSE_ERROR);
			}

		} catch (Exception e) {
			log.error("[OpenAI] 요청 중 오류 발생", e);
			throw new GeneralException(ErrorStatus.AI_REQUEST_ERROR);
		}
	}
}
