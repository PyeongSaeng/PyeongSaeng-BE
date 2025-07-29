package com.umc.pyeongsaeng.domain.application.controller;

import com.umc.pyeongsaeng.domain.application.dto.request.OpenAiRequestDTO;
import com.umc.pyeongsaeng.domain.application.dto.response.OpenAiResponseDTO;
import com.umc.pyeongsaeng.domain.application.service.OpenAiService;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class OpenAiController {

	private final OpenAiService openAiService;

	@Operation(
		summary = "AI 지원서 답변 생성 API",
		description = """
            📌 이 API는 지원자의 경력, 공고 설명, 질문을 바탕으로 GPT를 사용해 자동 답변을 생성합니다.

            📌 입력값 설명
            - experience: 지원자의 경력 설명
            - jobDescription: 채용 공고 상세 설명 (HTML/마크다운 제거 후 정제된 텍스트)
            - question: 기업이 제시한 자기소개서 질문

            📌 출력값 설명
            - answer: GPT가 생성한 자기소개서 자동 응답
            """
	)
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		required = true,
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = OpenAiRequestDTO.class),
			examples = @ExampleObject(
				name = "AI 자동 작성 예시",
				summary = "GPT를 통한 자동 답변 생성",
				value = """
                    {
                      "experience": "환경미화 경력 3년, 체계적인 청소 계획 수립과 안전관리 경험",
                      "jobDescription": "서울 강서구 지역 환경미화 공고, 오전 7시~12시 근무, 체력 중요",
                      "question": "우리 회사에 지원한 이유와 본인의 장점을 설명해주세요."
                    }
                """
			)
		)
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "답변 생성 성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(
					name = "성공 응답 예시",
					value = """
                        {
                          "isSuccess": true,
                          "code": "COMMON200",
                          "message": "성공입니다.",
                          "result": {
                            "answer": "저는 3년간 환경미화 경력을 통해 체계적인 청소와 안전한 업무 수행 능력을 키웠습니다..."
                          }
                        }
                    """
				)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "500",
			description = "서버 오류 (예: OpenAI 호출 실패)",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(
					name = "OpenAI API 실패",
					value = """
                        {
                          "isSuccess": false,
                          "code": "OPENAI_ERROR",
                          "message": "AI 응답 생성 중 오류가 발생했습니다.",
                          "result": null
                        }
                    """
				)
			)
		)
	})
	@PostMapping("/generate-answer")
	public ApiResponse<OpenAiResponseDTO> generateAnswer(@RequestBody OpenAiRequestDTO request) {
		OpenAiResponseDTO response = openAiService.generateAnswer(request);
		return ApiResponse.onSuccess(response);
	}
}
