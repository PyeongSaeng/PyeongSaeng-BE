package com.umc.pyeongsaeng.domain.senior.converter;

import java.util.ArrayList;
import java.util.List;

import com.umc.pyeongsaeng.domain.senior.dto.request.SeniorQuestionRequestDTO;
import com.umc.pyeongsaeng.domain.senior.dto.response.SeniorQuestionResponseDTO;
import com.umc.pyeongsaeng.domain.senior.entity.SeniorQuestion;
import com.umc.pyeongsaeng.domain.senior.entity.SeniorQuestionAnswer;
import com.umc.pyeongsaeng.domain.senior.entity.SeniorQuestionOption;

public class SeniorQuestionConverter {

	public static SeniorQuestionResponseDTO.OptionResponseDTO toOptionResponseDTO(SeniorQuestionOption option) {
		return SeniorQuestionResponseDTO.OptionResponseDTO.builder()
			.optionId(option.getId())
			.option(option.getOption())
			.build();
	}

	public static SeniorQuestionResponseDTO.QuestionAnswerResponseDTO toQuestionAnswerDTO(SeniorQuestion question, SeniorQuestionAnswer answer){
		List<SeniorQuestionResponseDTO.OptionResponseDTO> optionDTOList = question.getOptions()
			.stream()
			.map(SeniorQuestionConverter::toOptionResponseDTO)
			.toList();

		return SeniorQuestionResponseDTO.QuestionAnswerResponseDTO.builder()
			.questionId(question.getId())
			.question(question.getQuestion())
			.options(optionDTOList)
			.selectedOptionId(answer != null && answer.getSelectedOption() != null
				? answer.getSelectedOption().getId() : null)
			.selectedOption(answer != null && answer.getSelectedOption() != null
				? answer.getSelectedOption().getOption() : null)
			.build();
	}

	public static SeniorQuestion toSeniorQuestion(SeniorQuestionRequestDTO.QuestionRequestDTO requestDTO) {
		SeniorQuestion question = SeniorQuestion.builder()
			.question(requestDTO.getQuestion())
			.options(new ArrayList<>())
			.build();

		if (requestDTO.getOptions() != null) {
			for (String optionText : requestDTO.getOptions()) {
				SeniorQuestionOption option = toSeniorQuestionOption(optionText, question);
				question.getOptions().add(option);
			}
		}
		return question;
	}

	public static SeniorQuestionOption toSeniorQuestionOption(String option, SeniorQuestion question) {
		return SeniorQuestionOption.builder()
			.option(option)
			.seniorQuestion(question)
			.build();
	}

}
