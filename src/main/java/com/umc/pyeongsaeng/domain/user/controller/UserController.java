package com.umc.pyeongsaeng.domain.user.controller;

import java.util.*;

import org.springframework.http.*;
import org.springframework.security.core.annotation.*;
import org.springframework.validation.annotation.*;
import org.springframework.web.bind.annotation.*;

import com.umc.pyeongsaeng.domain.auth.dto.*;
import com.umc.pyeongsaeng.domain.auth.service.*;
import com.umc.pyeongsaeng.domain.sms.service.*;
import com.umc.pyeongsaeng.domain.token.service.*;
import com.umc.pyeongsaeng.domain.user.dto.*;
import com.umc.pyeongsaeng.domain.user.service.*;
import com.umc.pyeongsaeng.global.apiPayload.*;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;
import com.umc.pyeongsaeng.global.apiPayload.code.status.*;
import com.umc.pyeongsaeng.global.security.*;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.tags.*;
import lombok.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User 수정, 탈퇴 등 관련 API")
public class UserController {

	private final UserCommandServiceImpl userCommandServiceImpl;
	private final UserQueryServiceImpl userQueryServiceImpl;
	private final AuthCommandService authCommandService;
	private final TokenService tokenService;
	private final SmsService smsService;

	@DeleteMapping("/withdraw")
	@Operation(summary = "회원 탈퇴",
		description = "회원 탈퇴를 진행합니다. 탈퇴 후 7일 이내에 복구 가능합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER401", description = "존재하지 않는 회원입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER410", description = "탈퇴 의도가 확인되지 않았습니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER407", description = "이미 탈퇴한 회원입니다.")
	})
	public ResponseEntity<ApiResponse<String>> withdrawUser(
		@Validated @RequestBody UserRequest.WithdrawRequestDto request,
		@AuthenticationPrincipal CustomUserDetails currentUser) {

		Long userId = currentUser.getId();

		userCommandServiceImpl.withdrawUser(userId, request.isConfirmed());

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.SET_COOKIE, tokenService.deleteRefreshTokenCookie().toString());

		return ResponseEntity.ok()
			.headers(headers)
			.body(ApiResponse.of(SuccessStatus.WITHDRAW_SUCCESS, SuccessStatus.WITHDRAW_SUCCESS.getMessage()));
	}

	@PostMapping("/withdraw/cancel")
	@Operation(summary = "회원 탈퇴 취소",
		description = "탈퇴한 회원을 복구합니다. 탈퇴 7일 후 관련 모든 데이터가 삭제됩니다. 문구 입력에 성공하면 탈퇴 의도인 것으로 판단합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER401", description = "존재하지 않는 회원입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER408", description = "탈퇴하지 않은 회원입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER409", description = "탈퇴 후 7일이 경과하여 복구할 수 없습니다.")
	})
	public ResponseEntity<ApiResponse<AuthResponse.LoginResponseDto>> cancelWithdrawal(
		@Validated @RequestBody AuthRequest.LoginRequestDto request) {

		userCommandServiceImpl.cancelWithdrawal(request.getUsername());

		AuthResponse.LoginResponseDto response = authCommandService.login(request);

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.SET_COOKIE, response.getRefreshTokenCookie());

		return ResponseEntity.ok()
			.headers(headers)
			.body(ApiResponse.of(SuccessStatus._OK, response));
	}

	@GetMapping("/protector/me")
	@Operation(summary = "보호자 정보 조회",
		description = "현재 로그인한 보호자의 정보를 조회합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER401", description = "존재하지 않는 회원입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER412", description = "유효하지 않은 Role입니다.")
	})
	public ApiResponse<UserResponse.ProtectorInfoDto> getProtectorInfo(
		@AuthenticationPrincipal CustomUserDetails currentUser) {

		Long userId = currentUser.getId();
		UserResponse.ProtectorInfoDto protectorInfo = userQueryServiceImpl.getProtectorInfo(userId);

		return ApiResponse.of(SuccessStatus._OK, protectorInfo);
	}

	@GetMapping("/senior/me")
	@Operation(summary = "시니어 정보 조회",
		description = "현재 로그인한 시니어의 정보를 조회합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER401", description = "존재하지 않는 회원입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER412", description = "유효하지 않은 Role입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER411", description = "유효하지 않은 시니어 프로필입니다.")
	})
	public ApiResponse<UserResponse.SeniorInfoDto> getSeniorInfo(
		@AuthenticationPrincipal CustomUserDetails currentUser) {

		Long userId = currentUser.getId();
		UserResponse.SeniorInfoDto seniorInfo = userQueryServiceImpl.getSeniorInfo(userId);

		return ApiResponse.of(SuccessStatus._OK, seniorInfo);
	}

	@PatchMapping("/protector/me")
	@Operation(summary = "보호자 정보 수정",
		description = "보호자의 정보를 수정합니다. 비밀번호 변경 시 현재 비밀번호 확인이 필요합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER401", description = "존재하지 않는 회원입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER407", description = "이미 탈퇴한 회원입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER412", description = "유효하지 않은 Role입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH403", description = "비밀번호가 유효하지 않습니다.")
	})
	public ApiResponse<UserResponse.ProtectorInfoDto> updateProtectorInfo(
		@Validated @RequestBody UserRequest.UpdateProtectorDto request,
		@AuthenticationPrincipal CustomUserDetails currentUser) {

		Long userId = currentUser.getId();
		UserResponse.ProtectorInfoDto updatedInfo = userCommandServiceImpl.updateProtectorInfo(userId, request);

		return ApiResponse.of(SuccessStatus._OK, updatedInfo);
	}

	@PatchMapping("/senior/me")
	@Operation(summary = "시니어 정보 수정",
		description = "시니어의 정보를 수정합니다. 비밀번호 변경 시 현재 비밀번호 확인이 필요합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER401", description = "존재하지 않는 회원입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER407", description = "이미 탈퇴한 회원입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER412", description = "유효하지 않은 Role입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH403", description = "비밀번호가 유효하지 않습니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER411", description = "유효하지 않은 시니어 프로필입니다.")
	})
	public ApiResponse<UserResponse.SeniorInfoDto> updateSeniorInfo(
		@Validated @RequestBody UserRequest.UpdateSeniorDto request,
		@AuthenticationPrincipal CustomUserDetails currentUser) {

		Long userId = currentUser.getId();
		UserResponse.SeniorInfoDto updatedInfo = userCommandServiceImpl.updateSeniorInfo(userId, request);

		return ApiResponse.of(SuccessStatus._OK, updatedInfo);
	}

	@GetMapping("/seniors")
	@Operation(summary = "연결된 시니어 정보 조회",
		description = "보호자 역할인 사용자가 연결된 시니어들의 정보를 조회합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER401", description = "존재하지 않는 회원입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER412", description = "유효하지 않은 Role입니다.")
	})
	public ApiResponse<List<UserResponse.ConnectedSeniorDto>> getConnectedSeniors(
		@AuthenticationPrincipal CustomUserDetails currentUser) {

		Long protectorId = currentUser.getId();
		List<UserResponse.ConnectedSeniorDto> seniors = userQueryServiceImpl.getConnectedSeniors(protectorId);

		return ApiResponse.of(SuccessStatus._OK, seniors);
	}

	@PostMapping("/find-username")
	@SecurityRequirements
	@Operation(summary = "아이디 찾기",
		description = "이름, 전화번호, SMS 인증번호로 아이디를 조회합니다."
	+"인증번호의 경우, 번호를 보내기 위해 /api/sms/send/account SMS 인증 api를 활용하셔야 합니다. 인증번호가 옳은지 확인하기 위해 /api/sms/verify를 따로 이용하실 필요는 없습니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER401", description = "존재하지 않는 회원입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SMS401", description = "SMS 인증에 실패했습니다.")
	})
	public ApiResponse<UserResponse.UsernameDto> findUsername(
		@Validated @RequestBody UserRequest.FindUsernameDto request) {

		UserResponse.UsernameDto response = userQueryServiceImpl.findUsername(request);
		return ApiResponse.of(SuccessStatus._OK, response);
	}

	@PostMapping("/reset-password")
	@SecurityRequirements
	@Operation(summary = "비밀번호 찾기 (새 비밀번호 변경)",
		description = "인증번호가 확인된 후 해당 api를 사용해서 비밀번호를 새롭게 재설정하면 됩니다."
	+"인증 단계에서 return 되는 username을 그대로 가져다가 쓰시면 됩니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER401", description = "존재하지 않는 회원입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER407", description = "이미 탈퇴한 회원입니다."),
	})
	public ApiResponse<String> resetPassword(
		@Validated @RequestBody UserRequest.PasswordChangeDto request) {

		userCommandServiceImpl.resetPassword(request);
		return ApiResponse.onSuccess(null);
	}

	@PostMapping("/reset-password/verify")
	@SecurityRequirements
	@Operation(summary = "비밀번호 찾기(새 비밀번호 변경) 전 인증단계 ",
		description = "아이디, 전화번호, 인증번호를 확인합니다. 비밀번호를 바꿀 때 사용자 식별이 필요하니, 이때를 위한 username이 return됩니다."
	+"인증번호의 경우, 번호를 보내기 위해 /api/sms/send/account SMS 인증 api를 활용하셔야 합니다. 인증번호가 옳은지 확인하기 위해 /api/sms/verify를 따로 이용하실 필요는 없습니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER401", description = "존재하지 않는 회원입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH403", description = "인증번호 불일치"),
	})
	public ApiResponse<UserResponse.UsernameDto> verifyResetPasswordCode(
		@Validated @RequestBody UserRequest.PasswordVerificationDto request) {

		UserResponse.UsernameDto response = userCommandServiceImpl.verifyResetPasswordCode(request);
		return ApiResponse.of(SuccessStatus._OK, response);
	}
}
