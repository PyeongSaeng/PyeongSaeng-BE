package com.umc.pyeongsaeng.domain.job.service;

import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import com.umc.pyeongsaeng.domain.company.entity.*;
import com.umc.pyeongsaeng.domain.job.converter.*;
import com.umc.pyeongsaeng.domain.job.dto.response.*;
import com.umc.pyeongsaeng.domain.job.entity.*;
import com.umc.pyeongsaeng.domain.job.enums.*;
import com.umc.pyeongsaeng.domain.job.recommendation.service.*;
import com.umc.pyeongsaeng.domain.job.repository.*;
import com.umc.pyeongsaeng.domain.senior.entity.*;
import com.umc.pyeongsaeng.domain.senior.repository.*;
import com.umc.pyeongsaeng.domain.user.entity.*;
import com.umc.pyeongsaeng.domain.user.repository.*;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.*;
import com.umc.pyeongsaeng.global.apiPayload.code.status.*;
import com.umc.pyeongsaeng.global.s3.service.*;

import jakarta.transaction.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JobPostQueryServiceImpl implements JobPostQueryService {

	private final JobPostRepository jobPostRepository;
	private final FormFieldRepository formFieldRepository;
	private final TravelTimeService travelTimeService;
	private final UserRepository userRepository;
	private final SeniorProfileRepository seniorProfileRepository;
	private final S3Service s3Service;

	@Override
	public Page<JobPostResponseDTO.JobPostPreviewByCompanyDTO> getJobPostPreViewPageByCompany(Company company, Integer page, JobPostState jobPostState) {

		Page<JobPost> jobPostPage;
		Page<JobPostResponseDTO.JobPostPreviewByCompanyDTO> jobPostPreviewPageByCompany;

		if (jobPostState.equals(JobPostState.CLOSED)) {
			jobPostPage = jobPostRepository.findClosedJobPostsByCompany(company, PageRequest.of(page, 10));
		} else if (jobPostState.equals(JobPostState.RECRUITING)) {
			jobPostPage = jobPostRepository.findActiveJobPostsByCompany(company, PageRequest.of(page, 10));
		} else {
			throw new GeneralException(ErrorStatus.INVALID_JOB_POST_STATE);
		}

		jobPostPreviewPageByCompany = jobPostPage.map(jobPost -> {
			// 각 jobPost에 속한 이미지들을 DTO로 변환
			List<JobPostImageResponseDTO.JobPostImagePreviewWithUrlDTO> imagesWithUrl = getJobPostImageUrl(jobPost);
			return JobPostConverter.toJobPostPreviewByCompanyDTOWithState(jobPost, jobPostState, imagesWithUrl);
		});
		return jobPostPreviewPageByCompany;
	}

	@Override
	public Page<JobPostResponseDTO.JobPostPreviewByCompanyDTO> getJobPostPreViewPageByCompanyByPopularity(Company company, Integer page) {
		Page<JobPost> jobPostPage = jobPostRepository.findActiveJobPostsByCompanyByPopularity(company, PageRequest.of(page, 10));

		Page<JobPostResponseDTO.JobPostPreviewByCompanyDTO> jobPostPreviewPageByCompany = jobPostPage.map(jobPost -> {
			// 각 jobPost에 속한 이미지들을 DTO로 변환
			List<JobPostImageResponseDTO.JobPostImagePreviewWithUrlDTO> imagesWithUrl = jobPost.getImages().stream()
				.map(img -> {
					String presignedUrl = s3Service.getPresignedToDownload(img.getKeyName()).getUrl();
					return JobPostImageConverter.toJobPostImagePreViewWithUrlDTO(img, presignedUrl);
				})
				.toList();

			return JobPostConverter.toJobPostPreviewByCompanyDTO(jobPost, imagesWithUrl);
		});

		return jobPostPreviewPageByCompany;
	}

	@Override
	public FormFieldResponseDTO.FormFieldPreViewWithAnswerListDTO getFormFieldListDirect(Long jobPostId, User senior) {

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

		return FormFieldConverter.toFormFieldPreViewWithAnswerListDTO(formFieldList, formFieldAnswerMap);

	}
	@Override
	public FormFieldResponseDTO.FormFieldPreViewWithAnswerListDTO getFormFieldListDelegate(Long jobPostId, Long seniorId) {

		JobPost jobPost = jobPostRepository.findById(jobPostId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_JOB_POST_ID));

		User senior = userRepository.findById(seniorId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.SENIOR_NOT_FOUND));

		SeniorProfile seniorProfile = seniorProfileRepository.findBySeniorId(senior.getId())
			.orElseThrow(() -> new GeneralException(ErrorStatus.SENIOR_NOT_FOUND));

		List<FormField> formFieldList = formFieldRepository.findByJobPost(jobPost);

		Map<String, String> formFieldAnswerMap = Map.ofEntries(
			Map.entry("성함", senior.getName()),
			Map.entry("연세", String.valueOf(seniorProfile.getAge())),
			Map.entry("거주지", seniorProfile.getRoadAddress()),
			Map.entry("전화번호", seniorProfile.getPhoneNum())
		);

		return FormFieldConverter.toFormFieldPreViewWithAnswerListDTO(formFieldList, formFieldAnswerMap);

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
				String presignedUrl = s3Service.getPresignedToDownload(img.getKeyName()).getUrl();
				return JobPostImageConverter.toJobPostImagePreViewWithUrlDTO(img, presignedUrl);
			})
			.toList();

		return JobPostConverter.toJobPostDetailDTO(jobPost, travelTime, images);
	}

	public Page<JobPostResponseDTO.JobPostTrendingDTO> getJobPostTrending(Integer pageNumber) {
		Page<JobPost> jobPostPage = jobPostRepository.findJobPostTrending(PageRequest.of(pageNumber, 10));

		Page<JobPostResponseDTO.JobPostTrendingDTO> jobPostPageTrendingWithUrl = jobPostPage.map(jobPost -> {
			List<JobPostImageResponseDTO.JobPostImagePreviewWithUrlDTO> imagesWithUrl = jobPost.getImages().stream()
				.map(img -> {
					String presignedUrl = s3Service.getPresignedToDownload(img.getKeyName()).getUrl();
					return JobPostImageConverter.toJobPostImagePreViewWithUrlDTO(img, presignedUrl);
				})
				.toList();

			return JobPostConverter.toJobPostTrendingDTO(jobPost, imagesWithUrl);
		});

		return jobPostPageTrendingWithUrl;
	}

	private List<JobPostImageResponseDTO.JobPostImagePreviewWithUrlDTO> getJobPostImageUrl(JobPost jobPost) {
		return jobPost.getImages().stream()
			.map(img -> {
				String presignedUrl = s3Service.getPresignedToDownload(img.getKeyName()).getUrl();
				return JobPostImageConverter.toJobPostImagePreViewWithUrlDTO(img, presignedUrl);
			})
			.toList();
	}
}
