package com.umc.pyeongsaeng.domain.application.entity;

import com.umc.pyeongsaeng.domain.job.entity.FormField;
import com.umc.pyeongsaeng.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ApplicationAnswer extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id")
	private Application application;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "form_field_id")
	private FormField formField;

	@Builder.Default
	@OneToMany(mappedBy = "applicationAnswer", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ApplicationAnswerFile> applicationFiles = new ArrayList<>();

	@Column(columnDefinition = "TEXT", nullable = false)
	private String answerText;


}
