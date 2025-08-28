package com.umc.pyeongsaeng.domain.application.service;

import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import com.umc.pyeongsaeng.domain.application.converter.*;
import com.umc.pyeongsaeng.domain.application.dto.response.*;
import com.umc.pyeongsaeng.domain.application.entity.*;
import com.umc.pyeongsaeng.domain.application.enums.*;
import com.umc.pyeongsaeng.domain.application.repository.*;
import com.umc.pyeongsaeng.domain.job.entity.*;
import com.umc.pyeongsaeng.domain.job.recommendation.service.*;
import com.umc.pyeongsaeng.domain.job.repository.*;
import com.umc.pyeongsaeng.domain.senior.entity.*;
import com.umc.pyeongsaeng.domain.senior.repository.*;
import com.umc.pyeongsaeng.domain.user.entity.*;
import com.umc.pyeongsaeng.domain.user.repository.*;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.*;
import com.umc.pyeongsaeng.global.apiPayload.code.status.*;
import com.umc.pyeongsaeng.global.s3.service.*;

import lombok.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationQueryServiceImpl implements ApplicationQueryService {

	private final ApplicationRepository applicationRepository;
	private final UserRepository userRepository;
	private final JobPostRepository jobPostRepository;
	private final ApplicationConverter applicationConverter;
	private final TravelTimeService travelTimeService;
	private final SeniorProfileRepository seniorProfileRepository;
	private final S3Service s3Service;

	public Page<Application> findCompanyApplications(Long jobPostId, Integer page) {
		JobPost jobPost = jobPostRepository.findById(jobPostId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_JOB_POST_ID));

		Page<Application> applicationPage = applicationRepository.findAllByJobPostAndApplicationStatusNot(jobPost, ApplicationStatus.DRAFT, PageRequest.of(page, 10));

		return applicationPage;
	}

	@Override
	public ApplicationResponseDTO.ApplicationQnADetailPreViewDTO getApplicationQnADetail(Long applicationId) {
		ApplicationRepositoryCustom.ApplicationDetailView queryResult = applicationRepository.findApplicationQnADetailById(applicationId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_APPLICATION_ID));

		return applicationConverter.toApplicationQnADetailPreViewDTO(queryResult);
	}

	@Override
	public Page<ApplicationResponseDTO.SubmittedApplicationResponseDTO> getSubmittedApplication(User senior, Integer page) {
		Page<Application> applicationPage = applicationRepository.findApplicationsWithDetails(senior, PageRequest.of(page, 10));

		Page<ApplicationResponseDTO.SubmittedApplicationResponseDTO> resultApplication = applicationPage.map(application -> {
			List<ApplicationResponseDTO.ImagePreviewWithUrlDTO> imagesWithUrl = application.getJobPost().getImages().stream()
				.map(img -> {
					String presignedUrl = s3Service.getPresignedToDownload(img.getKeyName()).getUrl();
					return ApplicationConverter.toImagePreviewWithUrlDTO(img, presignedUrl);
				})
				.toList();
			return ApplicationConverter.toSubmittedApplicationResponseDTO(application, application.getJobPost(), imagesWithUrl);
		});

		return resultApplication;
	}

	@Override
	public Page<ApplicationResponseDTO.SubmittedApplicationResponseDTO> getSubmittedApplicationByProtector(Long seniorId, Integer page) {
		User senior = userRepository.findById(seniorId).orElseThrow(() -> new GeneralException(ErrorStatus.SENIOR_NOT_FOUND));

		Page<Application> applicationPage = applicationRepository.findApplicationsWithDetails(senior, PageRequest.of(page, 10));

		Page<ApplicationResponseDTO.SubmittedApplicationResponseDTO> resultApplication = applicationPage.map(application -> {
			List<ApplicationResponseDTO.ImagePreviewWithUrlDTO> imagesWithUrl = application.getJobPost().getImages().stream()
				.map(img -> {
					String presignedUrl = s3Service.getPresignedToDownload(img.getKeyName()).getUrl();
					return ApplicationConverter.toImagePreviewWithUrlDTO(img, presignedUrl);
				})
				.toList();
			return ApplicationConverter.toSubmittedApplicationResponseDTO(application, application.getJobPost(), imagesWithUrl);
		});

		return resultApplication;
	}

	public ApplicationResponseDTO.SubmittedApplicationQnADetailResponseDTO getSubmittedApplicationDetails(Long applicationId, Long userId) {
		ApplicationRepositoryCustom.ApplicationDetailView queryResult = applicationRepository.findApplicationQnADetailById(applicationId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_APPLICATION_ID));

		SeniorProfile seniorProfile = seniorProfileRepository.findBySeniorId(userId).orElseThrow(() -> new GeneralException(ErrorStatus.SENIOR_PROFILE_NOT_FOUND));
		JobPost jobPost = jobPostRepository.findByApplicationsId(applicationId).orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_APPLICATION_ID));

		// 이동시간 계산
		String travelTime = travelTimeService.getTravelTime(seniorProfile.getLatitude(), seniorProfile.getLongitude(), jobPost.getLatitude(), jobPost.getLongitude());

		// Presigned URL 포함 이미지 리스트 변환
		List<ApplicationResponseDTO.ImagePreviewWithUrlDTO> images = jobPost.getImages().stream()
			.map((JobPostImage img) -> {
				String presignedUrl = s3Service.getPresignedToDownload(img.getKeyName()).getUrl();
				return ApplicationConverter.toImagePreviewWithUrlDTO(img, presignedUrl);
			})
			.toList();

		return applicationConverter.toSubmittedApplicationQnADetailResponseDTO(jobPost, queryResult, travelTime, images);
	}

	@Override
	public List<ApplicationResponseDTO.ApplicationJobPostStatusDTO> getApplicationsForSenior(Long seniorId) {
		List<ApplicationStatus> statuses = List.of(ApplicationStatus.DRAFT, ApplicationStatus.NON_STARTED);

		return applicationRepository
			.findAllBySeniorIdAndApplicationStatusInOrderByUpdatedAtDesc(seniorId, statuses)
			.stream()
			.map(applicationConverter::toJobPostStatusDTO)
			.toList();
	}

	@Override
	public List<ApplicationResponseDTO.ProtectorApplicationJobPostDTO> getProtectorApplications(Long protectorId) {
		// 보호자에 연결된 시니어 ID 리스트
		List<Long> seniorIds = seniorProfileRepository.findByProtector_Id(protectorId)
			.stream()
			.map(sp -> sp.getSenior().getId())
			.toList();

		if (seniorIds.isEmpty()) return List.of();

		// 시니어들의 신청서 (최신순)
		return applicationRepository.findBySenior_IdInOrderByCreatedAtDesc(seniorIds)
			.stream()
			.map(a -> ApplicationResponseDTO.ProtectorApplicationJobPostDTO.builder()
				.applicationId(a.getId())
				.jobPostId(a.getJobPost().getId())
				.seniorName(a.getSenior().getName())
				.seniorId(a.getSenior().getId())
				.applicationStatus(a.getApplicationStatus())
				.build())
			.toList();
	}
}
