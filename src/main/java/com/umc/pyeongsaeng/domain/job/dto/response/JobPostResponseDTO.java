package com.umc.pyeongsaeng.domain.job.dto.response;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class JobPostResponseDTO {

	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class JobPostPreviewDTO {
		String title;
		String address;
		String detailAddress;
		String roadAddress;
		String zipcode;
		Integer hourlyWage;
		Integer monthlySalary;
		Integer yearSalary;
		String description;
		String workingTime;
		LocalDate deadline;
		Integer recruitCount;
		String note;
		List<JobPostImageResponseDTO.JobPostImagePreviewDTO> jobPostImageId;
	}

	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class JobPostPreviewListDTO {
		List<JobPostPreviewDTO> jobPostList;
		Integer listSize;
		Integer totalPage;
		Long totalElements;
		Boolean isFirst;
		Boolean isLast;
	}
}
