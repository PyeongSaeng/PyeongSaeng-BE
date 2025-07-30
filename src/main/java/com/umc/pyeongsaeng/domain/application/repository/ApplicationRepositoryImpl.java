package com.umc.pyeongsaeng.domain.application.repository;

import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ApplicationRepositoryImpl implements ApplicationRepositoryCustom {

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
			"                     FROM application_file af " +
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

		// EntityManager를 사용하여 네이티브 쿼리를 실행하고, 결과를 Object 배열의 리스트로 받습니다.
		@SuppressWarnings("unchecked") // 네이티브 쿼리 결과는 타입 캐스팅이 필요하므로 경고를 무시합니다.
		List<Object[]> resultList = em.createNativeQuery(sql)
			.setParameter(1, applicationId)
			.getResultList();

		// 결과가 없으면 빈 Optional을 반환
		if (resultList.isEmpty()) {
			return Optional.empty();
		}

		// 첫 번째 결과(Object 배열)를 가져옵니다.
		Object[] result = resultList.get(0);

		ApplicationDetailView detailView = new ApplicationDetailViewImpl(result);

		return Optional.of(detailView);
	}
}
