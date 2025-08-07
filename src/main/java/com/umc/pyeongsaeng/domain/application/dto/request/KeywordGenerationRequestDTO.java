package com.umc.pyeongsaeng.domain.application.dto.request;

import java.util.List;

import com.umc.pyeongsaeng.domain.senior.dto.response.SeniorQuestionResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeywordGenerationRequestDTO {
	private List<SeniorQuestionResponseDTO.QuestionAnswerResponseDTO> answers;
	private String question;
}
