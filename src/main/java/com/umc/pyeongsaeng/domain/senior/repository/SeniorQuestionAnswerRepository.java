package com.umc.pyeongsaeng.domain.senior.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umc.pyeongsaeng.domain.senior.entity.SeniorQuestionAnswer;

public interface SeniorQuestionAnswerRepository extends JpaRepository<SeniorQuestionAnswer, Long> {
	List<SeniorQuestionAnswer> findBySeniorProfile_SeniorId(Long seniorId);

	Optional<SeniorQuestionAnswer> findBySeniorProfile_SeniorIdAndQuestion_Id(Long seniorId, Long questionId);

	void deleteBySeniorProfile_SeniorId(Long seniorId);

}
