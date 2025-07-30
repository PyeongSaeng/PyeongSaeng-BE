package com.umc.pyeongsaeng.domain.senior.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umc.pyeongsaeng.domain.senior.entity.SeniorQuestionOption;

public interface SeniorQuestionOptionRepository extends JpaRepository<SeniorQuestionOption, Long> {
}
