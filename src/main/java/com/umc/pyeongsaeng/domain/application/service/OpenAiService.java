package com.umc.pyeongsaeng.domain.application.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OpenAiService {

	private final RestTemplate restTemplate;

	public String generateAnswer(String prompt) throws JsonProcessingException {
		// GPT 호출 API URL
		@Value("${openai.api.url}")
		private String openAiUrl;

		@Value("${openai.model}")
		private String model;

		// 요청 바디 설정 (ChatGPT 방식)
		Map<String, Object> request = Map.of(
			"model", "gpt-4",
			"messages", List.of(
				Map.of("role", "user", "content", prompt)
			)
		);

		// 요청 보내기
		ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

		// 응답 파싱
		ObjectMapper mapper = new ObjectMapper();
		String content = mapper.readTree(response.getBody())
			.get("choices").get(0).get("message").get("content").asText();

		return content;
	}
}
