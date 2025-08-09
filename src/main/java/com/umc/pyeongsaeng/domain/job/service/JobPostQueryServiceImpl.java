package com.umc.pyeongsaeng.domain.job.service;

import com.umc.pyeongsaeng.domain.company.entity.Company;
import com.umc.pyeongsaeng.domain.job.converter.FormFieldConverter;
import com.umc.pyeongsaeng.domain.job.converter.JobPostConverter;
import com.umc.pyeongsaeng.domain.job.converter.JobPostImageConverter;
import com.umc.pyeongsaeng.domain.job.dto.response.FormFieldResponseDTO;
import com.umc.pyeongsaeng.domain.job.dto.response.JobPostImageResponseDTO;
import com.umc.pyeongsaeng.domain.job.dto.response.JobPostResponseDTO;
import com.umc.pyeongsaeng.domain.job.entity.FormField;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import com.umc.pyeongsaeng.domain.job.entity.JobPostImage;
import com.umc.pyeongsaeng.domain.job.enums.JobPostState;
import com.umc.pyeongsaeng.domain.job.recommendation.service.TravelTimeService;
import com.umc.pyeongsaeng.domain.job.repository.FormFieldRepository;
import com.umc.pyeongsaeng.domain.job.repository.JobPostRepository;
import com.umc.pyeongsaeng.domain.senior.entity.SeniorProfile;
import com.umc.pyeongsaeng.domain.senior.repository.SeniorProfileRepository;
import com.umc.pyeongsaeng.domain.user.entity.User;
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
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JobPostQueryServiceImpl implements JobPostQueryService {

	private final JobPostRepository jobPostRepository;
	private final FormFieldRepository formFieldRepository;
	private final TravelTimeService travelTimeService;
	private final SeniorProfileRepository seniorProfileRepository;
	private final S3Service s3Service;

	@Override
	public Page<JobPostResponseDTO.JobPostPreviewByCompanyDTO> getJobPostPreViewPageByCompany(Company company, Integer page, JobPostState jobPostState) {

		Page<JobPost> jobPostPage;

		if (jobPostState.equals(JobPostState.CLOSED)) {
			jobPostPage = jobPostRepository.findClosedJobPostsByCompany(company, PageRequest.of(page, 10));
		} else if (jobPostState.equals(JobPostState.RECRUITING)) {
			jobPostPage = jobPostRepository.findActiveJobPostsByCompany(company, PageRequest.of(page, 10));
		} else {
			throw new GeneralException(ErrorStatus.INVALID_JOB_POST_STATE);
		}

		Page<JobPostResponseDTO.JobPostPreviewByCompanyDTO> jobPostPreviewPageByCompany = jobPostPage.map(jobPost -> {
			// 각 jobPost에 속한 이미지들을 DTO로 변환
			List<JobPostImageResponseDTO.JobPostImagePreviewWithUrlDTO> imagesWithUrl = jobPost.getImages().stream()
				.map(img -> {
					String presignedUrl = s3Service.getPresignedToDownload(
						S3DTO.PresignedUrlToDownloadRequest.builder()
							.keyName(img.getKeyName())
							.build()
					).getUrl();
					return JobPostImageConverter.toJobPostImagePreViewWithUrlDTO(img, presignedUrl);
				})
				.toList();

			return JobPostConverter.toJobPostPreviewByCompanyDTO(jobPost, imagesWithUrl);
		});

		return jobPostPreviewPageByCompany;
	}

	@Override
	public Page<JobPostResponseDTO.JobPostPreviewByCompanyDTO> getJobPostPreViewPageByCompanyByPopularity(Company company, Integer page) {

		Page<JobPost> jobPostPage;

		jobPostPage = jobPostRepository.findActiveJobPostsByCompanyByPopularity(company, PageRequest.of(page, 10));

		Page<JobPostResponseDTO.JobPostPreviewByCompanyDTO> jobPostPreviewPageByCompany = jobPostPage.map(jobPost -> {
			// 각 jobPost에 속한 이미지들을 DTO로 변환
			List<JobPostImageResponseDTO.JobPostImagePreviewWithUrlDTO> imagesWithUrl = jobPost.getImages().stream()
				.map(img -> {
					String presignedUrl = s3Service.getPresignedToDownload(
						S3DTO.PresignedUrlToDownloadRequest.builder()
							.keyName(img.getKeyName())
							.build()
					).getUrl();
					return JobPostImageConverter.toJobPostImagePreViewWithUrlDTO(img, presignedUrl);
				})
				.toList();

			return JobPostConverter.toJobPostPreviewByCompanyDTO(jobPost, imagesWithUrl);
		});

		return jobPostPreviewPageByCompany;
	}

	@Override
	public List<FormFieldResponseDTO.FormFieldPreview> getFormFieldListDirect(Long jobPostId, User senior) {

		JobPost jobPost = jobPostRepository.findById(jobPostId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_JOB_POST_ID));

		SeniorProfile seniorProfile = seniorProfileRepository.findBySeniorId(senior.getId())
			.orElseThrow(() -> new GeneralException(ErrorStatus.SENIOR_NOT_FOUND));

		List<FormField> formFieldList = formFieldRepository.findByJobPost(jobPost);

		Map<String, String> formFieldAnswerMap = Map.ofEntries(
			Map.entry("성함", senior.getName()),
			Map.entry("연세", String.valueOf(seniorProfile.getAge())),
			Map.entry("거주지", seniorProfile.getRoadAddress()),
			Map.entry("전화번호", seniorProfile.getPhoneNum())
		);

		return formFieldList.stream()
			.map(field -> FormFieldConverter.toFormFieldPreview(field, formFieldAnswerMap))
			.collect(Collectors.toList());

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
