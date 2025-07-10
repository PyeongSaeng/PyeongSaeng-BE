package com.umc.pyeongsaeng.domain.senior.enums;

public enum ExperiencePeriod {
	LESS_THAN_6_MONTHS("6개월 미만"),
	SIX_MONTHS_TO_1_YEAR("6개월 ~ 1년"),
	ONE_TO_THREE_YEARS("1년 ~ 3년"),
	THREE_TO_FIVE_YEARS("3 ~ 5년"),
	FIVE_TO_TEN_YEARS("5 ~ 10년"),
	OVER_TEN_YEARS("10년 이상");

	private final String label;

	ExperiencePeriod(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
