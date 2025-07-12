package com.umc.pyeongsaeng.domain.sms.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umc.pyeongsaeng.domain.sms.dto.SmsRequest;
import com.umc.pyeongsaeng.domain.sms.service.SmsService;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;
import com.umc.pyeongsaeng.global.apiPayload.code.status.SuccessStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
@Tag(name = "SMS 인증", description = "SMS 인증 관련 API")
public class SmsController {

	private final SmsService smsService;

	@PostMapping("/send")
	@Operation(summary = "SMS 인증번호 발송", description = "입력된 전화번호로 인증번호를 발송합니다.")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SMS201", description = "인증번호가 발송되었습니다.")
	public ApiResponse<String> sendSmsVerification(
		@Validated @RequestBody SmsRequest.SmsVerification request) {

		smsService.sendVerificationCode(request.getPhone());
		return ApiResponse.onSuccess(SuccessStatus.SMS_SENT.getMessage());
	}

	@PostMapping("/verify")
	@Operation(summary = "SMS 인증번호 확인",
		description = "SMS 인증번호를 확인합니다. 올바른 인증번호인 경우 성공 응답을 반환합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SMS202", description = "SMS 인증이 성공적으로 완료되었습니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SMS401", description = "SMS 인증에 실패했습니다. 인증번호를 다시 확인하거나 재발송 해주세요.")
	})
	public ApiResponse<String> verifySmsCode(
		@Validated @RequestBody SmsRequest.SmsVerificationConfirm request) {

		smsService.verifyCode(request.getPhone(), request.getVerificationCode());
		return ApiResponse.onSuccess(SuccessStatus.SMS_VERIFIED.getMessage());
	}
}
