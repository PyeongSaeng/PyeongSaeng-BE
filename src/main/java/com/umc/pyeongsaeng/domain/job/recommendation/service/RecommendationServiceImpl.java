package com.umc.pyeongsaeng.domain.job.recommendation.service;

import java.util.*;
import java.util.stream.*;

import org.springframework.stereotype.*;

import com.umc.pyeongsaeng.domain.job.entity.*;
import com.umc.pyeongsaeng.domain.job.recommendation.converter.*;
import com.umc.pyeongsaeng.domain.job.recommendation.dto.response.*;
import com.umc.pyeongsaeng.domain.job.recommendation.util.*;
import com.umc.pyeongsaeng.domain.job.repository.*;
import com.umc.pyeongsaeng.domain.job.search.document.*;
import com.umc.pyeongsaeng.domain.job.search.service.*;
import com.umc.pyeongsaeng.domain.senior.entity.*;
import com.umc.pyeongsaeng.domain.senior.repository.*;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.*;
import com.umc.pyeongsaeng.global.apiPayload.code.status.*;
import com.umc.pyeongsaeng.global.s3.service.*;

import lombok.*;
import lombok.extern.slf4j.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

	private final JobPostRepository jobPostRepository;
	private final JobPostImageRepository jobPostImageRepository;
	private final SeniorProfileRepository seniorProfileRepository;
	private final JobPostSearchQueryService jobPostSearchQueryService;
	private final S3Service s3Service;

	@Override
	public List<RecommendationResponseDTO> recommendJobsByDistance(Long userId) {
		SeniorProfile profile = findProfile(userId);
		double userLat = profile.getLatitude();
		double userLng = profile.getLongitude();

		return jobPostRepository.findAll().stream()
			.filter(job -> job.getLatitude() != null && job.getLongitude() != null)
			.map(job -> {
				double distance = roundDistance(userLat, userLng, job);
				String imageUrl = getPresignedImage(job.getId());

				return RecommendationConverter.toRecommendationResponseDTO(job, distance, imageUrl);
			})
			.sorted(Comparator.comparingDouble(RecommendationResponseDTO::distanceKm))
			.limit(10)
			.collect(Collectors.toList());
	}

	@Override
	public List<RecommendationResponseDTO> recommendJobsByJobTypeAndDistance(Long userId) {
		SeniorProfile profile = findProfile(userId);
		double userLat = profile.getLatitude();
		double userLng = profile.getLongitude();

		List<JobPostDocument> filteredDocs = jobPostSearchQueryService.searchByJobType(userId);

		if (filteredDocs.isEmpty()) {
			log.warn("[RECOMMEND] 직무 기반 결과 없음 → 거리 기준 추천 fallback");
			return recommendJobsByDistance(userId);
		}

		List<Long> jobPostIds = filteredDocs.stream()
			.map(doc -> Long.parseLong(doc.getId()))
			.toList();
		List<JobPost> jobPosts = jobPostRepository.findAllById(jobPostIds);

		return jobPosts.stream()
			.filter(job -> job.getLatitude() != null && job.getLongitude() != null)
			.map(job -> {
				double distance = roundDistance(userLat, userLng, job);
				String imageUrl = getPresignedImage(job.getId());

				return RecommendationConverter.toRecommendationResponseDTO(job, distance, imageUrl);
			})
			.sorted(Comparator.comparingDouble(RecommendationResponseDTO::distanceKm))
			.limit(10)
			.toList();
	}

	private SeniorProfile findProfile(Long userId) {
		return seniorProfileRepository.findBySeniorId(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
	}

	private String getPresignedImage(Long jobPostId) {
		return jobPostImageRepository.findFirstByJobPostIdOrderByIdAsc(jobPostId)
			.map(img -> {
				try {
					return s3Service.getPresignedToDownload(img.getKeyName()).getUrl();
				} catch (Exception e) {
					log.error("이미지 URL 생성 실패 - keyName: {}", img.getKeyName(), e);
					return null;
				}
			})
			.orElse(null);
	}

	private double roundDistance(double userLat, double userLng, JobPost job) {
		double raw = DistanceUtil.calculateDistance(userLat, userLng, job.getLatitude(), job.getLongitude());
		return Math.round(raw * 10.0) / 10.0;
	}
}
