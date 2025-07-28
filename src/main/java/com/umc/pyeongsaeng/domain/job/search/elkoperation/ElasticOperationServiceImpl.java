package com.umc.pyeongsaeng.domain.job.search.elkoperation;

import java.io.IOException;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.umc.pyeongsaeng.domain.job.search.annotation.GenericElkIndex;
import com.umc.pyeongsaeng.domain.job.search.document.BaseElkDocument;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticOperationServiceImpl implements ElasticOperationService {

	private final ElasticsearchClient elasticsearchClient;

	@Override
	public boolean checkIfExistIndex(String indexName) {
		try {
			BooleanResponse check = elasticsearchClient.indices().exists(ExistsRequest.of(e -> e.index(indexName)));
			return Objects.nonNull(check) && check.value();
		} catch (IOException e) {
			log.error("[ES] Index 존재 여부 확인 중 연결 실패", e);
			throw new GeneralException(ErrorStatus.ES_CONNECTION_ERROR);
		}
	}

	@Override
	public String createIndex(String indexName) {
		try {
			CreateIndexResponse response = elasticsearchClient.indices().create(c -> c
				.index(indexName)
				.mappings(m -> m
					.properties("geoPoint", p -> p.geoPoint(g -> g))
				)
			);
			return response.index();
		} catch (IOException e) {
			log.error("[ES] Index 생성 실패 - 통신 오류", e);
			throw new GeneralException(ErrorStatus.ES_CONNECTION_ERROR);
		} catch (ElasticsearchException e) {
			log.error("[ES] Index 생성 실패 - 요청 오류", e);
			throw new GeneralException(ErrorStatus.ES_REQUEST_ERROR);
		}

	}

	@Override
	public <T extends BaseElkDocument> String insertDocumentGeneric(T document) {
		GenericElkIndex genericElkIndex = document.getClass().getAnnotation(GenericElkIndex.class);

		if (Objects.nonNull(genericElkIndex)) {
			String indexName = genericElkIndex.indexName();

			try {
				IndexRequest<T> request = IndexRequest.of(i -> i
					.index(indexName)
					.id(document.getId())
					.document(document)
					.refresh(Refresh.True));

				IndexResponse response = elasticsearchClient.index(request);
				return response.result().toString();

			} catch (IOException e) {
				log.error("[ES] doc 색인 실패 - index: {}, id: {}", indexName, document.getId(), e);
				throw new GeneralException(ErrorStatus.ES_CONNECTION_ERROR);
			} catch (ElasticsearchException e) {
				log.error("[ES] doc 색인 요청 오류 - index: {}, id: {}", indexName, document.getId(), e);
				throw new GeneralException(ErrorStatus.ES_REQUEST_ERROR);
			}
		}

		log.error("[ES] GenericElkIndex 어노테이션 존재하지 않음 - class: {}", document.getClass().getName());
		throw new GeneralException(ErrorStatus.ES_REQUEST_ERROR);
	}
}

