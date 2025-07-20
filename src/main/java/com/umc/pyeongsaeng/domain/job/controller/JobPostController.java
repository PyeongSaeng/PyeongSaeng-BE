package com.umc.pyeongsaeng.domain.job.controller;

import com.umc.pyeongsaeng.domain.job.converter.JobPostConverter;
import com.umc.pyeongsaeng.domain.job.dto.request.JobPostRequestDTO;
import com.umc.pyeongsaeng.domain.job.dto.response.JobPostResponseDTO;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import com.umc.pyeongsaeng.domain.job.service.JobPostCommandService;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "채용 API", description = "채용공고 관련 API")
@RequestMapping("/api/job")
@RestController
@RequiredArgsConstructor
public class JobPostController {

	private final JobPostCommandService jobPostCommandService;

	@Operation(summary = "채용공고 생성 API", description = "기업이 새로운 채용공고를 생성하는 API입니다.")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "채용공고 생성 성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ApiResponse.class),
				examples = {
					@ExampleObject(
						name = "시급제 공고 예시",
						summary = "카페 바리스타와 같은 시급제 채용공고 생성 예시입니다.",
						value = """
                             {
                               "isSuccess": true,
                               "code": "200",
                               "message": "요청에 성공하였습니다.",
                               "result": {
                                 "title": "주말 시니어 바리스타 채용",
                                 "address": "서울시 마포구",
                                 "detailAddress": "월드컵북로 396, 101호",
                                 "roadAddress": "서울특별시 마포구 월드컵북로 396",
                                 "zipcode": "03925",
                                 "hourlyWage": 13000,
                                 "monthlySalary": null,
                                 "yearSalary": null,
                                 "description": "활기찬 주말을 함께할 시니어 바리스타를 찾습니다. 경력은 중요하지 않습니다.",
                                 "workingTime": "매주 토, 일 10:00 ~ 16:00",
                                 "deadline": "2025-08-15",
                                 "recruitCount": 1,
                                 "note": "앞치마 및 유니폼 제공",
                                 "jobPostImageId": [
                                   {
                                     "keyName": "cafe_weekend_barista.png"
                                   }
                                 ]
                               }
                             }
                             """
					),
				}
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "채용공고 생성 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(
					name = "UnauthorizedExample",
					value = "{\"isSuccess\": false, \"code\": \"AUTH401\", \"message\": \"인증되지 않은 사용자입니다.\", \"result\": null}"
				)
			)
		)
	})
	@PostMapping("/posts")
	public ApiResponse<JobPostResponseDTO.JobPostPreviewDTO> createJobPost(
		@RequestBody JobPostRequestDTO.CreateDTO requestDTO,
		@Parameter(hidden = true) @AuthenticationPrincipal Long companyId) {
		JobPost newJobPost = jobPostCommandService.createJobPost(requestDTO, companyId);
		return ApiResponse.onSuccess(JobPostConverter.JobPostPreviewDTO(newJobPost));
	}
}

