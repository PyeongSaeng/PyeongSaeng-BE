package com.umc.pyeongsaeng.domain.job.converter;

import com.umc.pyeongsaeng.domain.job.dto.request.FormFieldRequestDTO;
import com.umc.pyeongsaeng.domain.job.dto.response.FormFieldResponseDTO;
import com.umc.pyeongsaeng.domain.job.entity.FormField;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;

import java.util.List;
import java.util.Map;
import java.util.Set;
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

//	public static FormFieldResponseDTO.FormFieldPreViewListWithAnswerDTO toFormFieldPreViewListWithAnswerDTO (List<FormFieldResponseDTO.FormFieldPreview> formFieldList) {
//
//		List<FormFieldResponseDTO.FormFieldPreview> formFieldPreViewList = formFieldList.stream()
//			.map(FormFieldConverter::toFormFieldPreViewDTO)
//			.collect(Collectors.toList());
//
//		return FormFieldResponseDTO.FormFieldPreViewListDTO.builder()
//			.formFieldList(formFieldPreViewList)
//			.build();
//	}


	private static final Set<String> ANSWER_REQUIRED_FIELDS = Set.of("성함", "거주지", "연세", "전화번호");

	public static FormFieldResponseDTO.FormFieldPreview toFormFieldPreview(FormField field, Map<String, String> answers) {
		String fieldName = field.getFieldName();

		if (ANSWER_REQUIRED_FIELDS.contains(fieldName)) {
			return FormFieldResponseDTO.FormFieldPreViewWithAnswerDTO.builder()
				.id(field.getId())
				.fieldName(fieldName)
				.fieldType(field.getFieldType())
				.answer(answers.get(fieldName))
				.build();
		} else {
			return FormFieldResponseDTO.FormFieldPreViewDTO.builder()
				.id(field.getId())
				.fieldName(fieldName)
				.fieldType(field.getFieldType())
				.build();
		}
	}
}
