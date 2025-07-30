package com.umc.pyeongsaeng.domain.senior.service;

import java.util.List;

import com.umc.pyeongsaeng.domain.senior.dto.response.SeniorQuestionResponseDTO;

public interface SeniorQuestionQueryService {

	List<SeniorQuestionResponseDTO.QuestionAnswerResponseDTO> getAllSeniorQuestionsWithAnswers(Long seniorProfileId);
}
