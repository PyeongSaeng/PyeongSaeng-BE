package com.umc.pyeongsaeng.domain.job.entity;

import java.util.ArrayList;
import java.util.List;

import com.umc.pyeongsaeng.domain.job.enums.FieldType;
import com.umc.pyeongsaeng.global.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FormField extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "job_post_field_id")
	private JobPostField jobPostField;

	@Column(length = 50)
	private String fieldName;

	@Enumerated(EnumType.STRING)
	private FieldType fieldType;

	private Integer fieldOrder;
}
