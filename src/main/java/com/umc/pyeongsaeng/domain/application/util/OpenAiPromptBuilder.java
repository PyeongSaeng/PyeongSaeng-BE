package com.umc.pyeongsaeng.domain.application.util;

import com.umc.pyeongsaeng.domain.senior.dto.request.SeniorQuestionAnswerDTO;
import java.util.List;

public class OpenAiPromptBuilder {

	public static String buildKeywordPrompt(List<SeniorQuestionAnswerDTO> answers, String questionText) {
		StringBuilder sb = new StringBuilder();

		sb.append(buildUserProfile(answers));
		sb.append("위의 정보를 바탕으로 아래 질문에 대한 답변의 핵심 문장 키워드를 3개 생성해줘.\n");
		sb.append("지원자는 시니어이며, 질문은 다음과 같아.\n");
		sb.append("질문: ").append(questionText);

		return sb.toString();
	}

	public static String buildAnswerPrompt(List<SeniorQuestionAnswerDTO> answers, String questionText, String selectedKeyword) {
		StringBuilder sb = new StringBuilder();

		sb.append(buildUserProfile(answers));
		sb.append("선택된 키워드: ").append(selectedKeyword).append("\n\n");
		sb.append("위 정보를 바탕으로 아래 질문에 대한 답변을 200자 이내로 작성해줘.\n");
		sb.append("질문: ").append(questionText);

		return sb.toString();
	}

	private static String buildUserProfile(List<SeniorQuestionAnswerDTO> answers) {
		StringBuilder sb = new StringBuilder();

		sb.append("다음은 시니어의 정보입니다.\n\n");

		for (SeniorQuestionAnswerDTO answer : answers) {
			sb.append("Q: ").append(answer.getQuestionText()).append("\n");
			sb.append("A: ").append(answer.getOptionText()).append("\n\n");
		}

		return sb.toString();
	}
}
