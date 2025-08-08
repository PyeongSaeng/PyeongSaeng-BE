package com.umc.pyeongsaeng.domain.application.repository;

import com.umc.pyeongsaeng.domain.application.entity.Application;
import com.umc.pyeongsaeng.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ApplicationRepositoryCustom  {

	interface ApplicationDetailView {
		String getPost_state();
		String getApplication_status();
		String getQuestionAndAnswer();
	}

	Optional<ApplicationDetailView> findApplicationQnADetailById(Long applicationId);

	Page<Application> findApplicationsWithDetails(User senior, Pageable pageable);
}
