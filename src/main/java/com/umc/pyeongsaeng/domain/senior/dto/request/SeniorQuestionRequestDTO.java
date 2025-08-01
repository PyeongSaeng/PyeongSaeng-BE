package com.umc.pyeongsaeng.domain.senior.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SeniorQuestionRequestDTO {

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class AnswerRequestDTO {
		private Long questionId;
		private Long selectedOptionId;
	}


	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class QuestionRequestDTO {
		private String question;
		private List<String> options;

	}
}
