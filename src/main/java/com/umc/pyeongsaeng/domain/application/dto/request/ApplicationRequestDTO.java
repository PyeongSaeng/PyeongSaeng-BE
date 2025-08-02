package com.umc.pyeongsaeng.domain.application.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.umc.pyeongsaeng.domain.application.enums.ApplicationStatus;
import com.umc.pyeongsaeng.domain.job.enums.FieldType;
import com.umc.pyeongsaeng.global.common.annotation.ValidEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ApplicationRequestDTO {

	@Getter
	public static class ApplicationStatusRequestDTO {

		@Schema(description = "지원서 상태. APPROVED(승인) 또는 REJECT(거절)만 가능합니다.", allowableValues = {"APPROVED", "REJECT"})
		@ValidEnum(enumClass = ApplicationStatus.class, message = "유효하지 않은 상태값입니다. APPROVED, REJECT 중 하나여야 합니다.")
		String applicationStatus;
	}

	@Getter
	@NoArgsConstructor
	public static class RegistrationRequestDTO {

		private Long jobPostId;
		private Long seniorId;

		private List<FieldAndAnswerDTO> fieldAndAnswer;
	}

	@Getter
	@NoArgsConstructor
	@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
		property = "fieldType", // JSON에서 타입 구별에 사용할 필드 이름
		visible = true
	)
	@JsonSubTypes({
		@JsonSubTypes.Type(value = TextFieldDTO.class, name = "TEXT"), // fieldType이 "TEXT"이면 TextFieldDTO로
		@JsonSubTypes.Type(value = ImageFieldDTO.class, name = "IMAGE") // fieldType이 "IMAGE"이면 ImageFieldDTO로
	})
	public static abstract class FieldAndAnswerDTO { // 추상 클래스로 선언
		// 공통 필드
		private Long formFieldId;
		private String formFieldName;

		private FieldType fieldType;
	}

	@Getter
	@NoArgsConstructor
	public static class TextFieldDTO extends FieldAndAnswerDTO {
		private String answer;
	}

	@Getter
	@NoArgsConstructor
	public static class ImageFieldDTO extends FieldAndAnswerDTO {
		private List<ImageInfoDTO> answer;
	}

	@Getter
	@NoArgsConstructor
	public static class ImageInfoDTO {
		private String keyName;
		private String originalFileName;
	}
}
