package com.umc.pyeongsaeng.domain.application.entity;

import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import com.umc.pyeongsaeng.domain.user.entity.User;
import com.umc.pyeongsaeng.global.common.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Application extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "job_post_id")
	private JobPost jobPost;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "applicant_id")
	private User applicant;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "senior_id")
	private User senior;

	@Enumerated(EnumType.STRING)
	private ApplicationStatus applicationStatus = ApplicationStatus.DRAFT;

	private LocalDateTime submittedAt;

	public enum ApplicationStatus {
		DRAFT,
		SUBMITTED,
		APPROVED,
		REJECTED
	}

}
