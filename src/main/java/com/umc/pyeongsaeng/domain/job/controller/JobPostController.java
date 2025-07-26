package com.umc.pyeongsaeng.domain.job.controller;

import com.umc.pyeongsaeng.domain.company.entity.Company;
import com.umc.pyeongsaeng.domain.company.repository.CompanyRepository;
import com.umc.pyeongsaeng.domain.job.converter.JobPostConverter;
import com.umc.pyeongsaeng.domain.job.dto.request.JobPostRequestDTO;
import com.umc.pyeongsaeng.domain.job.dto.response.JobPostResponseDTO;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import com.umc.pyeongsaeng.domain.job.service.JobPostCommandService;
import com.umc.pyeongsaeng.domain.job.service.JobPostQueryService;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;
import com.umc.pyeongsaeng.global.resolvation.annotation.PageNumber;
import com.umc.pyeongsaeng.global.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "채용 API", description = "채용공고 관련 API")
@RequestMapping("/api/job")
@RestController
@RequiredArgsConstructor
public class JobPostController {

	private final JobPostCommandService jobPostCommandService;
	private final JobPostQueryService jobPostQueryService;
	private final CompanyRepository companyRepository;

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
		return ApiResponse.onSuccess(JobPostConverter.toJobPostPreviewDTO(newJobPost));
	}

	@Operation(summary = "채용공고 목록 조회 API", description = "기업이 자신이 등록한 채용공고 목록을 페이징하여 조회하는 API입니다.")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "채용공고 목록 조회 성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(
					name = "SuccessExample",
					value = """
						{
						  "isSuccess": true,
						  "code": "200",
						  "message": "요청에 성공하였습니다.",
						  "result": {
						    "jobPostList": [
						      {
						        "jobPostId": 1,
						        "title": "시니어 백엔드 개발자 모집",
						        "imageUrl": "https://pyeongsaeng-s3.s3.ap-northeast-2.amazonaws.com/company/pyeongsaeng_logo.png",
						        "companyName": "평생전자",
						        "address": "서울시 강남구",
						        "deadline": "2025-08-31",
						        "createdAt": "2025-07-25T10:00:00"
						      },
						      {
						        "jobPostId": 2,
						        "title": "경력 무관! 마케팅 전문가 모집",
						        "imageUrl": "https://pyeongsaeng-s3.s3.ap-northeast-2.amazonaws.com/company/eussya_logo.png",
						        "companyName": "으쌰으쌰컴퍼니",
						        "address": "서울시 서초구",
						        "deadline": "2025-09-15",
						        "createdAt": "2025-07-25T11:00:00"
						      }
						    ],
						    "listSize": 2,
						    "totalPage": 1,
						    "totalElements": 2,
						    "isFirst": true,
						    "isLast": true
						  }
						}
						"""
				)
			)
		),
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
	@GetMapping("/posts")
	public ApiResponse<JobPostResponseDTO.JobPostPreviewListDTO> getJobPost(
		@Parameter(name = "page", description = "페이지 번호 (1부터 시작)", example = "1") @PageNumber Integer page,
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

		// Company company = companyRepository.findById(companyId).orElse(null);
		// Page<JobPost> jobPostList =  jobPostQueryService.getJobPostList(company, page);
		Page<JobPost> jobPostList = jobPostQueryService.getJobPostList(userDetails.getCompany(), page);
		return ApiResponse.onSuccess(JobPostConverter.toJobPostPreviewListDTO(jobPostList));
	}
}
