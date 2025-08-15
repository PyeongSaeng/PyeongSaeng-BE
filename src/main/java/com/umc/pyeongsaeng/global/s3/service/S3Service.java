package com.umc.pyeongsaeng.global.s3.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.umc.pyeongsaeng.global.s3.dto.S3DTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private final AmazonS3 amazonS3;

	public S3DTO.PresignedUrlToUploadResponse getPresignedUrlToUpload(S3DTO.PresignedUrlToUploadRequest presignedUploadRequest) {

		String keyName = UUID.randomUUID() + "_" + presignedUploadRequest.getFileName();

		Date expiration = new Date();
		long expTime = expiration.getTime();
		expTime += TimeUnit.MINUTES.toMillis(3);
		expiration.setTime(expTime);

		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, keyName)
			.withMethod(HttpMethod.PUT)
			.withExpiration(expiration);

		String key = generatePresignedUrlRequest.getKey();

		return S3DTO.PresignedUrlToUploadResponse.builder()
			.url(amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString())
			.keyName(key)
			.build();
	}

	public S3DTO.PresignedUrlToDownloadResponse getPresignedToDownload(S3DTO.PresignedUrlToDownloadRequest presignedUrlToDownloadRequest) {

		Date expiration = new Date();
		long expTime = expiration.getTime();
		expTime += TimeUnit.MINUTES.toMillis(3);
		expiration.setTime(expTime);

		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket,
			presignedUrlToDownloadRequest.getKeyName())
			.withMethod(HttpMethod.GET)
			.withExpiration(expiration);

		return S3DTO.PresignedUrlToDownloadResponse.builder()
			.url(amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString())
			.build();
	}

	public S3DTO.PresignedUrlToDownloadResponse getPresignedToDownload(String keyName) {

		Date expiration = new Date();
		long expTime = expiration.getTime();
		expTime += TimeUnit.MINUTES.toMillis(3);
		expiration.setTime(expTime);

		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket,
			keyName)
			.withMethod(HttpMethod.GET)
			.withExpiration(expiration);

		return S3DTO.PresignedUrlToDownloadResponse.builder()
			.url(amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString())
			.build();
	}
}
