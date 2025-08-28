package com.umc.pyeongsaeng.global.s3.service;

import java.time.*;
import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.*;

import com.umc.pyeongsaeng.global.s3.dto.*;

import lombok.*;
import software.amazon.awssdk.services.s3.presigner.*;
import software.amazon.awssdk.services.s3.presigner.model.*;

@Service
@RequiredArgsConstructor
public class S3Service {

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private final S3Presigner presigner;

	public S3DTO.PresignedUrlToUploadResponse getPresignedUrlToUpload(S3DTO.PresignedUrlToUploadRequest req) {
		String key = UUID.randomUUID() + "_" + req.getFileName();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.signatureDuration(Duration.ofMinutes(3))
			.putObjectRequest(b -> b.bucket(bucket).key(key))
			.build();

		String url = presigner.presignPutObject(presignRequest)
			.url().toString();

		return S3DTO.PresignedUrlToUploadResponse.builder()
			.keyName(key)
			.url(url)
			.build();
	}

	public S3DTO.PresignedUrlToDownloadResponse getPresignedToDownload(String keyName) {
		GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
			.signatureDuration(Duration.ofMinutes(3))
			.getObjectRequest(b -> b.bucket(bucket).key(keyName))
			.build();

		String url = presigner.presignGetObject(presignRequest)
			.url().toString();

		return S3DTO.PresignedUrlToDownloadResponse.builder()
			.url(url)
			.build();
	}
}
