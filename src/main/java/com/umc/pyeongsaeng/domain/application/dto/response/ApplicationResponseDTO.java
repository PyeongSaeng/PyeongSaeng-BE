package com.umc.pyeongsaeng.domain.application.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ApplicationResponseDTO {

	@Builder
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ApplicationPreViewDTO {
		Long applicationId;
		String applicantName;
	}

	@Builder
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ApplicationPreViewListDTO {
		List<ApplicationPreViewDTO> applicationList;
		Integer listSize;
		Integer totalPage;
		Long totalElements;
		Boolean isFirst;
		Boolean isLast;
	}
}
