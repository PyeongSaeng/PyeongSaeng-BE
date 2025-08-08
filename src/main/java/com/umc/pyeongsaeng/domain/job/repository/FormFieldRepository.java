package com.umc.pyeongsaeng.domain.job.repository;

import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;

import com.umc.pyeongsaeng.domain.job.entity.FormField;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FormFieldRepository extends JpaRepository<FormField, Long> {
    List<FormField> findByJobPost(JobPost jobPost);

	@Modifying(clearAutomatically = true)
	@Query("DELETE FROM FormField ff WHERE ff.jobPost.id = :jobPostId")
	void deleteByJobPostId(@Param("jobPostId") Long jobPostId);
}
