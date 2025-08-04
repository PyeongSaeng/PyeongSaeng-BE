package com.umc.pyeongsaeng.domain.job.recommendation.service;

import com.umc.pyeongsaeng.domain.job.recommendation.converter.RecommendationConverter;
import com.umc.pyeongsaeng.domain.job.recommendation.dto.response.RecommendationResponseDTO;
import com.umc.pyeongsaeng.domain.job.recommendation.util.DistanceUtil;
import com.umc.pyeongsaeng.domain.job.repository.JobPostRepository;
import com.umc.pyeongsaeng.domain.job.repository.JobPostImageRepository;
import com.umc.pyeongsaeng.domain.senior.entity.SeniorProfile;
import com.umc.pyeongsaeng.domain.senior.repository.SeniorProfileRepository;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;
import com.umc.pyeongsaeng.global.s3.dto.S3DTO;
import com.umc.pyeongsaeng.global.s3.service.S3Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

	private final JobPostRepository jobPostRepository;
	private final JobPostImageRepository jobPostImageRepository;
	private final SeniorProfileRepository seniorProfileRepository;
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
				double distance = DistanceUtil.calculateDistance(userLat, userLng, job.getLatitude(), job.getLongitude());

				// 대표 이미지 가져오기
				String imageUrl = jobPostImageRepository.findFirstByJobPostIdOrderByIdAsc(job.getId())
					.map(img -> s3Service.getPresignedToDownload(
						S3DTO.PresignedUrlToDownloadRequest.builder()
							.keyName(img.getKeyName())
							.build()
					).getUrl())
					.orElse(null);

				return RecommendationConverter.toRecommendationResponseDTO(job, distance, imageUrl);
			})
			.sorted(Comparator.comparingDouble(RecommendationResponseDTO::distanceKm))
			.limit(10)
			.collect(Collectors.toList());
	}
}
