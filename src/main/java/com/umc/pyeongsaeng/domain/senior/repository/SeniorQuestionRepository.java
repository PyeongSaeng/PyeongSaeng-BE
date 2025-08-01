package com.umc.pyeongsaeng.domain.senior.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umc.pyeongsaeng.domain.senior.entity.SeniorQuestion;

@Repository
public interface SeniorQuestionRepository extends JpaRepository<SeniorQuestion, Long> {

	@EntityGraph(attributePaths = "options")
	List<SeniorQuestion> findAll();
}
