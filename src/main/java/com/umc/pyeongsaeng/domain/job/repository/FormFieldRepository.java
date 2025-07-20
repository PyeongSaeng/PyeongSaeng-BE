package com.umc.pyeongsaeng.domain.job.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umc.pyeongsaeng.domain.job.entity.FormField;

public interface FormFieldRepository extends JpaRepository<FormField, Long> {
}
