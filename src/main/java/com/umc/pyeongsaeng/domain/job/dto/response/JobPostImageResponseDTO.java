package com.umc.pyeongsaeng.domain.job.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class JobPostImageResponseDTO {

	@Builder
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class JobPostImagePreviewDTO {

		Long jobPostId;

		String keyName;
	}

	@Builder
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class JobPostImagePreviewWithUrlDTO {
		private Long jobPostId;
		private String keyName;
		private String imageUrl;
	}
}
