package com.umc.pyeongsaeng.domain.job.recommendation.service;

import com.umc.pyeongsaeng.domain.job.recommendation.converter.RecommendationConverter;
import com.umc.pyeongsaeng.domain.job.recommendation.dto.response.RecommendationResponseDTO;
import com.umc.pyeongsaeng.domain.job.recommendation.util.DistanceUtil;
import com.umc.pyeongsaeng.domain.job.repository.JobPostRepository;
import com.umc.pyeongsaeng.domain.job.repository.JobPostImageRepository;
import com.umc.pyeongsaeng.domain.job.search.dto.request.JobSearchRequest;
import com.umc.pyeongsaeng.domain.job.search.dto.response.JobSearchResult;
import com.umc.pyeongsaeng.domain.job.search.enums.JobSortType;
import com.umc.pyeongsaeng.domain.job.search.service.JobPostSearchService;
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
	private final JobPostSearchService jobPostSearchService;
	private final S3Service s3Service;

	@Override
	public List<RecommendationResponseDTO> recommendJobsByDistance(Long userId) {
		SeniorProfile profile = seniorProfileRepository.findBySeniorId(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		double userLat = profile.getLatitude();
		double userLng = profile.getLongitude();

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
	public List<RecommendationResponseDTO> recommendJobsByCareer(Long userId) {
		SeniorProfile profile = seniorProfileRepository.findBySeniorId(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		// 1. 경력 → 키워드 변환 (enum에서 korName 직접 사용)
		String keyword = profile.getJob().getKorName();

		// 2. 검색 요청 DTO 생성
		JobSearchRequest request = JobSearchRequest.builder()
			.keyword(keyword)
			.lat(profile.getLatitude())
			.lon(profile.getLongitude())
			.sort(JobSortType.DISTANCE_ASC)
			.size(10)
			.build();

		// 3. Elastic 검색
		JobSearchResult result = jobPostSearchService.search(request);

		// 4. 검색 결과 → 추천 DTO로 변환
		return result.getResults().stream()
			.map(RecommendationConverter::fromJobSearchResponse)
			.collect(Collectors.toList());
	}
}
