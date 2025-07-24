package com.umc.pyeongsaeng.domain.job.recommendation.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import com.umc.pyeongsaeng.domain.job.recommendation.dto.RecommendationResponse;
import com.umc.pyeongsaeng.domain.job.recommendation.util.DistanceUtil;
import com.umc.pyeongsaeng.domain.job.repository.JobPostRepository;
import com.umc.pyeongsaeng.domain.senior.entity.SeniorProfile;
import com.umc.pyeongsaeng.domain.senior.repository.SeniorProfileRepository;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobPostRecommendationService {

	private final JobPostRepository jobPostRepository;
	private final SeniorProfileRepository seniorProfileRepository;

	public List<RecommendationResponse> recommendJobsByDistance(Long userId) {
		// 1. 유저의 SeniorProfile 가져오기
		SeniorProfile profile = seniorProfileRepository.findBySeniorId(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		double userLat = profile.getLatitude();
		double userLng = profile.getLongitude();

		// 2. 전체 JobPost 가져오기
		List<JobPost> jobPosts = jobPostRepository.findAll();

		// 3. 거리 계산 후 정렬
		return jobPosts.stream()
			.filter(job -> job.getLatitude() != null && job.getLongitude() != null)
			.map(job -> {
				double distance = DistanceUtil.calculateDistance(userLat, userLng, job.getLatitude(), job.getLongitude());
				return RecommendationResponse.from(job, distance);
			})
			.sorted(Comparator.comparingDouble(RecommendationResponse::distanceKm))
			.limit(10) // 원하는 개수만큼 제한 (선택)
			.collect(Collectors.toList());
	}
}
