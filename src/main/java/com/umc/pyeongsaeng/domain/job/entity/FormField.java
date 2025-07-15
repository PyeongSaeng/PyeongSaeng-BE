package com.umc.pyeongsaeng.domain.job.entity;

import java.util.ArrayList;
import java.util.List;

import com.umc.pyeongsaeng.domain.job.enums.FieldType;
import com.umc.pyeongsaeng.global.common.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

	@OneToMany(mappedBy = "formField", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<JobPostField> jobPostFieldList = new ArrayList<JobPostField>();

	@Column(length = 50)
	private String fieldName;

	@Enumerated(EnumType.STRING)
	private FieldType fieldType;

	private Integer fieldOrder;
}
