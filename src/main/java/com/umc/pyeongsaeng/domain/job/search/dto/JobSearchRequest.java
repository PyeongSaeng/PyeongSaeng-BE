package com.umc.pyeongsaeng.domain.job.search.dto;

import java.util.List;

import com.umc.pyeongsaeng.domain.job.search.enums.JobSortType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "채용공고 검색 요청 DTO")
public class JobSearchRequest {

	@Schema(description = "검색 키워드", example = "바리스타", nullable = true)
	private String keyword;

	@Schema(
		description = """
            정렬 기준
            - DISTANCE_ASC: 거리순 (기본값)
            - POPULARITY_DESC: 인기순
            """,
		example = "DISTANCE_ASC",
		required = true
	)
	private JobSortType sort;

	@Schema(description = "사용자의 위도", example = "37.5665", nullable = true)
	private Double lat;

	@Schema(description = "사용자의 경도", example = "126.9780", nullable = true)
	private Double lon;

	@Schema(description = "searchAfter 기반 페이징 필드", nullable = true)
	private List<Object> searchAfter;

	@Schema(description = "페이지당 결과 개수", example = "10", defaultValue = "10")
	private int size = 10;
}

