package com.umc.pyeongsaeng.domain.application.util;

import com.umc.pyeongsaeng.domain.application.dto.request.AnswerGenerationRequestDTO;
import com.umc.pyeongsaeng.domain.application.dto.request.KeywordGenerationRequestDTO;
import com.umc.pyeongsaeng.domain.senior.dto.response.SeniorQuestionResponseDTO.QuestionAnswerResponseDTO;

import java.util.List;

public class OpenAiPromptBuilder {

	//키워드 생성
	public static String buildKeywordPrompt(KeywordGenerationRequestDTO request) {
		StringBuilder sb = new StringBuilder();

		sb.append(buildUserProfile(request.getAnswers()));
		sb.append("위의 정보를 바탕으로 아래 질문에 대한 답변의 핵심 문장 키워드를 3개 생성해줘. "
			+ "리스트 번호나 마지막에 . 없이 텍스트만 생성해주면 돼. 위에 정보가 없을 경우에는 한국 노인의 평균에 맞게 생성하면 돼.\n");
		sb.append("지원자는 시니어이며, 질문은 다음과 같아.\n");
		sb.append("질문: ").append(request.getQuestion());

		return sb.toString();
	}

	//질문에 대한 답변 생성
	public static String buildAnswerPrompt(AnswerGenerationRequestDTO request) {
		StringBuilder sb = new StringBuilder();

		sb.append(buildUserProfile(request.getAnswers()));
		sb.append("선택된 키워드: ").append(request.getSelectedKeyword()).append("\n\n");
		sb.append(
			"위 정보와 선택된 키워드를 바탕으로 아래 질문에 대한 답변을 400자 이내로 작성해줘. "
				+ "위에 정보가 없을 경우에는 한국 노인의 평균에 맞게 생성하면 돼. 앞 뒤에 인삿말은 빼줘.\n");
		sb.append("질문: ").append(request.getQuestion());

		return sb.toString();
	}

	//관련 추가 경험을 포함한 답변 생성
	public static String buildUpdateAnswerPrompt(AnswerGenerationRequestDTO request) {
		StringBuilder sb = new StringBuilder();
		sb.append(buildUserProfile(request.getAnswers()));
		sb.append("질문: ").append(nvl(request.getQuestion())).append("\n\n");
		sb.append("기존 생성된 답변: ").append(request.getGeneratedAnswer()).append("\n\n");
		sb.append("아래는 지원자가 추가로 제공한 관련 경험입니다. 내용에 자연스럽게 반영하되 과장하지 마세요.\n")
			.append("추가 경험: ").append(nvl(request.getAddedExperience())).append("\n\n");

		sb.append("위 정보를 바탕으로 질문에 대한 답변을 500자 이내로 담백하고 진솔하게 작성해줘. ")
			.append("인삿말/마무리 멘트 없이 본문만 출력.\n");

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

	private static String nvl(String s) { return s == null ? "" : s; }
}
