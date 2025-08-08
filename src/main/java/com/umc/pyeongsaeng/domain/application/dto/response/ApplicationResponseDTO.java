package com.umc.pyeongsaeng.domain.application.dto.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.umc.pyeongsaeng.domain.application.entity.ApplicationAnswer;
import com.umc.pyeongsaeng.domain.application.enums.ApplicationStatus;
import com.umc.pyeongsaeng.domain.job.enums.FieldType;
import com.umc.pyeongsaeng.domain.job.enums.JobPostState;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

	@Builder
	@Getter
	public static class RegistrationResultDTO {
		private Long applicationId;
		private Long jobPostId;
		private String applicationStatus;
		private LocalDateTime createdAt;

		private List<AnswerResponseDTO> answers;
	}

	@Getter
	@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
		property = "fieldType" // 이 필드를 기준으로 하위 타입을 결정
	)
	@JsonSubTypes({
		@JsonSubTypes.Type(value = TextAnswerResponseDTO.class, name = "TEXT"),
		@JsonSubTypes.Type(value = ImageAnswerResponseDTO.class, name = "IMAGE")
	})
	public static abstract class AnswerResponseDTO {
		// 공통 필드
		private final Long formFieldId;
		private final Long answerFieldId;
		private final String formFieldName;
		private final FieldType fieldType; // 직렬화 시 하위 타입을 결정하는 기준

		protected AnswerResponseDTO(ApplicationAnswer answer) {
			this.formFieldId = answer.getFormField().getId();
			this.answerFieldId = answer.getId();
			this.formFieldName = answer.getFormField().getFieldName();
			this.fieldType = answer.getFormField().getFieldType();
		}
	}

	@Getter
	public static class TextAnswerResponseDTO extends AnswerResponseDTO {
		private String answer;

		public TextAnswerResponseDTO(ApplicationAnswer answer) {
			super(answer);
			this.answer = answer.getAnswerText();
		}
	}

	@Getter
	public static class ImageAnswerResponseDTO extends AnswerResponseDTO {
		private List<ImageInfoDTO> answer;

		public ImageAnswerResponseDTO(ApplicationAnswer answer) {
			super(answer);
			this.answer = answer.getApplicationFiles().stream()
				.map(file -> new ImageInfoDTO(file.getKeyName(), file.getOriginalFileName()))
				.collect(Collectors.toList());
		}
	}

	@Getter
	@AllArgsConstructor
	public static class ImageInfoDTO {
		private String keyName;
		private String originalFileName;
	}

	@Builder
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ImagePreviewWithUrlDTO {
		Long imageId;
		private String keyName;
		private String imageUrl;
		private String originalFileName;
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SubmittedApplicationResponseDTO {
		private Long applicationId;
		private String title;
		private LocalDate deadline;
		private List<ImagePreviewWithUrlDTO> images;
	}

	@Builder
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class SubmittedApplicationResponseListDTO {
		List<SubmittedApplicationResponseDTO> applicationList;
		Integer listSize;
		Integer totalPage;
		Long totalElements;
		Boolean isFirst;
		Boolean isLast;
	}

}
