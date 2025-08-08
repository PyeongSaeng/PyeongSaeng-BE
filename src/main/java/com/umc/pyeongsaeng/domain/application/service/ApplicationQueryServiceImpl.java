package com.umc.pyeongsaeng.domain.application.service;


import com.umc.pyeongsaeng.domain.application.converter.ApplicationConverter;
import com.umc.pyeongsaeng.domain.application.dto.response.ApplicationResponseDTO;
import com.umc.pyeongsaeng.domain.application.entity.Application;
import com.umc.pyeongsaeng.domain.application.enums.ApplicationStatus;
import com.umc.pyeongsaeng.domain.application.repository.ApplicationRepository;
import com.umc.pyeongsaeng.domain.application.repository.ApplicationRepositoryCustom;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import com.umc.pyeongsaeng.domain.job.repository.JobPostImageRepository;
import com.umc.pyeongsaeng.domain.job.repository.JobPostRepository;
import com.umc.pyeongsaeng.domain.user.entity.User;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;
import com.umc.pyeongsaeng.global.s3.dto.S3DTO;
import com.umc.pyeongsaeng.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationQueryServiceImpl implements ApplicationQueryService {

	private final ApplicationRepository applicationRepository;
	private final JobPostRepository jobPostRepository;
	private final ApplicationConverter applicationConverter;
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
					String presignedUrl = s3Service.getPresignedToDownload(
						S3DTO.PresignedUrlToDownloadRequest.builder()
							.keyName(img.getKeyName())
							.build()
					).getUrl();
					return ApplicationConverter.toImagePreviewWithUrlDTO(img, presignedUrl);
				})
				.toList();
			return ApplicationConverter.toSubmittedApplicationResponseDTO(application, application.getJobPost(), imagesWithUrl);
		});

		return resultApplication;
	}
}
