package com.umc.pyeongsaeng.domain.job.converter;

import com.umc.pyeongsaeng.domain.job.dto.request.FormFieldRequestDTO;
import com.umc.pyeongsaeng.domain.job.dto.response.FormFieldResponseDTO;
import com.umc.pyeongsaeng.domain.job.entity.FormField;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;

public class FormFieldConverter {

	public static FormField toFormField(FormFieldRequestDTO.CreateDTO formField, JobPost newJobPost) {
		return FormField.builder()
			.fieldName(formField.getFieldName())
			.fieldType(formField.getFieldType())
			.jobPost(newJobPost)
			.build();
	}

	public static FormFieldResponseDTO.FormFieldPreViewDTO toFormFieldPreViewDTO(FormField formField) {

		return FormFieldResponseDTO.FormFieldPreViewDTO.builder()
			.id(formField.getId())
			.fieldName(formField.getFieldName())
			.fieldType(formField.getFieldType())
			.build();
	}
}
