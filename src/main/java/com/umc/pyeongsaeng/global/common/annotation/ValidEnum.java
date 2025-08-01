package com.umc.pyeongsaeng.global.common.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EnumValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER}) // 어노테이션 적용 위치
@Retention(RetentionPolicy.RUNTIME) // 어노테이션 유지 기간
public @interface ValidEnum {
	String message() default "유효하지 않은 값입니다. 허용된 값 목록: {allowedValues}"; // 기본 에러 메시지

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	Class<? extends Enum<?>> enumClass(); // 검증할 Enum 클래스를 받기
}
