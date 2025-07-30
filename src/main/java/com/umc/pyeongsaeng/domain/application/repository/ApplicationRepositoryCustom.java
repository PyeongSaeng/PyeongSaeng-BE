package com.umc.pyeongsaeng.domain.application.repository;

import java.util.Optional;

public interface ApplicationRepositoryCustom  {

	interface ApplicationDetailView {
		String getPost_state();
		String getApplication_status();
		String getQuestionAndAnswer();
	}

	Optional<ApplicationDetailView> findApplicationQnADetailById(Long applicationId);
}
