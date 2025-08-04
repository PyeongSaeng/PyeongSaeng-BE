package com.umc.pyeongsaeng.domain.job.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FieldType {
	TEXT("TEXT"),
	IMAGE("IMAGE");

	private final String value;

	FieldType(String value) {
		this.value = value;
	}

	@JsonValue // 1. Java Enum -> JSON String 으로 변환할 때 사용
	public String getValue() {
		return value;
	}

	@JsonCreator // 2. JSON String -> Java Enum 으로 변환할 때 사용
	public static FieldType from(String value) {
		if (value == null) {
			return null;
		}
		// 대소문자 구분 없이 비교하여 일치하는 Enum 상수를 찾아 반환
		for (FieldType type : FieldType.values()) {
			if (type.value.equalsIgnoreCase(value)) {
				return type;
			}
		}
		// 만약 "TEXT", "IMAGE" 외의 값이 들어오면 예외를 발생시켜 잘못된 요청임을 알림
		throw new IllegalArgumentException("Unknown FieldType: " + value);
	}
}
