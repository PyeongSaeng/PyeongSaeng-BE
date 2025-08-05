package com.umc.pyeongsaeng.domain.job.converter;

import com.umc.pyeongsaeng.domain.job.dto.request.FormFieldRequestDTO;
import com.umc.pyeongsaeng.domain.job.dto.response.FormFieldResponseDTO;
import com.umc.pyeongsaeng.domain.job.entity.FormField;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;

import java.util.List;
import java.util.stream.Collectors;

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

	public static FormFieldResponseDTO.FormFieldPreViewListDTO toFormFieldPreViewListDTO (List<FormField> formFieldList) {

		List<FormFieldResponseDTO.FormFieldPreViewDTO> formFieldPreViewList = formFieldList.stream()
			.map(FormFieldConverter::toFormFieldPreViewDTO)
			.collect(Collectors.toList());

		return FormFieldResponseDTO.FormFieldPreViewListDTO.builder()
			.formFieldList(formFieldPreViewList)
			.build();
	}
}
