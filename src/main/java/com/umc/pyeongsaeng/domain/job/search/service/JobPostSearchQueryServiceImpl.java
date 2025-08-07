package com.umc.pyeongsaeng.domain.job.search.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.umc.pyeongsaeng.domain.job.search.converter.JobPostDocumentConverter;
import com.umc.pyeongsaeng.domain.job.search.document.JobPostDocument;
import com.umc.pyeongsaeng.domain.job.search.dto.request.JobSearchRequest;
import com.umc.pyeongsaeng.domain.job.search.enums.JobSortType;
import com.umc.pyeongsaeng.domain.job.search.dto.response.JobSearchResponse;
import com.umc.pyeongsaeng.domain.job.search.dto.response.JobSearchResult;
import com.umc.pyeongsaeng.domain.senior.entity.SeniorProfile;
import com.umc.pyeongsaeng.domain.senior.repository.SeniorProfileRepository;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;
import com.umc.pyeongsaeng.global.s3.dto.S3DTO;
import com.umc.pyeongsaeng.global.s3.service.S3Service;

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
public class JobPostSearchQueryServiceImpl implements JobPostSearchQueryService {

	private final ElasticsearchClient esClient;
	private final S3Service s3Service;
	private final SeniorProfileRepository seniorProfileRepository;

	public JobSearchResult search(JobSearchRequest request, Long seniorId) {
		SeniorProfile profile = seniorProfileRepository.findBySeniorId(seniorId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.SENIOR_NOT_FOUND));

		try {
			SearchResponse<JobPostDocument> getJobPost = esClient.search(ss -> {
				ss.index("jobposts")
					.query(q -> q.bool(b -> b
						.should(s -> s.matchPhrasePrefix(mpp -> mpp.field("title").query(request.getKeyword())))
						.should(s -> s.matchPhrasePrefix(mpp -> mpp.field("description").query(request.getKeyword())))
						.should(s -> s.matchPhrasePrefix(mpp -> mpp.field("note").query(request.getKeyword())))
						.should(s -> s.matchPhrasePrefix(mpp -> mpp.field("address").query(request.getKeyword())))
						.should(s -> s.matchPhrasePrefix(mpp -> mpp.field("sido").query(request.getKeyword())))
						.should(s -> s.matchPhrasePrefix(mpp -> mpp.field("sigungu").query(request.getKeyword())))
						.should(s -> s.matchPhrasePrefix(mpp -> mpp.field("bname").query(request.getKeyword())))
						.minimumShouldMatch("1")
						.filter(f -> f.range(r -> r.date(d -> d.field("deadline").gte(LocalDate.now().toString()))))
					))
					.sort(sortBuilder(request, profile))
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
					.totalCount(0)
					.hasNext(false)
					.build();
			}

			// 검색 결과 DTO 변환
			List<JobSearchResponse> results = hits.stream()
				.map(hit -> mapToJobSearchResponse(hit, request))
				.toList();

			long totalCount = extractTotalCount(getJobPost);
			List<Object> searchAfter = extractSearchAfter(hits);
			boolean hasNext = judgeHasNext(getJobPost, request.getSize());

			return JobSearchResult.builder()
				.results(results)
				.searchAfter(searchAfter)
				.totalCount(totalCount)
				.hasNext(hasNext)
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

	private List<SortOptions> sortBuilder(JobSearchRequest request, SeniorProfile profile) {
		JobSortType sortType;
		try {
			sortType = request.getSort() == null ? JobSortType.DISTANCE_ASC : JobSortType.valueOf(request.getSort());
		} catch (IllegalArgumentException e) {
			throw new GeneralException(ErrorStatus.INVALID_SORT_TYPE);
		}

		List<SortOptions> sortOptions = new ArrayList<>();

		switch (sortType) {
			case DISTANCE_ASC:
				sortOptions.add(SortOptions.of(s -> s.geoDistance(g -> g
					.field("geoPoint")
					.location(GeoLocation.of(loc -> loc
						.latlon(LatLonGeoLocation.of(geo -> geo
							.lat(profile.getLatitude())
							.lon(profile.getLongitude())
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

	private long extractTotalCount(SearchResponse<JobPostDocument> response) {
		return response.hits().total() != null ? response.hits().total().value() : 0;
	}

	private boolean judgeHasNext(SearchResponse<JobPostDocument> response, int pageSize){
		long totalCount = extractTotalCount(response);
		return (response.hits().hits().size() == pageSize) && (totalCount > pageSize);
	}

	private List<Object> extractSearchAfter(List<Hit<JobPostDocument>> hits) {
		return hits.get(hits.size() - 1).sort().stream()
			.map(fieldValue -> fieldValue._get())
			.collect(Collectors.toList());
	}

	private JobSearchResponse mapToJobSearchResponse(Hit<JobPostDocument> hit, JobSearchRequest request) {
		JobPostDocument doc = hit.source();
		Double distance = null;

		if (request.getSort().equals(JobSortType.DISTANCE_ASC.toString()) && hit.sort() != null && !hit.sort().isEmpty()) {
			distance = hit.sort().get(0).doubleValue();
		}

		String imageUrl = null;
		if (doc.getKeyname() != null) {
			imageUrl = s3Service.getPresignedToDownload(
				S3DTO.PresignedUrlToDownloadRequest.builder()
					.keyName(doc.getKeyname())
					.build()
			).getUrl();
		}

		return JobPostDocumentConverter.toJobSearchResponse(doc, distance, imageUrl);
	}




}

