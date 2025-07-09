package com.umc.pyeongsaeng.domain.application.entity;

import com.umc.pyeongsaeng.domain.application.enums.QuestionType;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import com.umc.pyeongsaeng.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ApplicationQuestion extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "job_post_id")
	private JobPost jobPost;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id")
	private Application application;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String questionText;

	@Enumerated(EnumType.STRING)
	private QuestionType questionType;

	@Column(columnDefinition = "TEXT")
	private String options;

}
