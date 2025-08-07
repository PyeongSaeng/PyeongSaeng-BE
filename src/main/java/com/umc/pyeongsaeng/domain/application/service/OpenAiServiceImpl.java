package com.umc.pyeongsaeng.domain.application.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.umc.pyeongsaeng.domain.application.dto.request.AnswerGenerationRequestDTO;
import com.umc.pyeongsaeng.domain.application.dto.request.KeywordGenerationRequestDTO;
import com.umc.pyeongsaeng.domain.application.util.OpenAiPromptBuilder;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiServiceImpl implements OpenAiService {

	@Qualifier("openAiRestTemplate")
	private final RestTemplate restTemplate;

	@Value("${openai.api.url}")
	private String openAiUrl;

	@Value("${openai.model}")
	private String model;

	@Value("${openai.api.key}")
	private String apiKey;

	@Override
	public List<String> generateKeywords(KeywordGenerationRequestDTO request) {
		String prompt = OpenAiPromptBuilder.buildKeywordPrompt(request);
		return extractListFromResponse(callOpenAi(prompt));
	}

	@Override
	public String generateAnswer(AnswerGenerationRequestDTO request) {
		String prompt = OpenAiPromptBuilder.buildAnswerPrompt(request);
		return extractStringFromResponse(callOpenAi(prompt));
	}

	private String callOpenAi(String prompt) {
		Map<String, Object> requestBody = Map.of(
			"model", model,
			"messages", new Object[]{
				Map.of("role", "system", "content", "너는 노인을 위한 지원서 작성 도우미야. 시니어가 직접 말하는 것처럼 편안하고 자연스럽게, 간단명료하고 진심 어린 표현으로 작성해줘. 너무 미사여구를 쓰지 마."),
				Map.of("role", "user", "content", prompt)
			}
		);

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Bearer " + apiKey);

			HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
			ResponseEntity<Map> response = restTemplate.postForEntity(openAiUrl, entity, Map.class);

			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
				return ((Map)((Map)((List<?>)response.getBody().get("choices")).get(0)).get("message")).get("content").toString().trim();
			} else {
				log.error("[OpenAI] 응답 실패: {}", response);
				throw new GeneralException(ErrorStatus.AI_RESPONSE_ERROR);
			}
		} catch (Exception e) {
			log.error("[OpenAI] 요청 중 오류 발생", e);
			throw new GeneralException(ErrorStatus.AI_REQUEST_ERROR);
		}
	}

	private List<String> extractListFromResponse(String rawContent) {
		return Arrays.stream(rawContent.split("[,\n]"))
			.map(String::trim)
			.filter(s -> !s.isBlank())
			.toList();
	}

	private String extractStringFromResponse(String rawContent) {
		return rawContent;
	}
}
