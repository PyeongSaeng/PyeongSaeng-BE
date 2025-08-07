package com.umc.pyeongsaeng.domain.job.search.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umc.pyeongsaeng.domain.job.search.service.JobPostSearchQueryService;
import com.umc.pyeongsaeng.domain.job.search.dto.request.JobSearchRequest;
import com.umc.pyeongsaeng.domain.job.search.dto.response.JobSearchResult;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;
import com.umc.pyeongsaeng.global.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobs")
public class JobPostSearchController {

	private final JobPostSearchQueryService jobPostSearchQueryService;

	@Operation(
		summary = "채용공고 검색 API",
		description = """
        📌 정렬 방식
        - 정렬: 거리순(DISTANCE_ASC, 기본값), 인기순(POPULARITY_DESC)
        - 검색 후 사용자가 정렬 기준 변경 가능

        📌 페이징 방식
        - 최초 요청 또는 정렬 기준 변경 시: searchAfter는 null
        - 이후 요청 시: 이전 응답의 searchAfter 값을 그대로 요청에 포함
        """
	)
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		required = true,
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = JobSearchRequest.class),
			examples = @ExampleObject(
				name = "최초 요청 예시",
				summary = "최초 요청 시 searchAfter는 null",
				value = """
            {
              "keyword": "서울",
              "sort": "DISTANCE_ASC",
              "searchAfter": null,
              "size": 10
            }
            """
			)
		)
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "채용공고 검색 성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(
					name = "성공 응답 예시",
					summary = "검색 결과 예시",
					value = """
                {
                  "isSuccess": true,
                  "code": "COMMON200",
                  "message": "성공입니다.",
                  "result": {
                    "results": [
                      {
                        "id": "1",
                        "title": "카페소반에서 바리스타 선생님을 모집합니다 (주말 근무)",
                        "address": "서울시 마포구",
                        "imageUrl": "https://pyeongsaeng-bucket.s3.amazonaws.com/..",
                        "displayDistance": "27.6km",
                        "applicationCount": 8
                      },
                      {
						"id": "2",
						"title": "건물 미화원 채용 (강동구)",
						"address": "서울시 강동구",
						"imageUrl": "https://pyeongsaeng-bucket.s3.amazonaws.com/..",
						"displayDistance": "7.10km",
						"applicationCount": 2
						}
                    ],
                    "searchAfter": [
                      27.559542440130187,
                      1754412156428
                    ],
                    "totalCount": 2,
                    "hasNext": false
                  }
                }
                """
				)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "잘못된 요청",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(
					name = "InvalidSortType",
					value = """
                {
                  "isSuccess": false,
                  "code": "INVALID_SORT_TYPE",
                  "message": "지원하지 않는 정렬 방식입니다.",
                  "result": null
                }
                """
				)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "500",
			description = "서버 내부 오류",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(
					name = "ElasticSearchConnectionError",
					value = """
                {
                  "isSuccess": false,
                  "code": "ES_CONNECTION_ERROR",
                  "message": "Elasticsearch 연결에 실패했습니다.",
                  "result": null
                }
                """
				)
			)
		)
	})
	@PostMapping("/search")
	public ApiResponse<JobSearchResult> search(@RequestBody JobSearchRequest request,
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
		JobSearchResult result = jobPostSearchQueryService.search(request, userDetails.getId());
		return ApiResponse.onSuccess(result);
	}
}
