package com.umc.pyeongsaeng.domain.user.entity;

import com.umc.pyeongsaeng.global.common.entity.BaseEntity;
import com.umc.pyeongsaeng.domain.user.entity.enums.Gender;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SeniorProfile extends BaseEntity {

	@Id
	private Long seniorId;

	@MapsId
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "senior_id")
	private User senior;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "protector_id")
	private User protector;

	@Column(nullable = true, length = 20)
	private String relation;

	@Column(nullable = false)
	private Integer age;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Gender gender;

	@Column(nullable = false, length = 20)
	private String phoneNum;

	@Column(nullable = false, length = 200)
	private String address;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String job;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String career;

}
