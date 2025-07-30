package com.umc.pyeongsaeng.domain.senior.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SeniorQuestionResponseDTO {

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class OptionResponseDTO {
		private Long optionId;
		private String option;
	}


	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class QuestionAnswerResponseDTO {
		private Long questionId;
		private String question;
		private List<OptionResponseDTO> options;
		private Long selectedOptionId;
		private String selectedOption;
	}
}
