package com.umc.pyeongsaeng.domain.application.service;

import java.util.List;
import java.util.Optional;

import com.umc.pyeongsaeng.domain.application.converter.ApplicationConverter;
import com.umc.pyeongsaeng.domain.application.dto.response.ApplicationResponseDTO;
import com.umc.pyeongsaeng.domain.application.repository.ApplicationRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.umc.pyeongsaeng.domain.application.entity.Application;
import com.umc.pyeongsaeng.domain.application.repository.ApplicationRepository;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import com.umc.pyeongsaeng.domain.job.repository.JobPostRepository;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationQueryServiceImpl implements ApplicationQueryService {

	private final ApplicationRepository applicationRepository;
	private final JobPostRepository jobPostRepository;
	private final ApplicationConverter applicationConverter;

	public Page<Application> findCompanyApplications(Long jobPostId, Integer page) {

		JobPost jobPost = jobPostRepository.findById(jobPostId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_JOB_POST_ID));

		Page<Application> applicationPage = applicationRepository.findAllByJobPost(jobPost, PageRequest.of(page, 10));

		return applicationPage;
	}

	@Override
	public ApplicationResponseDTO.ApplicationQnADetailPreViewDTO getApplicationQnADetail(Long applicationId) {

		ApplicationRepositoryCustom.ApplicationDetailView queryResult = applicationRepository.findApplicationQnADetailById(applicationId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_APPLICATION_ID));

		return applicationConverter.toApplicationQnADetailPreViewDTO(queryResult);
	}
}
