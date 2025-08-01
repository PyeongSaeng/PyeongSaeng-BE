package com.umc.pyeongsaeng.domain.job.search.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobSearchResult {
	private List<JobSearchResponse> results;
	private List<Object> searchAfter;
}
