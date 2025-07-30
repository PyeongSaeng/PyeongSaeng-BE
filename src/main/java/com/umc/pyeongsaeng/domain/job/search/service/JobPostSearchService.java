package com.umc.pyeongsaeng.domain.job.search.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.umc.pyeongsaeng.domain.job.search.converter.JobPostDocumentConverter;
import com.umc.pyeongsaeng.domain.job.search.document.JobPostDocument;
import com.umc.pyeongsaeng.domain.job.search.dto.request.JobSearchRequest;
import com.umc.pyeongsaeng.domain.job.search.enums.JobSortType;
import com.umc.pyeongsaeng.domain.job.search.dto.response.JobSearchResponse;
import com.umc.pyeongsaeng.domain.job.search.dto.response.JobSearchResult;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

import co.elastic.clients.elasticsearch._types.DistanceUnit;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.GeoLocation;
import co.elastic.clients.elasticsearch._types.LatLonGeoLocation;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobPostSearchService {

	private final ElasticsearchClient esClient;

	public JobSearchResult search(JobSearchRequest request) {
		try {
			SearchResponse<JobPostDocument> getJobPost = esClient.search(ss -> {
				ss.index("jobposts")

					.query(q -> q.queryString(qs -> qs
						.query("*" + request.getKeyword() + "*")
						.fields("title", "description", "note", "sido", "sigungu", "bname")
					))

					.sort(sortBuilder(request))
					.size(request.getSize());

				if (request.getSearchAfter() != null && !request.getSearchAfter().isEmpty()) {
					ss.searchAfter(toFieldValueList(request.getSearchAfter()));
				}

				return ss;
			}, JobPostDocument.class);

			if (!getJobPost.shards().failures().isEmpty()) {
				getJobPost.shards().failures().forEach(failure -> {
					log.error("[ES] Shard 실패 이유: {}", failure.reason());
				});
				throw new GeneralException(ErrorStatus.ES_PARTIAL_SHARD_FAILURE);
			}

			List<Hit<JobPostDocument>> hits = getJobPost.hits().hits();
			if (hits.isEmpty()) {
				return JobSearchResult.builder()
					.results(List.of())
					.searchAfter(List.of())
					.build();
			}

			List<JobSearchResponse> results = hits.stream()
				.map(hit -> {
					JobPostDocument doc = hit.source();
					Double distance = null;

					if (request.getSort() == JobSortType.DISTANCE_ASC) {
						distance = hit.sort().get(0).doubleValue();
					}
					return JobPostDocumentConverter.toJobSearchResponse(doc, distance);
				})
				.toList();

			List<Object> searchAfter = hits.get(hits.size() - 1)
				.sort().stream()
				.map(fieldValue -> fieldValue._get())
				.collect(Collectors.toList());


			return JobSearchResult.builder()
				.results(results)
				.searchAfter(searchAfter)
				.build();
		}catch (IOException e){
			log.error("[ES] ES 연결 실패", e);
			throw new GeneralException(ErrorStatus.ES_CONNECTION_ERROR);
		}catch (ElasticsearchException e){
			log.error("[ES] ES 요청 중 오류", e);
			if (e.error() != null) {
				log.error("[ES] Error type: {}", e.error().type());
				log.error("[ES] Error reason: {}", e.error().reason());

				if (e.error().causedBy() != null) {
					log.error("[ES] Caused by: {}", e.error().causedBy().reason());
				}
			}

			throw new GeneralException(ErrorStatus.ES_REQUEST_ERROR);
		}
	}

	private List<SortOptions> sortBuilder(JobSearchRequest request) {
		JobSortType sortType = Optional.ofNullable(request.getSort())
			.orElse(JobSortType.DISTANCE_ASC);

		List<SortOptions> sortOptions = new ArrayList<>();

		switch (sortType) {
			case DISTANCE_ASC:
				sortOptions.add(SortOptions.of(s -> s.geoDistance(g -> g
					.field("geoPoint")
					.location(GeoLocation.of(loc -> loc
						.latlon(LatLonGeoLocation.of(geo -> geo
							.lat(request.getLat())
							.lon(request.getLon())
						)))
					)
					.unit(DistanceUnit.Kilometers)
					.order(SortOrder.Asc)
				)));
				break;

			case POPULARITY_DESC:
				sortOptions.add(SortOptions.of(s -> s.field(f -> f
					.field("applicationCount")
					.order(SortOrder.Desc)
				)));
				break;

			default:
				log.error("[ES] 지원하지 않는 정렬 타입");
				throw new GeneralException(ErrorStatus.INVALID_SORT_TYPE);
		}

		sortOptions.add(SortOptions.of(s -> s.field(f -> f
			.field("createdAt")
			.order(SortOrder.Desc)
		)));

		return sortOptions;
	}

	private List<FieldValue> toFieldValueList(List<Object> values) {
		return Optional.ofNullable(values)
			.orElse(List.of())
			.stream()
			.map(value -> FieldValue.of(value))
			.toList();
	}

}

