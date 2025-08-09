package com.umc.pyeongsaeng.domain.job.dto.response;

import com.umc.pyeongsaeng.domain.job.enums.FieldType;
import lombok.*;

import java.util.List;

public class FormFieldResponseDTO {


	public interface FormFieldPreview {
		Long getId();
		String getFieldName();
		FieldType getFieldType();
	}

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class FormFieldPreViewDTO implements FormFieldPreview {
		Long id;
		String fieldName;
		FieldType fieldType;
		// 질문별 선택지를 여기에 넣으면 좋을 것 같아요
	}

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class FormFieldPreViewWithAnswerDTO implements FormFieldPreview{
		Long id;
		String fieldName;
		FieldType fieldType;
		String answer;
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FormFieldPreViewListDTO {
		List<FormFieldResponseDTO.FormFieldPreViewDTO> formFieldList;

	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FormFieldPreViewWithAnswerListDTO {
		List<FormFieldResponseDTO.FormFieldPreview> formFieldList;

	}
}
