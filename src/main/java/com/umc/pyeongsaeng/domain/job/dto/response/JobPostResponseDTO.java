package com.umc.pyeongsaeng.domain.job.dto.response;

import com.umc.pyeongsaeng.domain.job.enums.JobPostState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class JobPostResponseDTO {

	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class JobPostPreviewDTO {
		Long id;
		JobPostState state;
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
		List<JobPostImageResponseDTO.JobPostImagePreviewDTO> jobPostImages;
		List<FormFieldResponseDTO.FormFieldPreViewDTO> formFields;
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

	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class JobPostDetailDTO {
		private String title;
		private String address;
		private String detailAddress;
		private String roadAddress;
		private String zipcode;
		private Integer hourlyWage;
		private Integer monthlySalary;
		private Integer yearSalary;
		private String description;
		private String workingTime;
		private LocalDate deadline;
		private Integer recruitCount;
		private String note;
		private List<JobPostImageResponseDTO.JobPostImagePreviewWithUrlDTO> images;
		private String travelTime;
	}



}
