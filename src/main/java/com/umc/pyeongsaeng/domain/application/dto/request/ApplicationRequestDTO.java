package com.umc.pyeongsaeng.domain.application.dto.request;

import com.umc.pyeongsaeng.domain.application.enums.ApplicationStatus;
import com.umc.pyeongsaeng.global.common.annotation.ValidEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

public class ApplicationRequestDTO {

	@Getter
	public static class ApplicationStatusRequestDTO {

		@Schema(description = "지원서 상태. APPROVED(승인) 또는 REJECT(거절)만 가능합니다.", allowableValues = {"APPROVED", "REJECT"})
		@ValidEnum(enumClass = ApplicationStatus.class, message = "유효하지 않은 상태값입니다. APPROVED, REJECT 중 하나여야 합니다.")
		String applicationStatus;
	}
}
