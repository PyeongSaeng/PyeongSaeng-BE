package com.umc.pyeongsaeng.domain.job.controller;

import com.umc.pyeongsaeng.domain.company.repository.CompanyRepository;
import com.umc.pyeongsaeng.domain.job.converter.JobPostConverter;
import com.umc.pyeongsaeng.domain.job.dto.request.JobPostRequestDTO;
import com.umc.pyeongsaeng.domain.job.dto.response.JobPostResponseDTO;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import com.umc.pyeongsaeng.domain.job.enums.JobPostState;
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
import org.springframework.web.bind.annotation.*;

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
                                "id": 3,
                                "state": "RECRUITING",
                                "title": "바리스타 구인",
                                "address": "평생요양원",
                                "detailAddress": "서울특별시 중구세종대로 110",
                                "roadAddress": "110",
                                "zipcode": "04538",
                                "hourlyWage": 11000,
                                "monthlySalary": 2300000,
                                "yearSalary": null,
                                "description": "커피를 사랑하는 동료모집",
                                "workingTime": "10:00 - 17:00",
                                "deadline": "2025-09-30",
                                "recruitCount": 1,
                                "note": "라떼 아트 가능자 특별 우대",
                                "jobPostImageId": [{
                                    "jobPostId": 3,
                                    "keyName": "example_image.png"
                                }]
						      }
						    ],
						    "listSize": 1,
						    "totalPage": 1,
						    "totalElements": 1,
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
	@GetMapping("/companies/me/posts")
	public ApiResponse<JobPostResponseDTO.JobPostPreviewListDTO> getJobPost(
		@Parameter(name = "page", description = "페이지 번호 (1부터 시작)", example = "1") @PageNumber Integer page,
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "state", required = false, defaultValue = "RECRUITING") JobPostState jobPostState) {

		Page<JobPost> jobPostList = jobPostQueryService.getJobPostList(userDetails.getCompany(), page, jobPostState);
		return ApiResponse.onSuccess(JobPostConverter.toJobPostPreviewListDTO(jobPostList));
	}

	@Operation(summary = "채용공고 상세 조회 API", description = "특정 채용공고를 클릭했을 때, 해당 공고의 상세 정보를 조회하는 API입니다.")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "채용공고 상세 조회 성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(
					name = "SuccessExample",
					value = """
                {
                  "isSuccess": true,
                  "code": "COMMON200",
                  "message": "성공입니다.",
                  "result": {
                    "title": "아파트 단지 경비원 모집",
                    "address": "서울시 서초구",
                    "detailAddress": "서울시 서초구 반포동 123",
                    "roadAddress": "서울특별시 서초구 반포대로 45",
                    "zipcode": "06545",
                    "hourlyWage": 11000,
                    "monthlySalary": null,
                    "yearSalary": null,
                    "description": "아파트 단지에서 근무할 경비원을 모집합니다.",
                    "workingTime": "06:00 ~ 18:00 (주간) / 18:00 ~ 06:00 (야간)",
                    "deadline": "2025-08-31",
                    "recruitCount": 2,
                    "note": "근무복 지급, 휴게 공간 제공",
                    "images": [
                      {
                        "jobPostId": 1,
                        "keyName": "3e4fd-28df-4cfc-9846-231389d_바다.jpg",
                        "imageUrl": "https://pyeongsaeng-bucket.s3.amazonaws.com/...."
                      }
                    ],
                    "travelTime": "도보 + 지하철 35분"
                  }
                }
                """
				)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "유저 또는 근무지의 위치 정보(위도/경도)가 잘못되었거나, 해당 지역에서는 대중교통 경로를 찾을 수 없습니다.",
			content = @io.swagger.v3.oas.annotations.media.Content(
				mediaType = "application/json",
				schema = @io.swagger.v3.oas.annotations.media.Schema(
					implementation = ApiResponse.class
				),
				examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
					name = "RouteNotFound",
					value = """
                {
                  "isSuccess": false,
                  "code": "ROUTE_NOT_FOUND",
                  "message": "출발지/도착지 좌표가 잘못되었거나, 요청 위치에서는 대중교통 이동 경로를 지원하지 않습니다.",
                  "result": null
                }
                """
				)
			)
		)
	})
	@GetMapping("/posts/{jobPostId}")
	public ApiResponse<JobPostResponseDTO.JobPostDetailDTO> getJobPostDetail(
		@Parameter(name = "jobPostId", description = "조회할 채용공고 ID", example = "1")
		@RequestParam Long jobPostId,
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails){
		return ApiResponse.onSuccess(jobPostQueryService.getJobPostDetail(jobPostId, userDetails.getUser().getId()));
	}


}
