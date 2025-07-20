package com.umc.pyeongsaeng.domain.job.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class JobPostRequestDTO {

	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CreateDTO {

		@NotNull(message = "title(제목) 필수 입력 값입니다.")
		String title;

		@NotNull(message = "address(주소) 필수 입력 값입니다.")
		String address;

		@NotNull(message = "detailAddress(상세주소) 필수 입력 값입니다.")
		String detailAddress;

		@NotNull(message = "roadDetail(도로명주소) 필수 입력 값입니다")
		String roadAddress;

		@NotNull(message = "zipcode(우편번호) 필수 입력 값입니다")
		String zipcode;

		Integer hourlyWage;

		Integer monthlySalary;

		Integer yearSalary;

		String description;

		@NotNull(message = "workingTile(근무시간) 필수 입력 값입니다.")
		String workingTime;

		LocalDate deadline;

		@NotNull(message = "recruitCount(모집인원) 필수 입력 값입니다.")
		Integer recruitCount;

		String note;

		List<String> keyName;
	}
}
