package com.umc.pyeongsaeng.domain.application.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.umc.pyeongsaeng.domain.application.converter.ApplicationConverter;
import com.umc.pyeongsaeng.domain.application.dto.response.ApplicationResponseDTO;
import com.umc.pyeongsaeng.domain.application.entity.Application;
import com.umc.pyeongsaeng.domain.application.service.ApplicationQueryService;
import com.umc.pyeongsaeng.domain.application.service.ApplicationQueryServiceImpl;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;
import com.umc.pyeongsaeng.global.resolvation.annotation.PageNumber;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/applications")
@Tag(name = "신청서 API", description = "신청서 관련 API")
@RequiredArgsConstructor
public class ApplicationController {

	private final ApplicationQueryService applicationQueryService;

	@GetMapping("")
	@Operation(summary = "지원서 목록 조회 API", description = "회사 유저가 특정 채용공고의 지원서 목록을 조회하는 API입니다.")
	@Parameters({
		@Parameter(name = "jobPostId", description = "채용공고 ID", required = true, in = ParameterIn.QUERY),
		@Parameter(name = "page", description = "페이지 번호", required = true, in = ParameterIn.QUERY)
	})
	public ApiResponse<ApplicationResponseDTO.ApplicationPreViewListDTO> getApplication(@RequestParam(name = "jobPostId") Long jobPostId, @PageNumber Integer page) {

		Page<Application> applicationPage = applicationQueryService.findCompanyApplications(jobPostId, page);

		return ApiResponse.onSuccess(ApplicationConverter.toApplicationPreViewListDTO(applicationPage));
	}

}
