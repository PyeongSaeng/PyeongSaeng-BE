package com.umc.pyeongsaeng.domain.job.repository;

import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;

import com.umc.pyeongsaeng.domain.job.entity.FormField;

import java.util.List;

public interface FormFieldRepository extends JpaRepository<FormField, Long> {
    List<FormField> findByJobPost(JobPost jobPost);
}
