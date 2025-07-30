package com.umc.pyeongsaeng.domain.senior.entity;

import com.umc.pyeongsaeng.global.common.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class SeniorQuestionAnswer extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="senior_profile_id", nullable = false)
	private SeniorProfile seniorProfile;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="senior_question_id")
	private SeniorQuestion question;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="senior_option_id")
	private SeniorQuestionOption selectedOption;

	public void updateSelectedOption(SeniorQuestionOption selectedOption) {
		this.selectedOption = selectedOption;
	}

}
