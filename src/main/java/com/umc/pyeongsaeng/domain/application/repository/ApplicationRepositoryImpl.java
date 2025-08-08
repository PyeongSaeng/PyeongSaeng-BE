package com.umc.pyeongsaeng.domain.application.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc.pyeongsaeng.domain.application.entity.Application;
import com.umc.pyeongsaeng.domain.application.enums.ApplicationStatus;
import com.umc.pyeongsaeng.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.umc.pyeongsaeng.domain.application.entity.QApplication.application;
import static com.umc.pyeongsaeng.domain.job.entity.QJobPost.jobPost;
import static com.umc.pyeongsaeng.domain.job.entity.QJobPostImage.jobPostImage;

@Repository
@RequiredArgsConstructor
public class ApplicationRepositoryImpl implements ApplicationRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final EntityManager em;

	@Getter
	private static class ApplicationDetailViewImpl implements ApplicationDetailView {
		private final String post_state;
		private final String application_status;
		private final String questionAndAnswer;

		public ApplicationDetailViewImpl(Object[] result) {
			this.post_state = (String) result[0];
			this.application_status = (String) result[1];
			this.questionAndAnswer = (String) result[2];
		}

	}

	@Override
	public Optional<ApplicationDetailView> findApplicationQnADetailById(Long applicationId) {
		String sql = "SELECT " +
			"    jp.state AS post_state, " +
			"    app.application_status AS application_status, " +
			"    CAST(JSON_ARRAYAGG( " +
			"        	JSON_OBJECT( " +
			"            'fieldName', ff.field_name, " +
			"            'fieldType', ff.field_type, " +
			"            'answerContent', " +
			"            CASE " +
			"                WHEN ff.field_type = 'IMAGE' THEN " +
			"                    (SELECT " +
			"                         CAST(JSON_ARRAYAGG( " +
			"							JSON_OBJECT( " +
			"                                 'keyName', af.key_name, " +
			"                                 'originalFileName', af.original_file_name " +
			"                             ) " +
			"                             ) AS CHAR) " +
			"                     FROM application_answer_file af " +
			"                     WHERE af.application_answer_id = ans.id) " +
			"                ELSE " +
			"                    ans.answer_text " +
			"            END " +
			"        )" +
			"    ) AS CHAR) AS questionAndAnswer " +
			"FROM " +
			"    application AS app " +
			"JOIN " +
			"    job_post AS jp ON app.job_post_id = jp.id " +
			"LEFT JOIN " +
			"    application_answer AS ans ON app.id = ans.application_id " +
			"LEFT JOIN " +
			"    form_field AS ff ON ans.form_field_id = ff.id " +
			"WHERE " +
			"    app.id = ?1 " +
			"GROUP BY " +
			"    app.id, jp.id";

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(sql)
			.setParameter(1, applicationId)
			.getResultList();

		if (resultList.isEmpty()) {
			return Optional.empty();
		}

		Object[] result = resultList.get(0);

		ApplicationDetailView detailView = new ApplicationDetailViewImpl(result);

		return Optional.of(detailView);
	}

	@Override
	public Page<Application> findApplicationsWithDetails(User senior, Pageable pageable) {

		List<Application> content = queryFactory
			.selectFrom(application).distinct()
			.join(application.jobPost, jobPost).fetchJoin()
			.leftJoin(jobPost.images, jobPostImage).fetchJoin()
			.where(application.senior.eq(senior)
				.and(application.applicationStatus.ne(ApplicationStatus.DRAFT)))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(application.createdAt.desc())
			.fetch();

		Long total = queryFactory
			.select(application.count())
			.from(application)
			.where(application.senior.eq(senior)
				.and(application.applicationStatus.ne(ApplicationStatus.DRAFT)))
			.fetchOne();

		return new PageImpl<>(content, pageable, total);
	}
}
