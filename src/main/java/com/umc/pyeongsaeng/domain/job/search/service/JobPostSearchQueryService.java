package com.umc.pyeongsaeng.domain.job.search.service;

import com.umc.pyeongsaeng.domain.job.search.dto.request.JobSearchRequest;
import com.umc.pyeongsaeng.domain.job.search.dto.response.JobSearchResult;

public interface JobPostSearchQueryService {
	/**
	 * 채용공고를 검색합니다.
	 *
	 * @param request 검색 조건을 담은 요청 객체
	 * @param seniorId 검색을 수행하는 시니어 회원의 ID
	 * @return 검색 결과
	 */
	JobSearchResult search(JobSearchRequest request, Long seniorId);
}
