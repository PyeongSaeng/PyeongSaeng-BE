package com.umc.pyeongsaeng.domain.user.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umc.pyeongsaeng.domain.auth.dto.AuthRequest;
import com.umc.pyeongsaeng.domain.auth.dto.AuthResponse;
import com.umc.pyeongsaeng.domain.auth.service.AuthServiceCommand;
import com.umc.pyeongsaeng.domain.token.service.TokenService;
import com.umc.pyeongsaeng.domain.user.dto.UserRequest;
import com.umc.pyeongsaeng.domain.user.service.UserService;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;
import com.umc.pyeongsaeng.global.apiPayload.code.status.SuccessStatus;
import com.umc.pyeongsaeng.global.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User 수정, 탈퇴 등 관련 API")
public class UserController {

	private final UserService userService;
	private final AuthServiceCommand authServiceCommand;
	private final TokenService tokenService;

	@DeleteMapping("/withdraw")
	@Operation(summary = "회원 탈퇴",
		description = "회원 탈퇴를 진행합니다. 탈퇴 후 7일 이내에 복구 가능합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER409", description = "탈퇴 의도가 확인되지 않았습니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER406", description = "이미 탈퇴한 회원입니다.")
	})
	public ResponseEntity<ApiResponse<String>> withdrawUser(
		@Validated @RequestBody UserRequest.WithdrawRequestDto request,
		@AuthenticationPrincipal CustomUserDetails currentUser) {

		Long userId = currentUser.getId();

		userService.withdrawUser(userId, request.isConfirmed());

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
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER401", description = "존재하지 않는 사용자입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER407", description = "탈퇴하지 않은 회원입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER408", description = "탈퇴 후 7일이 경과하여 복구할 수 없습니다.")
	})
	public ResponseEntity<ApiResponse<AuthResponse.LoginResponseDto>> cancelWithdrawal(
		@Validated @RequestBody AuthRequest.LoginRequestDto request) {

		userService.cancelWithdrawal(request.getUsername());

		AuthResponse.LoginResponseDto response = authServiceCommand.login(request);

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.SET_COOKIE, response.getRefreshTokenCookie());

		return ResponseEntity.ok()
			.headers(headers)
			.body(ApiResponse.of(SuccessStatus._OK, response));
	}
}
