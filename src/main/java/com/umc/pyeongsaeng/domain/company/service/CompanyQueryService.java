package com.umc.pyeongsaeng.domain.company.service;

import com.umc.pyeongsaeng.domain.company.dto.CompanyResponse;

public interface CompanyQueryService {
	/**
	 * 기업 상세 정보 조회.
	 * @param companyId 기업 ID
	 * @return 기업 상세 정보
	 */
	CompanyResponse.CompanyDetailDto getCompanyDetail(Long companyId);

	/**
	 * 아이디 중복 확인.
	 * @param username 확인할 아이디
	 */
	void checkUsernameAvailability(String username);
}
