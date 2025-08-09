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

	// 키워드 생성
	@Operation(
		summary = "키워드 추천 생성",
		description = "시니어의 기본 정보와 질문을 기반으로 GPT가 핵심 문장 키워드 3개를 추천합니다."
	)
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		description = "시니어 기본 정보와 질문 텍스트",
		required = true,
		content = @Content(
			schema = @Schema(implementation = KeywordGenerationRequestDTO.class),
			examples = @ExampleObject(
				name = "키워드 요청 예시",
				value = """
                    {
                      "answers": [
                        {"question": "하루에 몇 시간 정도 일하고 싶으신가요?", "option": "3시간 내외"},
                        {"question": "어디에서 일하는 것을 선호하시나요?", "option": "실내"}
                      ],
                      "question": "지원 동기가 무엇인가요?"
                    }
                """
			)
		)
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200", description = "키워드 생성 성공",
			content = @Content(
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
			)
		)
	})
	@PostMapping("/keywords")
	public ApiResponse<List<String>> generateKeywords(@RequestBody @Valid KeywordGenerationRequestDTO request) {
		return ApiResponse.onSuccess(openAiService.generateKeywords(request));
	}

	// 문장 생성
	@Operation(
		summary = "문장 자동 생성",
		description = "선택한 키워드와 시니어 정보로 질문에 대한 문장형 답변을 생성합니다. (400자 이내)"
	)
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		description = "시니어 기본 정보 + 질문 + 선택한 키워드",
		required = true,
		content = @Content(
			schema = @Schema(implementation = AnswerGenerationRequestDTO.class),
			examples = @ExampleObject(
				name = "문장 생성 예시",
				value = """
                    {
                      "answers": [
                        {"question": "하루에 몇 시간 정도 일하고 싶으신가요?", "option": "3시간 내외"},
                        {"question": "어디에서 일하는 것을 선호하시나요?", "option": "실내"}
                      ],
                      "question": "지원 동기가 무엇인가요?",
                      "selectedKeyword": "경제적인 이유"
                    }
                """
			)
		)
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200", description = "문장 생성 성공",
			content = @Content(
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
			)
		)
	})
	@PostMapping("/answers")
	public ApiResponse<String> generateAnswer(@RequestBody @Valid AnswerGenerationRequestDTO request) {
		return ApiResponse.onSuccess(openAiService.generateAnswer(request));
	}

	// 보강 답변 생성
	@Operation(
		summary = "문장 업데이트 생성",
		description = "기존 답변 생성과 동일한 입력에 더해 '추가 경험(addedExperience)'을 반영해 보완된 답변을 생성합니다.(500자 이내)"
	)
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		required = true,
		content = @Content(
			schema = @Schema(implementation = AnswerGenerationRequestDTO.class),
			examples = @ExampleObject(
				name = "보강 문장 생성 예시",
				value = """
                    {
                      "answers": [
                        {"question": "어디에서 일하는 것을 선호하시나요?", "option": "실내"},
                        {"question": "어떤 일을 할 때 가장 보람을 느끼시나요?", "option": "아동 보호"}
                      ],
                      "question": "지원 동기가 무엇인가요?",
                      "generatedAnswer": "저는 경제적으로 자립하고 손주에게 맛있는 걸 사주기 위해 지원했습니다.",
                      "addedExperience": "주민센터 아동돌봄 봉사 1년"
                    }
                """
			)
		)
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200", description = "보강 문장 생성 성공",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(
					name = "보강 문장 응답 예시",
					value = """
                        {
                          "isSuccess": true,
                          "code": "COMMON200",
                          "message": "성공입니다.",
                          "result": "평소 아이들을 돕는 일에 보람을 느껴 주민센터에서 1년간 아동돌봄 봉사를 했습니다. 그 경험을 살려 안정적인 실내 환경에서 책임 있게 일하고 손주에게 맛있는 걸 사주기 위해 지원했습니다."
                        }
                    """
				)
			)
		)
	})
	@PostMapping("/updated-answers")
	public ApiResponse<String> generateUpdatedAnswer(@RequestBody @Valid AnswerGenerationRequestDTO request) {
		return ApiResponse.onSuccess(openAiService.generateUpdatedAnswer(request));
	}
}
