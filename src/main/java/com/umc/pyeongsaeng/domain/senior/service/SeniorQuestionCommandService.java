package com.umc.pyeongsaeng.domain.senior.service;

import java.util.List;

import com.umc.pyeongsaeng.domain.senior.dto.request.SeniorQuestionRequestDTO;

public interface SeniorQuestionCommandService {
	void saveOrUpdateAnswer(Long seniorProfileId, SeniorQuestionRequestDTO.AnswerRequestDTO request);

	Long createQuestion(SeniorQuestionRequestDTO.QuestionRequestDTO request);


	/**
	 * 시니어(부모)의 추가 질문에 대한 답변을 저장합니다.
	 *
	 * 기존에 저장된 모든 답변을 삭제한 후, 선택된 옵션이 있는 질문만 다시 저장합니다.
	 * selectedOptionId가 null인 질문은 선택하지 않은 것으로 간주하고 저장하지 않습니다.
	 *
	 * @param seniorProfileId 시니어 프로필 ID
	 * @param requests 질문 ID와 선택된 옵션 ID 리스트 (전체 질문 목록)
	 */

	void saveOrUpdateAnswers(Long seniorProfileId, List<SeniorQuestionRequestDTO.AnswerRequestDTO> requests);

}
