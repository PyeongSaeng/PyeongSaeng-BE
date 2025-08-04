package com.umc.pyeongsaeng.domain.job.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class JobPostRequestDTO {

	@Getter
	@NoArgsConstructor
	public static class ImageRequestDTO {
		private String keyName;
		private String originalFileName;
	}

	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CreateDTO {

		@NotBlank(message = "제목을 입력해주세요.")
		@Size(max = 255, message = "제목은 255자 이하로 입력해주세요.")
		String title;

		@NotBlank(message = "주소를 입력해주세요.")
		@Size(max = 255, message = "주소는 255자 이하로 입력해주세요.")
		String address;

		@NotBlank(message = "상세주소를 입력해주세요.")
		@Size(max = 255, message = "상세주소는 255자 이하로 입력해주세요.")
		String detailAddress;

		@NotBlank(message = "도로명주소를 입력해주세요.")
		@Size(max = 255, message = "도로명주소는 255자 이하로 입력해주세요.")
		String roadAddress;

		@NotBlank(message = "우편번호를 입력해주세요.")
		@Size(max = 10, message = "우편번호는 10자 이하로 입력해주세요.")
		String zipcode;

		Integer hourlyWage;

		Integer monthlySalary;

		Integer yearSalary;

		@NotBlank(message = "상세설명을 입력해주세요.")
		String description;

		@NotBlank(message = "근무시간을 입력해주세요.")
		String workingTime;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		LocalDate deadline;

		@NotNull(message = "모집인원을 입력해주세요.")
		Integer recruitCount;

		String note;

		List<ImageRequestDTO> jobPostImageList;

		List<FormFieldRequestDTO.CreateDTO> formFieldList;
	}

	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UpdateDTO {

		@NotBlank(message = "제목을 입력해주세요.")
		@Size(max = 255, message = "제목은 255자 이하로 입력해주세요.")
		String title;

		@NotBlank(message = "주소를 입력해주세요.")
		@Size(max = 255, message = "주소는 255자 이하로 입력해주세요.")
		String address;

		@NotBlank(message = "상세주소를 입력해주세요.")
		@Size(max = 255, message = "상세주소는 255자 이하로 입력해주세요.")
		String detailAddress;

		@NotBlank(message = "도로명주소를 입력해주세요.")
		@Size(max = 255, message = "도로명주소는 255자 이하로 입력해주세요.")
		String roadAddress;

		@NotBlank(message = "우편번호를 입력해주세요.")
		@Size(max = 10, message = "우편번호는 10자 이하로 입력해주세요.")
		String zipcode;

		Integer hourlyWage;

		Integer monthlySalary;

		Integer yearSalary;

		@NotBlank(message = "상세설명을 입력해주세요.")
		String description;

		@NotBlank(message = "근무시간을 입력해주세요.")
		String workingTime;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		LocalDate deadline;

		@NotNull(message = "모집인원을 입력해주세요.")
		Integer recruitCount;

		String note;

		List<ImageRequestDTO> jobPostImageList;

		List<FormFieldRequestDTO.CreateDTO> formFieldList;
	}
}
