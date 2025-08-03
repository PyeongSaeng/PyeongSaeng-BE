package com.umc.pyeongsaeng.domain.application.util;

import com.umc.pyeongsaeng.domain.application.dto.request.AnswerGenerationRequestDTO;
import com.umc.pyeongsaeng.domain.application.dto.request.KeywordGenerationRequestDTO;
import com.umc.pyeongsaeng.domain.senior.dto.response.SeniorQuestionResponseDTO.QuestionAnswerResponseDTO;

import java.util.List;

public class OpenAiPromptBuilder {

	public static String buildKeywordPrompt(KeywordGenerationRequestDTO request) {
		StringBuilder sb = new StringBuilder();

		sb.append(buildUserProfile(request.getAnswers()));
		sb.append("위의 정보를 바탕으로 아래 질문에 대한 답변의 핵심 문장 키워드를 3개 생성해줘. 리스트 번호 없이 텍스트만 생성해주면 돼. 위에 정보가 없을 경우에는 한국 노인의 평균에 맞게 생성하면 돼.\n");
		sb.append("지원자는 시니어이며, 질문은 다음과 같아.\n");
		sb.append("질문: ").append(request.getQuestion());

		return sb.toString();
	}

	public static String buildAnswerPrompt(AnswerGenerationRequestDTO request) {
		StringBuilder sb = new StringBuilder();

		sb.append(buildUserProfile(request.getAnswers()));
		sb.append("선택된 키워드: ").append(request.getSelectedKeyword()).append("\n\n");
		sb.append("위 정보를 바탕으로 아래 질문에 대한 답변을 200자 이내로 작성해줘. 노인이 작성한다고 생각하고 알맞게 현실적으로 작성해줘. 위에 정보가 없을 경우에는 한국 노인의 평균에 맞게 생성하면 돼.\n");
		sb.append("질문: ").append(request.getQuestion());

		return sb.toString();
	}

	private static String buildUserProfile(List<QuestionAnswerResponseDTO> answers) {
		StringBuilder sb = new StringBuilder();
		sb.append("다음은 시니어의 정보입니다.\n\n");

		for (QuestionAnswerResponseDTO answer : answers) {
			String question = answer.getQuestion();
			String selectedOption = answer.getSelectedOption();

			if (question != null && !question.isBlank() &&
				selectedOption != null && !selectedOption.isBlank()) {
				sb.append("Q: ").append(question).append("\n");
				sb.append("A: ").append(selectedOption).append("\n\n");
			}
		}

		return sb.toString();
	}
}
