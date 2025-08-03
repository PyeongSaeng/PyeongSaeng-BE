package com.umc.pyeongsaeng.domain.job.dto.response;

import com.umc.pyeongsaeng.domain.job.enums.FieldType;
import lombok.*;

public class FormFieldResponseDTO {

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class FormFieldPreViewDTO {
		Long id;
		String fieldName;
		FieldType fieldType;
	}
}
