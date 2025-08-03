package com.umc.pyeongsaeng.domain.application.controller;

import java.util.List;

import com.umc.pyeongsaeng.domain.application.dto.request.AnswerGenerationRequestDTO;
import com.umc.pyeongsaeng.domain.application.dto.request.KeywordGenerationRequestDTO;
import com.umc.pyeongsaeng.domain.application.service.OpenAiService;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI 기반 자동작성", description = "시니어 지원서 질문 자동응답 생성 API")
public class OpenAiController {

	private final OpenAiService openAiService;

	@Operation(summary = "키워드 추천 생성", description = "시니어의 기본 정보와 질문을 기반으로 GPT가 핵심 문장 키워드 3개를 추천합니다.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		description = "시니어 기본 정보와 질문 텍스트",
		required = true,
		content = @Content(
			schema = @Schema(implementation = KeywordGenerationRequestDTO.class),
			examples = @ExampleObject(
				name = "키워드 요청 예시",
				value = "{\n" +
					"  \"answers\": [\n" +
					"    {\"question\": \"하루에 몇 시간 정도 일하고 싶으신가요?\", \"option\": \"3시간 내외\"},\n" +
					"    {\"question\": \"어디에서 일하는 것을 선호하시나요?\", \"option\": \"실내\"}\n" +
					"  ],\n" +
					"  \"question\": \"지원 동기가 무엇인가요?\"\n" +
					"}"
			)
		)
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "키워드 생성 성공", content = @Content(
			mediaType = "application/json",
			examples = @ExampleObject(
				name = "키워드 응답 예시",
				value = """
					{
					  "isSuccess": true,
					  "code": "COMMON200",
					  "message": "성공입니다.",
					  "result": [
					    "경제적인 이유",
					    "사람들과 어울리고 싶어서",
					    "자신감 회복을 위해"
					  ]
					}
				"""
			)
		))
	})
	@PostMapping("/keywords")
	public ApiResponse<List<String>> generateKeywords(@RequestBody @Valid KeywordGenerationRequestDTO request) {
		List<String> keywords = openAiService.generateKeywords(request);
		return ApiResponse.onSuccess(keywords);
	}

	@Operation(summary = "문장 자동 생성", description = "선택한 키워드와 시니어 정보로 질문에 대한 문장형 답변을 생성합니다.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		description = "시니어 기본 정보 + 질문 + 선택한 키워드",
		required = true,
		content = @Content(
			schema = @Schema(implementation = AnswerGenerationRequestDTO.class),
			examples = @ExampleObject(
				name = "문장 생성 예시",
				value = "{\n" +
					"  \"answers\": [\n" +
					"    {\"question\": \"하루에 몇 시간 정도 일하고 싶으신가요?\", \"option\": \"3시간 내외\"},\n" +
					"    {\"question\": \"어디에서 일하는 것을 선호하시나요?\", \"option\": \"실내\"}\n" +
					"  ],\n" +
					"  \"question\": \"지원 동기가 무엇인가요?\",\n" +
					"  \"selectedKeyword\": \"경제적인 이유\"\n" +
					"}"
			)
		)
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "문장 생성 성공", content = @Content(
			mediaType = "application/json",
			examples = @ExampleObject(
				name = "문장 응답 예시",
				value = """
					{
					  "isSuccess": true,
					  "code": "COMMON200",
					  "message": "성공입니다.",
					  "result": "저는 경제적으로 자립하고 손주에게 맛있는 걸 사주기 위해 지원했습니다."
					}
				"""
			)
		))
	})
	@PostMapping("/answers")
	public ApiResponse<String> generateAnswer(@RequestBody @Valid AnswerGenerationRequestDTO request) {
		String response = openAiService.generateAnswer(request);
		return ApiResponse.onSuccess(response);
	}
}
