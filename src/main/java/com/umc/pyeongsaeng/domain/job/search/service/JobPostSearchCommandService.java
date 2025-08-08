package com.umc.pyeongsaeng.domain.job.search.service;

public interface JobPostSearchCommandService {
	/**
	 * 특정 채용공고의 지원자 수를 갱신합니다.
	 *
	 * @param jobPostId 지원자 수를 갱신할 채용공고의 ID
	 * @param updatedCount 새로운 지원자 수
	 */
	void updateApplicationCount(Long jobPostId, int updatedCount);
}
