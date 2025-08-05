package com.umc.pyeongsaeng.domain.application.service.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.umc.pyeongsaeng.domain.application.repository.ApplicationRepository;
import com.umc.pyeongsaeng.domain.job.search.service.JobPostSearchCommandService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationEventListener  {

	private final ApplicationRepository applicationRepository;
	private final JobPostSearchCommandService jobPostSearchCommandService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleApplicationSubmitted(ApplicationSubmittedEvent event) {
		long updatedCount = applicationRepository.countByJobPostId(event.getJobPostId());
		log.info("[ES] 실제 DB 기준 application 개수 = {}", updatedCount);
		jobPostSearchCommandService.updateApplicationCount(event.getJobPostId(), (int)updatedCount);
	}

}
