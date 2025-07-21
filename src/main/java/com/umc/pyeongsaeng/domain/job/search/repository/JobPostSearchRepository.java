package com.umc.pyeongsaeng.domain.job.search.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.umc.pyeongsaeng.domain.job.search.document.JobPostDocument;

@Profile("!local & !prod")
public interface JobPostSearchRepository extends ElasticsearchRepository<JobPostDocument, String>, JobPostSearchRepositoryCustom {
}

