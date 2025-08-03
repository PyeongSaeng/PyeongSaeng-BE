package com.umc.pyeongsaeng.domain.job.service;

import com.umc.pyeongsaeng.domain.company.entity.Company;
import com.umc.pyeongsaeng.domain.job.converter.JobPostConverter;
import com.umc.pyeongsaeng.domain.job.converter.JobPostImageConverter;
import com.umc.pyeongsaeng.domain.job.dto.response.JobPostImageResponseDTO;
import com.umc.pyeongsaeng.domain.job.dto.response.JobPostResponseDTO;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import com.umc.pyeongsaeng.domain.job.entity.JobPostImage;
import com.umc.pyeongsaeng.domain.job.enums.JobPostState;
import com.umc.pyeongsaeng.domain.job.recommendation.service.TravelTimeService;
import com.umc.pyeongsaeng.domain.job.repository.JobPostRepository;
import com.umc.pyeongsaeng.domain.senior.entity.SeniorProfile;
import com.umc.pyeongsaeng.domain.senior.repository.SeniorProfileRepository;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;
import com.umc.pyeongsaeng.global.s3.dto.S3DTO;
import com.umc.pyeongsaeng.global.s3.service.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JobPostQueryServiceImpl implements JobPostQueryService {

	private final JobPostRepository jobPostRepository;
	private final TravelTimeService travelTimeService;
	private final SeniorProfileRepository seniorProfileRepository;
	private final S3Service s3Service;

	@Override
	public Page<JobPost> getJobPostList(Company company, Integer page, JobPostState jobPostState) {

		if(jobPostState.equals(JobPostState.CLOSED)) {
			return jobPostRepository.findClosedJobPostsByCompany(company, PageRequest.of(page, 10));
		} else if (jobPostState.equals(JobPostState.RECRUITING)) {
			return jobPostRepository.findActiveJobPostsByCompany(company, PageRequest.of(page, 10));
		}
		throw new GeneralException(ErrorStatus.INVALID_JOB_POST_STATE);
	}


	@Override
	public JobPostResponseDTO.JobPostDetailDTO getJobPostDetail(Long jobPostId, Long userId) {

		SeniorProfile seniorProfile = seniorProfileRepository.findBySeniorId(userId).orElseThrow(() -> new GeneralException(ErrorStatus.SENIOR_PROFILE_NOT_FOUND));
		JobPost jobPost = jobPostRepository.findById(jobPostId).orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_JOB_POST_ID));

		// 이동시간 계산
		String travelTime = travelTimeService.getTravelTime(seniorProfile.getLatitude(), seniorProfile.getLongitude(), jobPost.getLatitude(), jobPost.getLongitude());

		// Presigned URL 포함 이미지 리스트 변환
		List<JobPostImageResponseDTO.JobPostImagePreviewWithUrlDTO> images = jobPost.getImages().stream()
			.map((JobPostImage img) -> {
				String presignedUrl = s3Service.getPresignedToDownload(
					S3DTO.PresignedUrlToDownloadRequest.builder()
						.keyName(img.getKeyName())
						.build()
				).getUrl();

				return JobPostImageConverter.toJobPostImagePreViewWithUrlDTO(img, presignedUrl);
			})
			.toList();

		return JobPostConverter.toJobPostDetailDTO(jobPost, travelTime, images);
	}




}
