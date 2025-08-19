package com.umc.pyeongsaeng.domain.senior.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/seniors")
@RequiredArgsConstructor
@Tag(name = "시니어 추가 질문 API", description = "추가 질문/답변 관련 API")
public class SeniorQuestionController {

	private final SeniorQuestionQueryService seniorQuestionQueryService;
	private final SeniorQuestionCommandService seniorQuestionCommandService;

	@Operation(
		summary = "시니어 추가 질문 전체 조회",
		description =
			"해당 시니어 프로필의 모든 추가 질문과 선택 가능한 옵션, 그리고 이미 선택한 답변을 조회합니다.\n" +
				"- 로그인 유저 - 시니어 본인: seniorProfileId는 현재 로그인한 유저 Id\n" +
				"- 로그인 유저 - 보호자: 케어 중인 어르신 목록에서 선택한 어르신의 seniorId를 사용"
	)

	@GetMapping("/{seniorProfileId}/questions")
	public ApiResponse<List<SeniorQuestionResponseDTO.QuestionAnswerResponseDTO>> getQuestions(@PathVariable Long seniorProfileId) {
		return ApiResponse.onSuccess(seniorQuestionQueryService.getAllSeniorQuestionsWithAnswers(seniorProfileId));
	}

	@Operation(summary = "추가 질문 선택 저장", description = "시니어가 질문에 대해 선택한 옵션들을 한 번에 저장합니다.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				schema = @Schema(implementation = SeniorQuestionRequestDTO.AnswersSaveRequestDTO.class),
				examples = @ExampleObject(
					name = "답변 선택 예시",
					value = """
					{
					  "answers": [
					    {
					      "questionId": 1,
					      "selectedOptionId": 1
					    },
					    {
					      "questionId": 2,
					      "selectedOptionId": 5
					    },
					    {
					      "questionId": 3,
					      "selectedOptionId": 7
					    }
					  ]
					}
				"""
				)
			)
		)
	)
	@PutMapping("/{seniorProfileId}/answers")
	public ApiResponse<Void> updateAnswers(@PathVariable Long seniorProfileId, @RequestBody SeniorQuestionRequestDTO.AnswersSaveRequestDTO request) {
		seniorQuestionCommandService.saveOrUpdateAnswers(seniorProfileId, request.getAnswers());
		return ApiResponse.of(SuccessStatus.SENIOR_QUESTION_ANSWER_SELECTED, null);
	}

	@Operation(summary = "추가 질문 생성", description = "하나의 추가 질문과 해당 옵션들을 생성합니다.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				schema = @Schema(implementation = SeniorQuestionRequestDTO.QuestionRequestDTO.class),
				examples = @ExampleObject(
					name = "추가 질문 예시",
					value = """
					  {
					    "question": "하루에 몇 시간 정도 일하고 싶으신가요?",
					    "options": ["1시간 내외", "2시간 내외", "3시간 내외", "3시간 초과"]
					  }
				"""
				)
			)
		)
	)
	@PostMapping("/questions")
	public ApiResponse<Long> createQuestion(@RequestBody SeniorQuestionRequestDTO.QuestionRequestDTO request) {
		Long createdQuestionId = seniorQuestionCommandService.createQuestion(request);
		return ApiResponse.of(SuccessStatus.SENIOR_QUESTION_CREATED, createdQuestionId);
	}


}
