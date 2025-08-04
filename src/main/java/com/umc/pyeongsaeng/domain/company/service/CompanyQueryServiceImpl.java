package com.umc.pyeongsaeng.domain.company.service;

import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import com.umc.pyeongsaeng.domain.company.dto.*;
import com.umc.pyeongsaeng.domain.company.entity.*;
import com.umc.pyeongsaeng.domain.company.repository.*;
import com.umc.pyeongsaeng.domain.sms.service.*;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.*;
import com.umc.pyeongsaeng.global.apiPayload.code.status.*;

import lombok.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyQueryServiceImpl implements CompanyQueryService {
	private final CompanyRepository companyRepository;
	private final SmsService smsService;

	// 기업 상세 정보 조회
	@Override
	public CompanyResponse.CompanyDetailDto getCompanyDetail(Long companyId) {
		Company company = companyRepository.findById(companyId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.COMPANY_NOT_FOUND));

		return CompanyResponse.CompanyDetailDto.builder()
			.companyId(company.getId())
			.username(company.getUsername())
			.businessNo(company.getBusinessNo())
			.companyName(company.getCompanyName())
			.ownerName(company.getOwnerName())
			.phone(company.getPhone())
			.email(company.getEmail())
			.status(company.getStatus())
			.build();
	}

	// 아이디 중복 확인
	@Override
	public void checkUsernameAvailability(String username) {
		if (companyRepository.existsByUsername(username)) {
			throw new GeneralException(ErrorStatus.DUPLICATE_USERNAME);
		}
	}

	// 아이디 찾기
	@Override
	public CompanyResponse.UsernameDto findUsername(CompanyRequest.FindCompanyUsernameDto request) {
		smsService.verifyCode(request.getPhone(), request.getVerificationCode());

		Company company = companyRepository.findByOwnerNameAndPhone(request.getOwnerName(), request.getPhone())
			.orElseThrow(() -> new GeneralException(ErrorStatus.COMPANY_NOT_FOUND));

		return CompanyResponse.UsernameDto.from(company);
	}
}
