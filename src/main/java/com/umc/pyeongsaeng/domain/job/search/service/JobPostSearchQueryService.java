package com.umc.pyeongsaeng.domain.job.search.service;

import java.util.List;

import com.umc.pyeongsaeng.domain.job.search.dto.request.JobSearchRequest;
import com.umc.pyeongsaeng.domain.job.search.dto.response.JobSearchResult;
import com.umc.pyeongsaeng.domain.job.search.document.JobPostDocument;

public interface JobPostSearchQueryService {
	/**
	 * 채용공고를 검색합니다.
	 *
	 * @param request 검색 조건을 담은 요청 객체
	 * @param seniorId 검색을 수행하는 시니어 회원의 ID
	 * @return 검색 결과
	 */

	JobSearchResult search(JobSearchRequest request, Long seniorId);

	/**
	 * 선호 직무 키워드로 채용공고를 검색합니다.
	 *
	 * @param seniorId 시니어 회원 ID
	 * @return 직무 관련 채용공고 리스트
	 */
	List<JobPostDocument> searchByJobType(Long seniorId);
}
