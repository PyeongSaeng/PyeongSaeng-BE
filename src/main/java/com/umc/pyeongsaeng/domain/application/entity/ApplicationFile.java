package com.umc.pyeongsaeng.domain.application.entity;

import com.umc.pyeongsaeng.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ApplicationFile extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_answer_id")
	private ApplicationAnswer applicationAnswer;

	@Column
	private String keyName;

	@Column
	private String originalFileName;


}
