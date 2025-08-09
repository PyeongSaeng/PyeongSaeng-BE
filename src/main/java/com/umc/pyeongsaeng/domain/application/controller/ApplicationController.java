package com.umc.pyeongsaeng.domain.application.controller;

import java.util.List;

import com.umc.pyeongsaeng.domain.application.converter.ApplicationConverter;
import com.umc.pyeongsaeng.domain.application.dto.request.ApplicationRequestDTO;
import com.umc.pyeongsaeng.domain.application.dto.response.ApplicationResponseDTO;
import com.umc.pyeongsaeng.domain.application.entity.Application;
import com.umc.pyeongsaeng.domain.application.service.ApplicationCommandService;
import com.umc.pyeongsaeng.domain.application.service.ApplicationQueryService;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;
import com.umc.pyeongsaeng.global.apiPayload.code.status.SuccessStatus;
import com.umc.pyeongsaeng.global.resolvation.annotation.PageNumber;
import com.umc.pyeongsaeng.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
@Tag(name = "신청서 API", description = "지원서 조회 관련 API")
@RequiredArgsConstructor
public class ApplicationController {

	private final ApplicationQueryService applicationQueryService;
	private final ApplicationCommandService applicationCommandService;

	@GetMapping
	@Operation(summary = "회사가 특정 공고의 지원서 목록 조회 API", description = "특정 채용 공고에 제출된 지원서 목록을 페이지별로 조회하는 API입니다.")
	@Parameters({
		@Parameter(name = "jobPostId", description = "조회할 채용 공고의 ID", required = true, in = ParameterIn.QUERY),
		@Parameter(name = "page", description = "페이지 번호 (1부터 시작)", required = true, in = ParameterIn.QUERY)
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
				"                \"applicantStatus\": \"DRAFT\"\n" +
				"            },\n" +
				"            {\n" +
				"                \"applicationId\": 2,\n" +
				"                \"applicantName\": \"박시니어\"\n" +
				"                \"applicantStatus\": \"SUBMITTED\"\n" +
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
	@Operation(summary = "회사가 지원서 상세 조회 API", description = "특정 지원서의 상세 정보를 조회하는 API입니다. 질문과 답변 목록을 포함합니다.")
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
				"        \"applicationState\": \"DRAFT\"\n" +
				"    }\n" +
				"}"))),
	})
	public ApiResponse<ApplicationResponseDTO.ApplicationQnADetailPreViewDTO> getApplicationDetails(
		@PathVariable(name = "applicationId") Long applicationId) {

		ApplicationResponseDTO.ApplicationQnADetailPreViewDTO applicationQnADetailPreViewDTO =
			applicationQueryService.getApplicationQnADetail(applicationId);

		return ApiResponse.onSuccess(applicationQnADetailPreViewDTO);
	}

	@PatchMapping("/{applicationId}/state")
	@Operation(summary = "회사가 지원서 상태 변경 API", description = "특정 지원서의 상태(합격, 불합격 등)를 변경하는 API입니다.")
	@Parameters({
		@Parameter(name = "applicationId", description = "상태를 변경할 지원서의 ID", required = true, in = ParameterIn.PATH)
	})
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value =
			"{\n" +
				"    \"isSuccess\": true,\n" +
				"    \"code\": \"COMMON200\",\n" +
				"    \"message\": \"성공입니다.\",\n" +
				"    \"result\": {\n" +
				"        \"applicationId\": 1,\n" +
				"        \"status\": \"APPROVED\"\n" +
				"    }\n" +
				"}"))),
	})
	public ApiResponse<ApplicationResponseDTO.ApplicationStateResponseDTO> updateApplicationStatus(
		@PathVariable(name = "applicationId") Long applicationId,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "지원서 상태 변경 요청",
			required = true,
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ApplicationRequestDTO.ApplicationStatusRequestDTO.class),
				examples = @ExampleObject(
					name = "상태 변경 예시",
					value = "{\n" +
						"  \"applicationStatus\": \"APPROVED\"\n" +
						"}"
				)
			)
		)
		@RequestBody @Valid ApplicationRequestDTO.ApplicationStatusRequestDTO applicationStatusRequestDTO) {

		Application updatedApplication = applicationCommandService.updateApplicationState(applicationId,applicationStatusRequestDTO);

		return ApiResponse.onSuccess(ApplicationConverter.toApplicationStateResponseDTO(updatedApplication));
	}

	@PostMapping("/delegate")
	@Operation(summary = "사용자 지원서 대리로 최종 제출 or 임시저장 API", description = "대리자가 사용자의 특정 채용 공고에 지원서를 제출하는 API입니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value =
			"{\n" +
			"    \"isSuccess\": true,\n" +
			"    \"code\": \"COMMON200\",\n" +
			"    \"message\": \"성공입니다.\",\n" +
			"    \"result\": {\n" +
			"        \"applicationId\": 1,\n" +
			"        \"jobPostId\": 1,\n" +
			"        \"applicationStatus\": \"SUBMITTED\",\n" +
			"        \"createdAt\": \"2025-08-03T15:00:00\",\n" +
			"        \"answers\": [\n" +
			"            {\n" +
			"                \"fieldType\": \"TEXT\",\n" +
			"                \"formFieldId\": 1,\n" +
			"                \"formFieldName\": \"성함\",\n" +
			"                \"answer\": \"김시니어\"\n" +
			"            },\n" +
			"            {\n" +
			"                \"fieldType\": \"IMAGE\",\n" +
			"                \"formFieldId\": 2,\n" +
			"                \"formFieldName\": \"경력 증명서\",\n" +
			"                \"answer\": [\n" +
			"                    {\n" +
			"                        \"keyName\": \"a1b2c3d4\",\n" +
			"                        \"originalFileName\": \"career_certificate.png\"\n" +
			"                    }\n" +
			"                ]\n" +
			"            }\n" +
			"        ]\n" +
			"    }\n" +
			"}"))),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 공고 또는 사용자", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value =
			"{\n" +
			"    \"isSuccess\": false,\n" +
			"    \"code\": \"JOB_POST_NOT_FOUND\",\n" +
			"    \"message\": \"해당하는 공고를 찾을 수 없습니다.\",\n" +
			"    \"result\": null\n" +
			"}")))
	})
	public ApiResponse<ApplicationResponseDTO.RegistrationResultDTO> registerUserApplicationByDelegate(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "지원서 제출 요청 DTO",
            required = true
        )
		@RequestBody @Valid ApplicationRequestDTO.DelegateRegistrationRequestDTO requestDTO) {
		return ApiResponse.onSuccess(applicationCommandService.createDelegateApplication(requestDTO, userDetails.getUser()));
	}

	@PostMapping("/direct")
	@Operation(summary = "사용자 지원서 직접 최종 제출 or 임시저장 API 직접", description = "사용자가 특정 채용 공고에 직접 지원서를 제출하는 API입니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value =
			"{\n" +
				"    \"isSuccess\": true,\n" +
				"    \"code\": \"COMMON200\",\n" +
				"    \"message\": \"성공입니다.\",\n" +
				"    \"result\": {\n" +
				"        \"applicationId\": 1,\n" +
				"        \"jobPostId\": 1,\n" +
				"        \"applicationStatus\": \"SUBMITTED\",\n" +
				"        \"createdAt\": \"2025-08-03T15:00:00\",\n" +
				"        \"answers\": [\n" +
				"            {\n" +
				"                \"fieldType\": \"TEXT\",\n" +
				"                \"formFieldId\": 1,\n" +
				"                \"formFieldName\": \"성함\",\n" +
				"                \"answer\": \"김시니어\"\n" +
				"            },\n" +
				"            {\n" +
				"                \"fieldType\": \"IMAGE\",\n" +
				"                \"formFieldId\": 2,\n" +
				"                \"formFieldName\": \"경력 증명서\",\n" +
				"                \"answer\": [\n" +
				"                    {\n" +
				"                        \"keyName\": \"a1b2c3d4\",\n" +
				"                        \"originalFileName\": \"career_certificate.png\"\n" +
				"                    }\n" +
				"                ]\n" +
				"            }\n" +
				"        ]\n" +
				"    }\n" +
				"}"))),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 공고 또는 사용자", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value =
			"{\n" +
				"    \"isSuccess\": false,\n" +
				"    \"code\": \"JOB_POST_NOT_FOUND\",\n" +
				"    \"message\": \"해당하는 공고를 찾을 수 없습니다.\",\n" +
				"    \"result\": null\n" +
				"}")))
	})
	public ApiResponse<ApplicationResponseDTO.RegistrationResultDTO> registerUserApplicationDirect(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "지원서 제출 요청 DTO",
			required = true
		)
		@RequestBody @Valid ApplicationRequestDTO.DirectRegistrationRequestDTO requestDTO) {
		return ApiResponse.onSuccess(applicationCommandService.createDirectApplication(requestDTO, userDetails.getUser()));
	}


	@PutMapping("/{applicationId}")
	@Operation(summary = "사용자 지원서 임시 저장 -> 최종 제출로 변경", description = "사용자가 임시 저장했던 지원서를 최종 제출로 변경하는 API")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value =
			"{\n" +
				"    \"isSuccess\": true,\n" +
				"    \"code\": \"COMMON200\",\n" +
				"    \"message\": \"성공입니다.\",\n" +
				"    \"result\": {\n" +
				"        \"applicationId\": 1,\n" +
				"        \"jobPostId\": 1,\n" +
				"        \"applicationStatus\": \"SUBMITTED\",\n" +
				"        \"createdAt\": \"2025-08-03T15:00:00\",\n" +
				"        \"answers\": [\n" +
				"            {\n" +
				"                \"fieldType\": \"TEXT\",\n" +
				"                \"formFieldId\": 1,\n" +
				"                \"formFieldName\": \"성함\",\n" +
				"                \"answer\": \"김시니어\"\n" +
				"            },\n" +
				"            {\n" +
				"                \"fieldType\": \"IMAGE\",\n" +
				"                \"formFieldId\": 2,\n" +
				"                \"formFieldName\": \"경력 증명서\",\n" +
				"                \"answer\": [\n" +
				"                    {\n" +
				"                        \"keyName\": \"a1b2c3d4\",\n" +
				"                        \"originalFileName\": \"career_certificate.png\"\n" +
				"                    }\n" +
				"                ]\n" +
				"            }\n" +
				"        ]\n" +
				"    }\n" +
				"}"))),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 공고 또는 사용자", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value =
			"{\n" +
				"    \"isSuccess\": false,\n" +
				"    \"code\": \"JOB_POST_NOT_FOUND\",\n" +
				"    \"message\": \"해당하는 공고를 찾을 수 없습니다.\",\n" +
				"    \"result\": null\n" +
				"}")))
	})
		public ApiResponse<ApplicationResponseDTO.RegistrationResultDTO> updateTmpApplicationToFinalApplication(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable Long applicationId,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "지원서 최종제출 요청 DTO",
			required = true
		)
		@RequestBody @Valid ApplicationRequestDTO.TmpToFinalRegistrationRequestDTO requestDTO) {
		return ApiResponse.onSuccess(applicationCommandService.updateTmpApplicationToFinalApplication(requestDTO, applicationId, userDetails.getUser()));
	}

	@Operation(summary = "사용자가 자신이 작성한 제출된 지원서를 조회", description = "마이페이지에서 사용자가 자신이 작성한 제출된 지원서를 조회합니다.")
	@GetMapping("/me/submitted")
	public ApiResponse<ApplicationResponseDTO.SubmittedApplicationResponseListDTO> getSubmittedApplication(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@PageNumber Integer page
	) {
		Page<ApplicationResponseDTO.SubmittedApplicationResponseDTO> submittedApplicationResponseDTOList = applicationQueryService.getSubmittedApplication(customUserDetails.getUser(), page);

		return ApiResponse.onSuccess(ApplicationConverter.toSubmittedApplicationResponseListDTO(submittedApplicationResponseDTOList));
	}

	@Operation(summary = "사용자가 자신이 작성한 제출된 지원서 하나를 상세조회", description = "마이페이지에서 사용자가 자신이 작성한 제출된 지원서하나를 상세 조회합니다.")
	@GetMapping("/me/details/{applicationId}")
	public ApiResponse<ApplicationResponseDTO.SubmittedApplicationQnADetailResponseDTO> getSubmittedApplicationDetails(
		@PathVariable Long applicationId,
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {
		return ApiResponse.onSuccess(applicationQueryService.getSubmittedApplicationDetails(applicationId, customUserDetails.getId()));
	}

	@Operation(summary = "[시니어] 일자리 신청함 - 목록 조회", description = "로그인한 본인의 신청함을 조회합니다. 각 신청서에 해당하는 채용공고는 시니어 채용공고 상세 조회 API를 이용해주세요. NON_STARTED(작성 전), DRAFT(임시저장) 신청서 기준")
	@GetMapping("/mine")
	public ApiResponse<List<ApplicationResponseDTO.ApplicationJobPostStatusDTO>> getMyApplications(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ApiResponse.onSuccess(applicationQueryService.getApplicationsForSenior(userDetails.getUser().getId()));
	}

	@Operation(summary = "[시니어] 일자리 저장함 - 신청 버튼", description = " 추천/상세 화면에서 '신청' 클릭 시 호출합니다. 해당 채용공고에 대한 신청이 없으면 NON_STARTED(작성 전) 상태로 생성합니다. 생성만 하고 끝나며, 목록은 /applications/mine에서 별도 조회하세요.")
	@PostMapping("/ensure")
	public ApiResponse<Long> ensureApplication(@Parameter(description = "신청할 채용공고 ID", example = "1") @RequestParam Long jobPostId, @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
		Long applicationId = applicationCommandService.createIfNotExists(jobPostId, userDetails).getId();
		return ApiResponse.of(SuccessStatus.APPLICATION_NON_STARTED_CREATED, applicationId);
	}

	@Operation(summary = "[시니어] 일자리 신청함 - 신청 삭제", description = "본인 신청서를 삭제합니다.")
	@DeleteMapping("/{applicationId}")
	public ApiResponse<SuccessStatus> deleteApplication( @Parameter(description = "삭제할 신청서 ID", example = "1")@PathVariable Long applicationId, @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
		applicationCommandService.deleteApplication(applicationId, userDetails.getUser().getId());
		return ApiResponse.onSuccess(SuccessStatus.APPLICATION_DELETED);
	}

	@Operation(summary = "[보호자] 일자리 신청함 - 연결 시니어 신청함 목록 조회", description = "보호자 계정으로 로그인 시, 연결된 모든 시니어의 신청 목록을 조회합니다. 각 채용공고는 보호자 채용공고 상세조회 API에서 seniorId와 jobPostId를 전달해 호출하세요.")
	@GetMapping("/protector")
	public ApiResponse<List<ApplicationResponseDTO.ProtectorApplicationJobPostDTO>> getProtectorApplications(
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		Long protectorId = userDetails.getUser().getId();
		return ApiResponse.onSuccess(applicationQueryService.getProtectorApplications(protectorId));
	}

}
