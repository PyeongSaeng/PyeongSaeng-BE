package com.umc.pyeongsaeng.domain.job.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.umc.pyeongsaeng.domain.job.converter.JobPostConverter;
import com.umc.pyeongsaeng.domain.job.converter.JobPostImageConverter;
import com.umc.pyeongsaeng.domain.job.dto.request.JobPostRequestDTO;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import com.umc.pyeongsaeng.domain.job.entity.JobPostImage;
import com.umc.pyeongsaeng.domain.job.repository.JobPostImageRepository;
import com.umc.pyeongsaeng.domain.job.repository.JobPostRepository;

import com.umc.pyeongsaeng.domain.job.search.elkoperation.ElasticOperationServiceImpl;
import com.umc.pyeongsaeng.domain.job.search.document.JobPostDocument;
import com.umc.pyeongsaeng.global.client.google.GoogleGeocodingClient;
import com.umc.pyeongsaeng.global.client.google.GoogleGeocodingResult;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JobPostCommandServiceImpl implements JobPostCommandService {

	private final JobPostRepository jobPostRepository;
	private final JobPostImageRepository jobPostImageRepository;
	private final GoogleGeocodingClient googleGeocodingClient;
	//private final JobPostSearchRepository jobPostSearchRepository;
	private final ElasticOperationServiceImpl elasticOperationServiceImpl;

	@Override
	public JobPost createJobPost(JobPostRequestDTO.CreateDTO requestDTO, Long companyId) {

		GoogleGeocodingResult convertedAddress = googleGeocodingClient.convert(requestDTO.getRoadAddress());
		JobPost requestedJobPost = JobPostConverter.toJobPost(requestDTO, convertedAddress);

		JobPost newJobPost = jobPostRepository.save(requestedJobPost);

		// requestDTO에 잇는 jobPostImage 저장을 위한 분리 stream
		List<JobPostImage> savedImages = requestDTO.getKeyName().stream()
			.map(keyName -> JobPostImageConverter.toJobPostImage(keyName, newJobPost))
			.collect(Collectors.toList());

		jobPostImageRepository.saveAll(savedImages);

		newJobPost.getImages().addAll(savedImages);

		saveToElasticsearch(newJobPost, convertedAddress);
		// 이미지 정보까지 완전히 채워진 JobPost 객체를 반환
		return newJobPost;
	}

	private void saveToElasticsearch(JobPost jobPost, GoogleGeocodingResult convertedAddress) {
		try {
			JobPostDocument jobPostDocument = JobPostConverter.toDocument(jobPost, convertedAddress);
			String result = elasticOperationServiceImpl.insertDocumentGeneric(jobPostDocument);
			log.info("ES 저장 성공 - id={}, result={}", jobPostDocument.getId(), result);
		} catch (Exception e) {
			log.error("ES 저장 실패 - id={}", jobPost.getId(), e);
		}
	}



}
