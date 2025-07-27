package com.umc.pyeongsaeng.domain.company.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.umc.pyeongsaeng.domain.company.enums.CompanyStatus;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import com.umc.pyeongsaeng.global.common.BaseEntity;

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
import lombok.Setter;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Company extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	@Setter
	private String companyName;

	@Column(nullable = false, length = 100)
	@Setter
	private String ownerName;

	@Column(nullable = false, length = 20, unique = true)
	private String businessNo;

	@Column(nullable = false, length = 50, unique = true)
	private String username;

	@Column(nullable = false, length = 100)
	@Setter
	private String password;

	@Column(length = 100)
	private String email;

	@Column(nullable = false, length = 20)
	@Setter
	private String phone;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE'")
	@Setter
	private CompanyStatus status = CompanyStatus.ACTIVE;

	@Column
	@Setter
	private LocalDateTime withdrawnAt;

	@OneToMany(mappedBy = "company")
	private List<JobPost> jobPosts = new ArrayList<>();
}
