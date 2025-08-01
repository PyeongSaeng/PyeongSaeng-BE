package com.umc.pyeongsaeng.domain.senior.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umc.pyeongsaeng.domain.senior.dto.request.SeniorQuestionRequestDTO;
import com.umc.pyeongsaeng.domain.senior.dto.response.SeniorQuestionResponseDTO;
import com.umc.pyeongsaeng.domain.senior.service.SeniorQuestionCommandService;
import com.umc.pyeongsaeng.domain.senior.service.SeniorQuestionQueryService;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;
import com.umc.pyeongsaeng.global.apiPayload.code.status.SuccessStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/seniors")
@RequiredArgsConstructor
@Tag(name = "시니어 추가 질문 API", description = "추가 질문/답변 관련 API")
public class SeniorQuestionController {

	private final SeniorQuestionQueryService seniorQuestionQueryService;
	private final SeniorQuestionCommandService seniorQuestionCommandService;

	@Operation(summary = "시니어 추가 질문 전체 조회", description = "해당 시니어 프로필의 모든 추가 질문과 선택 가능한 옵션, 그리고 이미 선택한 답변을 조회합니다.(seniorProfileId = userId)")
	@GetMapping("/{seniorProfileId}/questions")
	public ApiResponse<List<SeniorQuestionResponseDTO.QuestionAnswerResponseDTO>> getQuestions(@PathVariable Long seniorProfileId) {
		return ApiResponse.onSuccess(seniorQuestionQueryService.getAllSeniorQuestionsWithAnswers(seniorProfileId));
	}

	@Operation(summary = "추가 질문 답변 선택", description = "해당 시니어의 질문에 대해 답변을 선택하거나 수정합니다.")
	@PatchMapping("/{seniorProfileId}/answers")
	public ApiResponse<Void> updateAnswer(@PathVariable Long seniorProfileId, @RequestBody SeniorQuestionRequestDTO.AnswerRequestDTO request) {
		seniorQuestionCommandService.saveOrUpdateAnswer(seniorProfileId, request);
		return ApiResponse.of(SuccessStatus.SENIOR_QUESTION_ANSWER_SELECTED, null);
	}

	@Operation(summary = "추가 질문 생성", description = "새로운 추가 질문과 선택 가능한 옵션을 생성합니다.")
	@PostMapping("/questions")
	public ApiResponse<Long> createQuestion(@RequestBody SeniorQuestionRequestDTO.QuestionRequestDTO request) {
		Long createdQuestionId = seniorQuestionCommandService.createQuestion(request);
		return ApiResponse.of(SuccessStatus.SENIOR_QUESTION_CREATED, createdQuestionId);
	}


}
