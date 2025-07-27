package com.umc.pyeongsaeng.domain.company.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.umc.pyeongsaeng.domain.company.dto.CompanyRequest;
import com.umc.pyeongsaeng.domain.company.dto.CompanyResponse;
import com.umc.pyeongsaeng.domain.company.service.CompanyService;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;
import com.umc.pyeongsaeng.global.apiPayload.code.status.SuccessStatus;
import com.umc.pyeongsaeng.global.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Tag(name = "Company", description = "company 회원가입, 로그인 등 관련 API")
public class CompanyController {

	private final CompanyService companyService;

	@PostMapping("/sign-up")
	@SecurityRequirements
	@Operation(summary = "기업 회원가입",
	description = """
    사업자 등록번호 상태를 확인하고 회원가입합니다.

    등록번호가 옳지 않으면 회원가입 불가.

    인증번호의 경우, 번호를 보내기 위해 /api/sms/send SMS 인증 api를 활용하셔야 합니다.

    인증번호가 옳은지 확인하기 위해 /api/sms/verify를 따로 이용하실 필요는 없습니다.

    인증 번호를 옳게 적었다면 회원가입이 진행됩니다.
    """)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON201", description = "회원가입이 성공적으로 완료되었습니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMPANY401", description = "이미 사용중인 아이디입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMPANY402", description = "이미 가입한 사업자등록번호입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMPANY403", description = "유효하지 않거나 활성화되지 않은 사업자등록번호입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON500", description = "서버 내부 오류가 발생했습니다.")
	})
	public ApiResponse<CompanyResponse.CompanySignUpResponseDto> signUp(
		@Valid @RequestBody CompanyRequest.CompanySignUpRequestDto request) {
		CompanyResponse.CompanySignUpResponseDto response = companyService.signUp(request);
		return ApiResponse.of(SuccessStatus.CREATED, response);
	}

	@PostMapping("/login")
	@SecurityRequirements
	@Operation(summary = "기업 로그인", description = "아이디와 비밀번호로 기업 로그인을 진행합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "로그인이 성공적으로 완료되었습니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMPANY404", description = "존재하지 않는 기업 계정입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMPANY405", description = "비밀번호가 일치하지 않습니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMPANY406", description = "탈퇴한 기업 계정입니다.")
	})
	public ResponseEntity<ApiResponse<CompanyResponse.LoginResponseDto>> login(
		@Valid @RequestBody CompanyRequest.LoginRequestDto request) {

		CompanyResponse.LoginResponseDto response = companyService.login(request);

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.SET_COOKIE, response.getRefreshTokenCookie());

		return ResponseEntity.ok()
			.headers(headers)
			.body(ApiResponse.of(SuccessStatus._OK, response));
	}

	@PostMapping("/logout")
	@Operation(summary = "기업 로그아웃", description = "현재 로그인한 기업 계정을 로그아웃합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "로그아웃이 성공적으로 완료되었습니다."),
	})
	public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long companyId = userDetails.getId();
		companyService.logout(companyId);

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.SET_COOKIE, companyService.getLogoutCookie());

		return ResponseEntity.ok()
			.headers(headers)
			.body(ApiResponse.of(SuccessStatus._OK, null));
	}

	@PatchMapping("/profile")
	@Operation(summary = "기업 정보 수정", description = "기업명과 비밀번호를 수정합니다. 비밀번호 변경 시 현재 비밀번호 확인이 필요합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "정보 수정이 성공적으로 완료되었습니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH401", description = "인증되지 않은 사용자입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMPANY404", description = "존재하지 않는 기업 계정입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH403", description = "비밀번호가 유효하지 않습니다.")
	})
	public ApiResponse<CompanyResponse.CompanyInfoDto> updateProfile(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@Valid @RequestBody CompanyRequest.UpdateProfileRequestDto request) {

		Long companyId = userDetails.getId();
		CompanyResponse.CompanyInfoDto response = companyService.updateProfile(companyId, request);
		return ApiResponse.of(SuccessStatus._OK, response);
	}

	@PostMapping("/withdraw")
	@Operation(summary = "기업 탈퇴", description = "기업 계정을 탈퇴합니다. 탈퇴 후 7일 이내에 복구 가능합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "탈퇴가 성공적으로 완료되었습니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH401", description = "인증되지 않은 사용자입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMPANY404", description = "존재하지 않는 기업 계정입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMPANY407", description = "이미 탈퇴한 기업 계정입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMPANY408", description = "탈퇴 의도가 확인되지 않았습니다.")
	})
	public ApiResponse<Void> withdraw(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@Valid @RequestBody CompanyRequest.WithdrawRequestDto request) {

		Long companyId = userDetails.getId();
		companyService.withdrawCompany(companyId, request.isConfirmed());
		return ApiResponse.of(SuccessStatus._OK, null);
	}

	@PostMapping("/withdraw/cancel")
	@Operation(summary = "기업 탈퇴 취소", description = "탈퇴한 기업 계정을 복구합니다. 탈퇴 7일 후 모든 데이터가 삭제됩니다. 문구 입력에 성공하면 탈퇴 의도인 것으로 판단합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "탈퇴 취소가 성공적으로 완료되었습니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMPANY404", description = "존재하지 않는 기업 계정입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMPANY409", description = "탈퇴하지 않은 기업 계정입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER408", description = "탈퇴 후 7일이 경과하여 복구할 수 없습니다.")
	})
	public ResponseEntity<ApiResponse<CompanyResponse.LoginResponseDto>> cancelWithdrawal(
		@Valid @RequestBody CompanyRequest.LoginRequestDto request) {

		companyService.cancelWithdrawal(request.getUsername());

		CompanyResponse.LoginResponseDto response = companyService.login(request);

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.SET_COOKIE, response.getRefreshTokenCookie());

		return ResponseEntity.ok()
			.headers(headers)
			.body(ApiResponse.of(SuccessStatus._OK, response));
	}

	@GetMapping("/profile")
	@Operation(summary = "기업 정보 조회", description = "현재 로그인한 기업의 정보를 조회합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "조회가 성공적으로 완료되었습니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH401", description = "인증되지 않은 사용자입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMPANY404", description = "존재하지 않는 기업 계정입니다.")
	})
	public ApiResponse<CompanyResponse.CompanyDetailDto> getProfile(
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		Long companyId = userDetails.getId();
		CompanyResponse.CompanyDetailDto response = companyService.getCompanyDetail(companyId);
		return ApiResponse.of(SuccessStatus._OK, response);
	}

	@GetMapping("/check-username")
	@SecurityRequirements
	@Operation(summary = "기업 아이디 중복 확인", description = "기업 아이디 중복 여부를 확인합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "사용 가능한 아이디입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다."),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMPANY401", description = "이미 사용중인 아이디입니다.")
	})
	public ApiResponse<String> checkUsername(@RequestParam String username) {
		companyService.checkUsernameAvailability(username);
		return ApiResponse.of(SuccessStatus._OK, "사용 가능한 아이디입니다.");
	}
}
