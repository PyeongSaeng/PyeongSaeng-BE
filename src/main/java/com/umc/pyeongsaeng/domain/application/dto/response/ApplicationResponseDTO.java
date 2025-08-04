package com.umc.pyeongsaeng.domain.application.dto.response;

import com.umc.pyeongsaeng.domain.application.enums.ApplicationStatus;
import com.umc.pyeongsaeng.domain.job.enums.JobPostState;
import lombok.*;

import java.util.List;

public class ApplicationResponseDTO {

	@Builder
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ApplicationPreViewDTO {
		Long applicationId;
		String applicantName;
		ApplicationStatus applicationStatus;
	}

	@Builder
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ApplicationPreViewListDTO {
		List<ApplicationPreViewDTO> applicationList;
		Integer listSize;
		Integer totalPage;
		Long totalElements;
		Boolean isFirst;
		Boolean isLast;
	}


	@Builder
	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ApplicationFilePreViewDTO {
		private String keyName;
		private String originalFileName;
	}

	@Builder
	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ApplicationQnADTO {
		String fieldName;
		Object answerContent; // answerContent의 타입은 String 또는 List<ImageFileDTO>
		String fieldType;
	}


	@Builder
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ApplicationQnADetailPreViewDTO {
		List<ApplicationQnADTO> questionAndAnswerList;
		JobPostState postState;
		String applicationState;
	}

	@Builder
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ApplicationStateResponseDTO {
		Long applicationId;
		ApplicationStatus applicationStatus;
	}

}
