package com.umc.pyeongsaeng.domain.sms.controller;

import org.springframework.validation.annotation.*;
import org.springframework.web.bind.annotation.*;

import com.umc.pyeongsaeng.domain.sms.dto.*;
import com.umc.pyeongsaeng.domain.sms.service.*;
import com.umc.pyeongsaeng.global.apiPayload.*;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;
import com.umc.pyeongsaeng.global.apiPayload.code.status.*;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.tags.*;
import lombok.*;

@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
@Tag(name = "SMS 인증", description = "SMS 인증 관련 API")
public class SmsController {

	private final SmsService smsService;

	@PostMapping("/send")
	@SecurityRequirements
	@Operation(summary = "SMS 인증번호 발송", description = "입력된 전화번호로 인증번호를 발송합니다.")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SMS201", description = "인증번호가 발송되었습니다.")
	public ApiResponse<String> sendSmsVerification(
		@Validated @RequestBody SmsRequest.SmsVerificationRequestDto request) {

		smsService.sendVerificationCode(request.getPhone());
		return ApiResponse.onSuccess(SuccessStatus.SMS_SENT.getMessage());
	}

	@PostMapping("/verify")
	@SecurityRequirements
	@Operation(summary = "SMS 인증번호 확인",
		description = "SMS 인증번호를 확인합니다. 올바른 인증번호인 경우 성공 응답을 반환합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SMS202", description = "SMS 인증이 성공적으로 완료되었습니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SMS401", description = "SMS 인증에 실패했습니다. 인증번호를 다시 확인하거나 재발송 해주세요.")
	})
	public ApiResponse<String> verifySmsCode(
		@Validated @RequestBody SmsRequest.SmsVerificationConfirmRequestDto request) {

		smsService.verifyCode(request.getPhone(), request.getVerificationCode());
		return ApiResponse.onSuccess(SuccessStatus.SMS_VERIFIED.getMessage());
	}

	@PostMapping("/send/account")
	@SecurityRequirements
	@Operation(summary = "계정 찾기(아이디 찾기, 비밀번호 찾기)용 SMS 인증번호 발송",
		description = "아이디 찾기 또는 비밀번호 재설정을 위한 인증번호를 발송합니다. 카카오 회원은 사용할 수 없습니다. "
	+"카카오 회원이 사용할 경우, 이용자의 상황을 판단 후 '카카오 회원은 아이디와 비밀번호를 찾을 수 없습니다.'고 메시지가 뜹니다. "
	+"일반 유저인지 카카오 유저인지 판단해서 메시지를 보내야 될 때 사용해주시면 됩니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SMS201", description = "인증번호가 발송되었습니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER413", description = "카카오 회원은 아이디와 비밀번호를 찾을 수 없습니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SMS403", description = "하루 인증 요청 가능 횟수를 초과했습니다.")
	})
	public ApiResponse<SmsResponse.SmsResultDto> sendAccountVerificationSms(
		@Validated @RequestBody SmsRequest.AccountSmsRequestDto request) {

		SmsResponse.SmsResultDto response = smsService.sendAccountVerificationCode(request.getPhone());
		return ApiResponse.of(SuccessStatus.SMS_SENT, response);
	}
}
