package com.umc.pyeongsaeng.domain.job.search.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobSearchResult {
	private List<JobSearchResponse> results;
	private List<Object> searchAfter;
	private long totalCount;	// 전체 검색 결과 개수
	private boolean hasNext;	// 다음 페이지 여부
}
