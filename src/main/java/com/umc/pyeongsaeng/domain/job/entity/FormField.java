package com.umc.pyeongsaeng.domain.job.entity;

import java.util.ArrayList;
import java.util.List;

import com.umc.pyeongsaeng.domain.application.entity.ApplicationAnswer;
import com.umc.pyeongsaeng.domain.job.enums.FieldType;
import com.umc.pyeongsaeng.global.common.entity.BaseEntity;

import jakarta.persistence.*;
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
	@JoinColumn(name = "job_post_id")
	private JobPost jobPost;

	@Column(length = 50)
	private String fieldName;

	@Enumerated(EnumType.STRING)
	private FieldType fieldType;

	private Integer fieldOrder;
}
