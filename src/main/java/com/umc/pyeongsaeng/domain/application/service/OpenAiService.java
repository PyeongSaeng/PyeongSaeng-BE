package com.umc.pyeongsaeng.domain.application.service;

import java.util.List;

import com.umc.pyeongsaeng.domain.application.dto.request.AnswerGenerationRequestDTO;
import com.umc.pyeongsaeng.domain.application.dto.request.KeywordGenerationRequestDTO;

public interface OpenAiService {
	List<String> generateKeywords(KeywordGenerationRequestDTO request);
	String generateAnswer(AnswerGenerationRequestDTO request);
	String generateUpdatedAnswer(AnswerGenerationRequestDTO request);
}
