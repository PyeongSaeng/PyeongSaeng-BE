package com.umc.pyeongsaeng.domain.senior.enums;

public enum JobType {
	HOUSEWIFE("돌봄"),
	EMPLOYEE("회사원"),
	PUBLIC_OFFICER("공무원"),
	PROFESSIONAL("전문직"),
	ARTIST("예술가"),
	BUSINESS_OWNER("사업가"),
	ETC("기타");

	private final String korName;

	JobType(String korName) {
		this.korName = korName;
	}

	public String getKorName() {
		return korName;
	}
}
