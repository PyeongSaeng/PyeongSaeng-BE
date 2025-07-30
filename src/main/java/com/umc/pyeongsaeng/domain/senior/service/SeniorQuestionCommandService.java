package com.umc.pyeongsaeng.domain.senior.service;

import com.umc.pyeongsaeng.domain.senior.dto.request.SeniorQuestionRequestDTO;

public interface SeniorQuestionCommandService {
	void saveOrUpdateAnswer(Long seniorProfileId, SeniorQuestionRequestDTO.AnswerRequestDTO request);

	Long createQuestion(SeniorQuestionRequestDTO.QuestionRequestDTO request);
}
