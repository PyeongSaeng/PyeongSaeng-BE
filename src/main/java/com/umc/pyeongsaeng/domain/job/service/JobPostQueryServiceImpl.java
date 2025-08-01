package com.umc.pyeongsaeng.domain.job.service;

import com.umc.pyeongsaeng.domain.job.entity.FormField;
import com.umc.pyeongsaeng.domain.job.repository.FormFieldRepository;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.umc.pyeongsaeng.domain.company.entity.Company;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import com.umc.pyeongsaeng.domain.job.repository.JobPostRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JobPostQueryServiceImpl implements  JobPostQueryService{

	private final JobPostRepository jobPostRepository;
	private final FormFieldRepository formFieldRepository;

	@Override
	public Page<JobPost> getJobPostList(Company company, Integer page) {

		Page<JobPost> jobPostPage = jobPostRepository.findAllByCompany(company, PageRequest.of(page, 10));

		return jobPostPage;
	}

	@Override
	public List<FormField> getFormFieldList(Long jobPostId) {
		JobPost jobPost = jobPostRepository.findById(jobPostId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_JOB_POST_ID));

		List<FormField> formFieldList = formFieldRepository.findByJobPost(jobPost);


		return formFieldList;
	}
}
