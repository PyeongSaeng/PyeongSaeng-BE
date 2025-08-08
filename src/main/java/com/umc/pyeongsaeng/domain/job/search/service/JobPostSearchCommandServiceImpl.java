package com.umc.pyeongsaeng.domain.job.search.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.umc.pyeongsaeng.domain.job.search.document.JobPostDocument;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobPostSearchCommandServiceImpl implements JobPostSearchCommandService {

	private final ElasticsearchClient esClient;

	public void updateApplicationCount(Long jobPostId, int updatedCount ) {
		try {
			UpdateResponse<JobPostDocument> response = esClient.update(u -> u
					.index("jobposts")
					.id(jobPostId.toString())
					.doc(Map.of("applicationCount", updatedCount)),
				JobPostDocument.class
			);

			log.info("[ES] applicationCount update 완료 - id={}, result={}", jobPostId, response.result());

		} catch (IOException e) {
			log.error("[ES] 연결 실패", e);
			throw new GeneralException(ErrorStatus.ES_CONNECTION_ERROR);

		} catch (ElasticsearchException e) {
			log.error("[ES] ES 요청 중 오류", e);
			if (e.error() != null) {
				log.error("[ES] type={}, reason={}", e.error().type(), e.error().reason());
			}
			throw new GeneralException(ErrorStatus.ES_REQUEST_ERROR);
		}

	}

}
