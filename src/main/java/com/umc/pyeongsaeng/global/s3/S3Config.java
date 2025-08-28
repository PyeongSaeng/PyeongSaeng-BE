package com.umc.pyeongsaeng.global.s3;

import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;

import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.*;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.presigner.*;

@Configuration
public class S3Config {

	@Value("${cloud.aws.credentials.accessKey}")
	private String accessKey;

	@Value("${cloud.aws.credentials.secretKey}")
	private String secretKey;

	@Value("${cloud.aws.region.static}")
	private String region;

	@Bean
	public S3Client s3Client() {
		AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);

		return S3Client.builder()
			.region(Region.of(region))
			.credentialsProvider(StaticCredentialsProvider.create(awsCreds))
			.build();
	}

	@Bean
	public S3Presigner s3Presigner() {
		AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
		return S3Presigner.builder()
			.region(Region.of(region))
			.credentialsProvider(StaticCredentialsProvider.create(awsCreds))
			.build();
	}
}
