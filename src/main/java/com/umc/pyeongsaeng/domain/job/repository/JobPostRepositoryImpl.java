package com.umc.pyeongsaeng.domain.job.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.umc.pyeongsaeng.domain.application.entity.QApplication.application;
import static com.umc.pyeongsaeng.domain.job.entity.QJobPost.jobPost;

@Repository
@RequiredArgsConstructor
public class JobPostRepositoryImpl implements JobPostRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<JobPost> findJobPostTrending(Pageable pageable) {

		List<JobPost> content = queryFactory
			.select(jobPost)
			.from(jobPost)
			.leftJoin(jobPost.applications, application)
			.groupBy(jobPost.id)
			.orderBy(application.count().desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(jobPost.count())
			.from(jobPost);

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}
}
