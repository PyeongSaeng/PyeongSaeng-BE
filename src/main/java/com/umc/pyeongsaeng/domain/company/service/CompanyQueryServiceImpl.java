package com.umc.pyeongsaeng.domain.company.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umc.pyeongsaeng.domain.company.dto.CompanyResponse;
import com.umc.pyeongsaeng.domain.company.entity.Company;
import com.umc.pyeongsaeng.domain.company.repository.CompanyRepository;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyQueryServiceImpl implements CompanyQueryService {
	private final CompanyRepository companyRepository;

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
}
