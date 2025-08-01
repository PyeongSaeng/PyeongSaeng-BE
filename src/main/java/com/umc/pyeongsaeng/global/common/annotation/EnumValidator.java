package com.umc.pyeongsaeng.global.common.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.stream.Stream;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

	private Class<? extends Enum<?>> enumClass;

	@Override
	public void initialize(ValidEnum constraintAnnotation) {
		this.enumClass = constraintAnnotation.enumClass();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return true; // null 값은 @NotNull 등으로 별도 처리하므로 여기서는 통과
		}

		// Enum에 정의된 모든 상수를 스트림으로 가져와, 입력된 문자열과 이름이 일치하는 것이 있는지 확인
		return Stream.of(enumClass.getEnumConstants())
			.anyMatch(enumValue -> enumValue.name().equals(value));
	}
}
