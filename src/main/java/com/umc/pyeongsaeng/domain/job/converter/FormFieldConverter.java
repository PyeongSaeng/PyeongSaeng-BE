package com.umc.pyeongsaeng.domain.job.converter;

import com.umc.pyeongsaeng.domain.job.dto.response.JobPostFormFieldResponseDTO;
import com.umc.pyeongsaeng.domain.job.entity.FormField;

import java.util.List;
import java.util.stream.Collectors;

public class FormFieldConverter {

	public static JobPostFormFieldResponseDTO.FormFieldPreViewDTO toFormFieldPreViewDTO(FormField formField) {

		return JobPostFormFieldResponseDTO.FormFieldPreViewDTO.builder()
			.formField(formField.getFieldName())
			.fieldType(formField.getFieldType())
			.build();
	}

	public static JobPostFormFieldResponseDTO.FormFieldPreViewListDTO toFormFieldPreViewListDTO (List<FormField> formFieldList) {

		List<JobPostFormFieldResponseDTO.FormFieldPreViewDTO> formFieldPreViewList = formFieldList.stream()
			.map(FormFieldConverter::toFormFieldPreViewDTO)
			.collect(Collectors.toList());

		return JobPostFormFieldResponseDTO.FormFieldPreViewListDTO.builder()
			.formFieldList(formFieldPreViewList)
			.build();
	}
}
