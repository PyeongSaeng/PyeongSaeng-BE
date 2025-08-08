package com.umc.pyeongsaeng.domain.job.recommendation.service;

import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import com.umc.pyeongsaeng.domain.job.recommendation.converter.RecommendationConverter;
import com.umc.pyeongsaeng.domain.job.recommendation.dto.response.RecommendationResponseDTO;
import com.umc.pyeongsaeng.domain.job.recommendation.util.DistanceUtil;
import com.umc.pyeongsaeng.domain.job.repository.JobPostRepository;
import com.umc.pyeongsaeng.domain.job.repository.JobPostImageRepository;
import com.umc.pyeongsaeng.domain.job.search.document.JobPostDocument;
import com.umc.pyeongsaeng.domain.job.search.service.JobPostSearchQueryService;
import com.umc.pyeongsaeng.domain.senior.entity.SeniorProfile;
import com.umc.pyeongsaeng.domain.senior.repository.SeniorProfileRepository;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;
import com.umc.pyeongsaeng.global.s3.dto.S3DTO;
import com.umc.pyeongsaeng.global.s3.service.S3Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

	private final JobPostRepository jobPostRepository;
	private final JobPostImageRepository jobPostImageRepository;
	private final SeniorProfileRepository seniorProfileRepository;
	private final JobPostSearchQueryService jobPostSearchQueryService;
	private final S3Service s3Service;

	@Override
	public List<RecommendationResponseDTO> recommendJobsByDistance(Long userId) {
		SeniorProfile profile = seniorProfileRepository.findBySeniorId(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		double userLat = profile.getLatitude();
		double userLng = profile.getLongitude();
		String jobKeyword = profile.getJob().getKorName();

		return jobPostRepository.findAll().stream()
			.filter(job -> job.getLatitude() != null && job.getLongitude() != null)
			.map(job -> {
				log.info("추천 대상 jobPostId: {}", job.getId());

				double distance = DistanceUtil.calculateDistance(userLat, userLng, job.getLatitude(), job.getLongitude());

				String imageUrl = null;
				try {
					imageUrl = jobPostImageRepository.findFirstByJobPostIdOrderByIdAsc(job.getId())
						.map(img -> {
							log.info("대표 이미지 keyName = {}", img.getKeyName());
							return s3Service.getPresignedToDownload(
								S3DTO.PresignedUrlToDownloadRequest.builder()
									.keyName(img.getKeyName())
									.build()
							).getUrl();
						})
						.orElse(null);
				} catch (Exception e) {
					log.error("이미지 presigned URL 생성 실패: {}", e.getMessage());
				}

				return RecommendationConverter.toRecommendationResponseDTO(job, distance, imageUrl);
			})
			.sorted(Comparator.comparingDouble(RecommendationResponseDTO::distanceKm))
			.limit(10)
			.collect(Collectors.toList());
	}

	@Override
	public List<RecommendationResponseDTO> recommendJobsByJobTypeAndDistance(Long userId) {
		SeniorProfile profile = seniorProfileRepository.findBySeniorId(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
		double userLat = profile.getLatitude();
		double userLng = profile.getLongitude();
		String jobKeyword = profile.getJob().getKorName();

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
				double distance = DistanceUtil.calculateDistance(userLat, userLng, job.getLatitude(), job.getLongitude());
				String imageUrl = getPresignedImage(job.getId());
				return RecommendationConverter.toRecommendationResponseDTO(job, distance, imageUrl);
			})
			.sorted(Comparator.comparingDouble(RecommendationResponseDTO::distanceKm))
			.limit(10)
			.toList();
	}

	private String getPresignedImage(Long jobPostId) {
		try {
			return jobPostImageRepository.findFirstByJobPostIdOrderByIdAsc(jobPostId)
				.map(img -> s3Service.getPresignedToDownload(
					S3DTO.PresignedUrlToDownloadRequest.builder()
						.keyName(img.getKeyName())
						.build()
				).getUrl()).orElse(null);
		} catch (Exception e) {
			log.error("이미지 presigned URL 생성 실패: {}", e.getMessage());
			return null;
		}
	}

}
