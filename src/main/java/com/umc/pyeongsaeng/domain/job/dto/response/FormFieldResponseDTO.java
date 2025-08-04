package com.umc.pyeongsaeng.domain.job.dto.response;

import com.umc.pyeongsaeng.domain.job.enums.FieldType;
import lombok.*;

import java.util.List;

public class FormFieldResponseDTO {

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class FormFieldPreViewDTO {
		Long id;
		String fieldName;
		FieldType fieldType;
		// 질문별 선택지를 여기에 넣으면 좋을 것 같아요
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FormFieldPreViewListDTO {
		List<FormFieldResponseDTO.FormFieldPreViewDTO> formFieldList;

	}
}
