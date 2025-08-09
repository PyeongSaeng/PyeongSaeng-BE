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
public class AnswerGenerationRequestDTO {
	private List<SeniorQuestionResponseDTO.QuestionAnswerResponseDTO> answers;	//시니어 추가정보
	private String question;	//지원서 질문
	private String selectedKeyword;	//선택된 키워드
	private String generatedAnswer; //생성된 답변
	private String addedExperience;	//관련 추가 정보
}
