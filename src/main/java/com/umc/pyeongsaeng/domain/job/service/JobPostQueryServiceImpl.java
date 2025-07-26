package com.umc.pyeongsaeng.domain.job.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.umc.pyeongsaeng.domain.company.entity.Company;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import com.umc.pyeongsaeng.domain.job.repository.JobPostRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JobPostQueryServiceImpl implements  JobPostQueryService{

	private final JobPostRepository jobPostRepository;

	@Override
	public Page<JobPost> getJobPostList(Company company, Integer page) {

		Page<JobPost> jobPostPage = jobPostRepository.findAllByCompany(company, PageRequest.of(page, 10));

		return jobPostPage;
	}
}
