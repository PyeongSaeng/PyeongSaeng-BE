package com.umc.pyeongsaeng.domain.application.repository;

import java.util.List;
import java.util.Optional;

import com.umc.pyeongsaeng.domain.application.entity.Application;
import com.umc.pyeongsaeng.domain.application.enums.ApplicationStatus;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long>, ApplicationRepositoryCustom {
	void deleteByApplicantId(Long applicantId);
	void deleteBySeniorId(Long seniorId);
	Page<Application> findAllByJobPostAndApplicationStatusNot(JobPost jobPost, ApplicationStatus status, Pageable pageable);

	long countByJobPostId(Long jobPostId);

	// 특정 채용공고에 대한 지원서 조회
	Optional<Application> findByJobPostIdAndSeniorId(Long jobPostId, Long seniorId);

	// 특정 유저에 대한 모든 채용공고 조회
	List<Application> findAllBySeniorIdAndApplicationStatusInOrderByUpdatedAtDesc(
		Long seniorId,
		List<ApplicationStatus> statuses
	);

	List<Application> findBySenior_IdInOrderByCreatedAtDesc(List<Long> seniorIds);
}
