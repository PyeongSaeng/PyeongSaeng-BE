package com.umc.pyeongsaeng.domain.application.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import com.umc.pyeongsaeng.domain.application.converter.ApplicationConverter;
import com.umc.pyeongsaeng.domain.application.dto.response.ApplicationResponseDTO;
import com.umc.pyeongsaeng.domain.application.entity.Application;
import com.umc.pyeongsaeng.domain.application.service.ApplicationQueryService;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;
import com.umc.pyeongsaeng.global.resolvation.annotation.PageNumber;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/applications")
@Tag(name = "신청서 API", description = "지원서 조회 관련 API")
@RequiredArgsConstructor
public class ApplicationController {

	private final ApplicationQueryService applicationQueryService;
	private final ApplicationConverter applicationConverter;

	@GetMapping
	@Operation(summary = "특정 공고의 지원서 목록 조회 API", description = "특정 채용 공고에 제출된 지원서 목록을 페이지별로 조회하는 API입니다.")
	@Parameters({
		@Parameter(name = "jobPostId", description = "조회할 채용 공고의 ID", required = true, in = ParameterIn.QUERY),
		@Parameter(name = "page", description = "페이지 번호 (0부터 시작)", required = true, in = ParameterIn.QUERY)
	})
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value =
			"{\n" +
				"    \"isSuccess\": true,\n" +
				"    \"code\": \"COMMON200\",\n" +
				"    \"message\": \"성공입니다.\",\n" +
				"    \"result\": {\n" +
				"        \"applicationList\": [\n" +
				"            {\n" +
				"                \"applicationId\": 1,\n" +
				"                \"applicantName\": \"김시니어\"\n" +
				"            },\n" +
				"            {\n" +
				"                \"applicationId\": 2,\n" +
				"                \"applicantName\": \"박시니어\"\n" +
				"            }\n" +
				"        ],\n" +
				"        \"listSize\": 2,\n" +
				"        \"totalPage\": 1,\n" +
				"        \"totalElements\": 2,\n" +
				"        \"isFirst\": true,\n" +
				"        \"isLast\": true\n" +
				"    }\n" +
				"}"))),
	})
	public ApiResponse<ApplicationResponseDTO.ApplicationPreViewListDTO> getApplications(
		@RequestParam(name = "jobPostId") Long jobPostId,
		@PageNumber Integer page) {

		Page<Application> applicationPage = applicationQueryService.findCompanyApplications(jobPostId, page);

		return ApiResponse.onSuccess(ApplicationConverter.toApplicationPreViewListDTO(applicationPage));
	}

	@GetMapping("/{applicationId}/details")
	@Operation(summary = "지원서 상세 조회 API", description = "특정 지원서의 상세 정보를 조회하는 API입니다. 질문과 답변 목록을 포함합니다.")
	@Parameters({
		@Parameter(name = "applicationId", description = "조회할 지원서의 ID", required = true, in = ParameterIn.PATH)
	})
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value =
			"{\n" +
				"    \"isSuccess\": true,\n" +
				"    \"code\": \"COMMON200\",\n" +
				"    \"message\": \"성공입니다.\",\n" +
				"    \"result\": {\n" +
				"        \"questionAndAnswerList\": [\n" +
				"            {\n" +
				"                \"fieldName\": \"성함\",\n" +
				"                \"answerContent\": \"김시니어\",\n" +
				"                \"fieldType\": \"TEXT\"\n" +
				"            },\n" +
				"            {\n" +
				"                \"fieldName\": \"경력 증명서\",\n" +
				"                \"answerContent\": [\n" +
				"                    {\n" +
				"                        \"keyName\": \"a1b2c3d4\",\n" +
				"                        \"originalFileName\": \"career_certificate.png\"\n" +
				"                    }\n" +
				"                ],\n" +
				"                \"fieldType\": \"IMAGE\"\n" +
				"            }\n" +
				"        ],\n" +
				"        \"postState\": \"ACTIVE\",\n" +
				"        \"applicationState\": \"PENDING\"\n" +
				"    }\n" +
				"}"))),
	})
	public ApiResponse<ApplicationResponseDTO.ApplicationQnADetailPreViewDTO> getApplicationDetails(
		@PathVariable(name = "applicationId") Long applicationId) {

		ApplicationResponseDTO.ApplicationQnADetailPreViewDTO applicationQnADetailPreViewDTO =
			applicationQueryService.getApplicationQnADetail(applicationId);

		return ApiResponse.onSuccess(applicationQnADetailPreViewDTO);
	}
}
