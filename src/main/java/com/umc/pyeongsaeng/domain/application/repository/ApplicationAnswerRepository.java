package com.umc.pyeongsaeng.domain.application.repository;

import com.umc.pyeongsaeng.domain.application.entity.ApplicationAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationAnswerRepository extends JpaRepository<ApplicationAnswer, Long> {
}
