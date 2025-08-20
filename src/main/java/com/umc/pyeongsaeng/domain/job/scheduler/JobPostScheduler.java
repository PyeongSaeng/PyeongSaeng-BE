package com.umc.pyeongsaeng.domain.job.scheduler;

import com.umc.pyeongsaeng.domain.job.repository.JobPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobPostScheduler {

	private final JobPostRepository jobPostRepository;

	// 매일 새벽 2시에 실행되도록 설정
	@Scheduled(cron = "0 0 2 * * *")
	public void closeExpiredJobPosts() {
		log.info("마감된 채용 공고 상태 업데이트 스케줄러 시작...");

		int updatedCount = jobPostRepository.updateStatusForExpiredJobPosts();

		if (updatedCount > 0) {
			log.info("총 {}개의 채용 공고 상태를 CLOSED로 변경했습니다.", updatedCount);
		} else {
			log.info("상태를 변경할 마감된 채용 공고가 없습니다.");
		}
	}
}
