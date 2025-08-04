package com.umc.pyeongsaeng.domain.job.recommendation.service;

import com.umc.pyeongsaeng.domain.job.recommendation.dto.response.RecommendationResponseDTO;
import com.umc.pyeongsaeng.domain.job.recommendation.util.DistanceUtil;
import com.umc.pyeongsaeng.domain.job.repository.JobPostRepository;
import com.umc.pyeongsaeng.domain.senior.entity.SeniorProfile;
import com.umc.pyeongsaeng.domain.senior.repository.SeniorProfileRepository;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

	private final JobPostRepository jobPostRepository;
	private final SeniorProfileRepository seniorProfileRepository;

	@Override
	public List<RecommendationResponseDTO> recommendJobsByDistance(Long userId) {
		SeniorProfile profile = seniorProfileRepository.findBySeniorId(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		double userLat = profile.getLatitude();
		double userLng = profile.getLongitude();

		return jobPostRepository.findAll().stream()
			.filter(job -> job.getLatitude() != null && job.getLongitude() != null)
			.map(job -> {
				double distance = DistanceUtil.calculateDistance(userLat, userLng, job.getLatitude(), job.getLongitude());
				return RecommendationResponseDTO.from(job, distance);
			})
			.sorted(Comparator.comparingDouble(RecommendationResponseDTO::distanceKm))
			.limit(10)
			.collect(Collectors.toList());
	}
}
