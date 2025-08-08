package com.umc.pyeongsaeng.domain.job.controller;

import com.umc.pyeongsaeng.domain.job.converter.FormFieldConverter;
import com.umc.pyeongsaeng.domain.job.converter.JobPostConverter;
import com.umc.pyeongsaeng.domain.job.dto.request.JobPostRequestDTO;
import com.umc.pyeongsaeng.domain.job.dto.response.FormFieldResponseDTO;
import com.umc.pyeongsaeng.domain.job.dto.response.JobPostResponseDTO;
import com.umc.pyeongsaeng.domain.job.entity.FormField;
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

import java.util.List;

@Tag(name = "채용 API", description = "채용공고 관련 API")
@RequestMapping("/api/job")
@RestController
@RequiredArgsConstructor
public class JobPostController {

	private final JobPostCommandService jobPostCommandService;
	private final JobPostQueryService jobPostQueryService;

	@Operation(summary = "회사가 채용공고 생성 API", description = "기업이 새로운 채용공고를 생성하는 API입니다.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
		description = "회사가 채용공고 생성 요청 DTO",
		required = true,
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = JobPostRequestDTO.CreateDTO.class),
			examples = @ExampleObject(
				name = "채용공고 생성 예시",
				summary = "폼 필드를 포함한 채용공고 생성 예시입니다.",
				value = """
					{
					  "title": "시니어 돌보미 채용",
					  "address": "서울특별시 강남구",
					  "detailAddress": "테헤란로 212",
					  "roadAddress": "서울특별시 강남구 테헤란로 212",
					  "zipcode": "06222",
					  "hourlyWage": 15000,
					  "monthlySalary": null,
					  "yearSalary": null,
					  "description": "어르신과 함께 즐거운 시간을 보내실 분을 찾습니다. 주 3회, 오후 시간에 근무하며, 식사 준비 및 말벗이 주된 업무입니다.",
					  "workingTime": "월, 수, 금 14:00 ~ 18:00",
					  "deadline": "2025-08-31",
					  "recruitCount": 1,
					  "note": "경력자 우대",
					  "jobPostImageList": [
					    {
					      "keyName": "file5123",
					      "originalFileName": "job_post_image_1.jpg"
					    },
					    {
					      "keyName": "file5126",
					      "originalFileName": "job_post_image_2.png"
					    }
					  ],
					  "formFieldList": [
					    {
					      "fieldName": "성함",
					      "fieldType": "TEXT"
					    },
					    {
					      "fieldName": "연락처",
					      "fieldType": "TEXT"
					    },
					    {
					      "fieldName": "자기소개",
					      "fieldType": "TEXT"
					    },
					    {
					      "fieldName": "경력 유무",
					      "fieldType": "IMAGE"
					    }
					  ]
					}
					"""
			)
		)
	))
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "채용공고 생성 성공",
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
						    "id": 2,
						    "state": "RECRUITING",
						    "title": "시니어 돌보미 채용",
						    "address": "서울특별시 강남구",
						    "detailAddress": "테헤란로 212",
						    "roadAddress": "서울특별시 강남구 테헤란로 212",
						    "zipcode": "06222",
						    "hourlyWage": 15000,
						    "monthlySalary": null,
						    "yearSalary": null,
						    "description": "어르신과 함께 즐거운 시간을 보내실 분을 찾습니다. 주 3회, 오후 시간에 근무하며, 식사 준비 및 말벗이 주된 업무입니다.",
						    "workingTime": "월, 수, 금 14:00 ~ 18:00",
						    "deadline": "2025-08-31",
						    "recruitCount": 1,
						    "note": "경력자 우대",
						    "jobPostImageList": [
						      {
						        "imageId": 3,
						        "keyName": "image_key_124",
						        "originalFileName": "job_post_image_1.jpg"
						      },
						      {
						        "imageId": 4,
						        "keyName": "image_key_2.png",
						        "originalFileName": "job_post_image_2.png"
						      }
						    ],
						    "formFields": [
						      {
						        "id": 5,
						        "fieldName": "성함",
						        "fieldType": "TEXT"
						      },
						      {
						        "id": 6,
						        "fieldName": "연락처",
						        "fieldType": "TEXT"
						      },
						      {
						        "id": 7,
						        "fieldName": "자기소개",
						        "fieldType": "TEXT"
						      },
						      {
						        "id": 8,
						        "fieldName": "경력 유무",
						        "fieldType": "IMAGE"
						      }
						    ]
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
	@PostMapping("/posts")
	public ApiResponse<JobPostResponseDTO.JobPostPreviewDTO> createJobPost(
		@RequestBody JobPostRequestDTO.CreateDTO requestDTO,
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
		JobPost newJobPost = jobPostCommandService.createJobPost(requestDTO, userDetails.getCompany());
		return ApiResponse.onSuccess(JobPostConverter.toJobPostPreviewDTO(newJobPost));
	}

	@Operation(summary = "회사가 채용공고 수정 API", description = "기업이 자신이 등록한 채용공고를 수정하는 API입니다. PUT API로 데이터를 전부 넣어줘야 합니다.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "회사가 채용공고 수정 요청 DTO",
			required = true,
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = JobPostRequestDTO.UpdateDTO.class),
				examples = @ExampleObject(
					name = "채용공고 수정 예시",
					summary = "폼 필드를 포함한 채용공고 수정 예시입니다.",
					value = """
						{
						  "title": "시니어 돌보미 채용",
						  "address": "서울특별시 강남구",
						  "detailAddress": "테헤란로 212",
						  "roadAddress": "서울특별시 강남구 테헤란로 212",
						  "zipcode": "06222",
						  "hourlyWage": 15000,
						  "monthlySalary": null,
						  "yearSalary": null,
						  "description": "어르신과 함께 즐거운 시간을 보내실 분을 찾습니다. 주 3회, 오후 시간에 근무하며, 식사 준비 및 말벗이 주된 업무입니다.",
						  "workingTime": "월, 수, 금 14:00 ~ 18:00",
						  "deadline": "2025-08-31",
						  "recruitCount": 1,
						  "note": "경력자 우대",
						  "jobPostImageList": [
						    {
						      "keyName": "image_key_1.jpg43",
						      "originalFileName": "job_post_image_1.jpg"
						    },
						    {
						      "keyName": "image_key_2.png43",
						      "originalFileName": "job_post_image_2.png"
						    }
						  ],
						  "formFieldList": [
						    {
						      "fieldName": "성함",
						      "fieldType": "TEXT"
						    },
						    {
						      "fieldName": "연락처",
						      "fieldType": "TEXT"
						    },
						    {
						      "fieldName": "자기소개",
						      "fieldType": "TEXT"
						    },
						    {
						      "fieldName": "경력 유무",
						      "fieldType": "IMAGE"
						    }
						  ]
						}
						"""
				)
			)
		))
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "채용공고 수정 성공",
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
						     "id": 5,
						     "state": "RECRUITING",
						     "title": "시니어 돌보미 채용",
						     "address": "서울특별시 강남구",
						     "detailAddress": "테헤란로 212",
						     "roadAddress": "서울특별시 강남구 테헤란로 212",
						     "zipcode": "06222",
						     "hourlyWage": 15000,
						     "monthlySalary": null,
						     "yearSalary": null,
						     "description": "어르신과 함께 즐거운 시간을 보내실 분을 찾습니다. 주 3회, 오후 시간에 근무하며, 식사 준비 및 말벗이 주된 업무입니다.",
						     "workingTime": "월, 수, 금 14:00 ~ 18:00",
						     "deadline": "2025-08-31",
						     "recruitCount": 1,
						     "note": "경력자 우대",
						     "jobPostImageList": [
						       {
						         "imageId": 17,
						         "keyName": "image_key_1.jpg43",
						         "originalFileName": "job_post_image_1.jpg"
						       },
						       {
						         "imageId": 18,
						         "keyName": "image_key_2.png43",
						         "originalFileName": "job_post_image_2.png"
						       }
						     ],
						     "formFieldList": [
						       {
						         "id": 33,
						         "fieldName": "성함",
						         "fieldType": "TEXT"
						       },
						       {
						         "id": 34,
						         "fieldName": "연락처",
						         "fieldType": "TEXT"
						       },
						       {
						         "id": 35,
						         "fieldName": "자기소개",
						         "fieldType": "TEXT"
						       },
						       {
						         "id": 36,
						         "fieldName": "경력 유무",
						         "fieldType": "IMAGE"
						       }
						     ]
						   }
						 }
						"""
				)
			)
		),
	})
	@PutMapping("/posts/{jobPostId}")
	public ApiResponse<JobPostResponseDTO.JobPostPreviewDTO> updateJobPost(
		@PathVariable Long jobPostId,
		@RequestBody JobPostRequestDTO.UpdateDTO requestDTO) {
		JobPost updatedJobPost = jobPostCommandService.updateJobPost(jobPostId, requestDTO);
		return ApiResponse.onSuccess(JobPostConverter.toJobPostPreviewDTO(updatedJobPost));
	}

	@Operation(summary = "회사가 채용공고 상세보기 API", description = "기업이 자신이 등록한 채용공고를 상세볼 수 있는 API 입니다.")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "채용공고 수정 성공",
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
						     "id": 5,
						     "state": "RECRUITING",
						     "title": "시니어 돌보미 채용",
						     "address": "서울특별시 강남구",
						     "detailAddress": "테헤란로 212",
						     "roadAddress": "서울특별시 강남구 테헤란로 212",
						     "zipcode": "06222",
						     "hourlyWage": 15000,
						     "monthlySalary": null,
						     "yearSalary": null,
						     "description": "어르신과 함께 즐거운 시간을 보내실 분을 찾습니다. 주 3회, 오후 시간에 근무하며, 식사 준비 및 말벗이 주된 업무입니다.",
						     "workingTime": "월, 수, 금 14:00 ~ 18:00",
						     "deadline": "2025-08-31",
						     "recruitCount": 1,
						     "note": "경력자 우대",
						     "jobPostImageList": [
						       {
						         "imageId": 17,
						         "keyName": "image_key_1.jpg43",
						         "originalFileName": "job_post_image_1.jpg"
						       },
						       {
						         "imageId": 18,
						         "keyName": "image_key_2.png43",
						         "originalFileName": "job_post_image_2.png"
						       }
						     ],
						     "formFieldList": [
						       {
						         "id": 33,
						         "fieldName": "성함",
						         "fieldType": "TEXT"
						       },
						       {
						         "id": 34,
						         "fieldName": "연락처",
						         "fieldType": "TEXT"
						       },
						       {
						         "id": 35,
						         "fieldName": "자기소개",
						         "fieldType": "TEXT"
						       },
						       {
						         "id": 36,
						         "fieldName": "경력 유무",
						         "fieldType": "IMAGE"
						       }
						     ]
						   }
						 }
						"""
				)
			)
		),
	})
	@GetMapping("/posts/{jobPostId}/detail")
	public ApiResponse<JobPostResponseDTO.JobPostPreviewDTO> updateJobPost(
		@PathVariable Long jobPostId) {
		JobPost searchedJobPost = jobPostCommandService.getJobPostDetail(jobPostId);
		return ApiResponse.onSuccess(JobPostConverter.toJobPostPreviewDTO(searchedJobPost));
	}

	@Operation(summary = "회사의 채용공고 목록 조회 API", description = "회사가 쓴 채용 공고 목록을 조회하는 API입니다.")
	@GetMapping("/companies/me/posts")
	public ApiResponse<JobPostResponseDTO.JobPostPreviewByCompanyListDTO> getJobPost(
		@Parameter(name = "page", description = "페이지 번호 (1부터 시작)", example = "1") @PageNumber Integer page,
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "state", required = false, defaultValue = "RECRUITING") JobPostState jobPostState) {

		Page<JobPostResponseDTO.JobPostPreviewByCompanyDTO> jobPostList = jobPostQueryService.getJobPostPreViewPageByCompany(userDetails.getCompany(), page, jobPostState);
		return ApiResponse.onSuccess(JobPostConverter.toJobPostPreviewByCompanyListDTO(jobPostList));
	}

@Operation(summary = "채용공고 지원서 질문 목록 조회 API", description = "채용공고 지원서의 질문 목록을 조회하는 API입니다.")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "질문 목록 조회 성공",
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
						    "formFieldList": [
						      {
						        "formField": "이름",
						        "fieldType": "TEXT"
						      },
						      {
						        "formField": "연락처",
						        "fieldType": "TEXT"
						      },
						      {
						        "formField": "자기소개",
						        "fieldType": "TEXT"
						      },
						      {
						        "formField": "자기소개",
						        "fieldType": "IMAGE"
						      }
						    ]
						  }
						}
						"""
				)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 채용공고",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(
					name = "NotFoundExample",
					value = "{\"isSuccess\": false, \"code\": \"JOBPOST404\", \"message\": \"존재하지 않는 채용공고입니다.\", \"result\": null}"
				)
			)
		)
	})
	@GetMapping("/{jobPostId}/questions")
	public ApiResponse<FormFieldResponseDTO.FormFieldPreViewListDTO> getJobPostQuestions(
		@Parameter(name = "jobPostId", description = "채용공고 ID", example = "1") @PathVariable(name = "jobPostId") Long jobPostId) {

		List<FormField> formFieldList = jobPostQueryService.getFormFieldList(jobPostId);

		return ApiResponse.onSuccess(FormFieldConverter.toFormFieldPreViewListDTO(formFieldList));
	}

	@Operation(summary = "회사의 인기순 채용공고 목록 조회 API", description = "회사가 쓴 채용 공고 목록을 조회하는 API입니다.")
	@GetMapping("/companies/me/posts/popularity")
	public ApiResponse<JobPostResponseDTO.JobPostPreviewByCompanyListDTO> getJobPostByPopularity(
		@Parameter(name = "page", description = "페이지 번호 (1부터 시작)", example = "1") @PageNumber Integer page,
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

		Page<JobPostResponseDTO.JobPostPreviewByCompanyDTO> jobPostList = jobPostQueryService.getJobPostPreViewPageByCompanyByPopularity(userDetails.getCompany(), page);
		return ApiResponse.onSuccess(JobPostConverter.toJobPostPreviewByCompanyListDTO(jobPostList));
	}

	@DeleteMapping("/posts/{jobPostId}")
	@Operation(summary = "회사가 만들었던 공고 삭제", description = "회사가 만들었던 공고 삭제하는 API")
	public ApiResponse<Long> deleteJobPost(@PathVariable Long jobPostId) {
		jobPostCommandService.deleteJobPost(jobPostId);

		return ApiResponse.onSuccess(jobPostId);
	}
  
	@Operation(summary = "시니어가 채용공고 상세 조회 API", description = "특정 채용공고를 클릭했을 때, 해당 공고의 상세 정보를 조회하는 API입니다.")
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
