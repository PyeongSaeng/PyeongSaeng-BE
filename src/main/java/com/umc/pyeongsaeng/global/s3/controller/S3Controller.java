package com.umc.pyeongsaeng.global.s3.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;
import com.umc.pyeongsaeng.global.s3.dto.S3DTO;
import com.umc.pyeongsaeng.global.s3.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Presigned-URL", description = "PresignedURL 관리를 위한 API ")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
public class S3Controller {

	private final S3Service s3Service;

	@Operation(summary = "업로드를 위해 Presigned URL 생성", description = "업로드를 위한 Presigned URL을 생성합니다")
	@PostMapping("/presigned/upload")
	public ApiResponse<S3DTO.PresignedUrlToUploadResponse> getPresignedUrlToUpload(@Valid @RequestBody S3DTO.PresignedUrlToUploadRequest presignedUrlUploadRequest) {

		S3DTO.PresignedUrlToUploadResponse response = s3Service.getPresignedUrlToUpload(presignedUrlUploadRequest);

		return ApiResponse.onSuccess(response);
	}

	@Operation(summary = "다운로드를 위해 Persigned URL 생성", description = "다운로드를 위하여 Presigned URL을 생성합니다.")
	@GetMapping("/presigned/download")
	public com.umc.pyeongsaeng.global.apiPayload.ApiResponse<S3DTO.PresignedUrlToDownloadResponse> getPresginedUrlToDownload(@RequestBody S3DTO.PresignedUrlToDownloadRequest presignedUrlToDownloadRequest) {

		S3DTO.PresignedUrlToDownloadResponse response = s3Service.getPresignedToDownload(presignedUrlToDownloadRequest);

		return com.umc.pyeongsaeng.global.apiPayload.ApiResponse.onSuccess(response);
	}
}
