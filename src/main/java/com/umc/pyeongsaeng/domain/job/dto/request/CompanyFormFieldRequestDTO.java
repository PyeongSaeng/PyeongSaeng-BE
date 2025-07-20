package com.umc.pyeongsaeng.domain.job.dto.request;

import com.umc.pyeongsaeng.domain.job.enums.FieldType;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public class CompanyFormFieldRequestDTO {

	@Getter
	public static class CreateDTO {

		@NotBlank(message = "filedName 필수 입력 값입니다.")
		String fieldName;

		@NotBlank(message = "fieldType 필수 입력 값입니다.")
		FieldType fieldType;
	}
}
