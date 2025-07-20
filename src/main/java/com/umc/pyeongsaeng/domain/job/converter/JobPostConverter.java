package com.umc.pyeongsaeng.domain.job.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.umc.pyeongsaeng.domain.job.dto.request.JobPostRequestDTO;
import com.umc.pyeongsaeng.domain.job.dto.response.JobPostImageResponseDTO;
import com.umc.pyeongsaeng.domain.job.dto.response.JobPostResponseDTO;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;

public class JobPostConverter {

	public static JobPost toJobPost(JobPostRequestDTO.CreateDTO requestDTO) {

		return JobPost.builder()
			.title(requestDTO.getTitle())
			.address(requestDTO.getAddress())
			.detailAddress(requestDTO.getDetailAddress())
			.zipcode(requestDTO.getZipcode())
			.roadAddress(requestDTO.getRoadAddress())
			.description(requestDTO.getDescription())
			.hourlyWage(requestDTO.getHourlyWage())
			.monthlySalary(requestDTO.getMonthlySalary())
			.yearSalary(requestDTO.getYearSalary())
			.workingTime(requestDTO.getWorkingTime())
			.deadline(requestDTO.getDeadline())
			.recruitCount(requestDTO.getRecruitCount())
			.images(new ArrayList<>())
			.note(requestDTO.getNote())
			.build();
	}

	public static JobPostResponseDTO.JobPostPreviewDTO toJobPostPreviewDTO(JobPost jobPost) {

		List<JobPostImageResponseDTO.JobPostImagePreviewDTO> jobPostImagePreviewDTOList = jobPost.getImages().stream()
			.map(JobPostImageConverter::toJobPostImagePreViewDTO).collect(Collectors.toList());

		return JobPostResponseDTO.JobPostPreviewDTO.builder()
			.title(jobPost.getTitle())
			.address(jobPost.getAddress())
			.detailAddress(jobPost.getDetailAddress())
			.zipcode(jobPost.getZipcode())
			.roadAddress(jobPost.getRoadAddress())
			.description(jobPost.getDescription())
			.hourlyWage(jobPost.getHourlyWage())
			.monthlySalary(jobPost.getMonthlySalary())
			.yearSalary(jobPost.getYearSalary())
			.workingTime(jobPost.getWorkingTime())
			.deadline(jobPost.getDeadline())
			.recruitCount(jobPost.getRecruitCount())
			.jobPostImageId(jobPostImagePreviewDTOList)
			.note(jobPost.getNote())
			.build();
	}
}
