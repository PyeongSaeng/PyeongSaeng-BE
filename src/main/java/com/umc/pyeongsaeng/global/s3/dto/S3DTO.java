package com.umc.pyeongsaeng.global.s3.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class S3DTO {

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class PresignedUrlToUploadResponse {
		private String keyName;
		private String url;
	}


	@Getter
	public static class PresignedUrlToUploadRequest {

		@NotBlank
		@NotNull
		@Length(min = 1, max = 30)
		private String fileName;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class PresignedUrlToDownloadResponse {
		private String url;
	}

	@Getter
	public static class PresignedUrlToDownloadRequest {

		@NotBlank
		@NotNull
		private String keyName;
	}
}
