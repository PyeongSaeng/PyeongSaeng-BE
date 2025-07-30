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
	private String companyName;

	@Column(nullable = false, length = 100)
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
	private CompanyStatus status = CompanyStatus.ACTIVE;

	@Column
	private LocalDateTime withdrawnAt;

	@OneToMany(mappedBy = "company")
	private List<JobPost> jobPosts = new ArrayList<>();

	// 프로필 정보 업데이트 메서드
	public void updateProfile(String companyName, String ownerName, String phone) {
		if (companyName != null) {
			this.companyName = companyName;
		}
		if (ownerName != null) {
			this.ownerName = ownerName;
		}
		if (phone != null) {
			this.phone = phone;
		}
	}

	// 비밀번호 변경 메서드
	public void changePassword(String encodedPassword) {
		this.password = encodedPassword;
	}

	// 탈퇴 처리 메서드
	public void withdraw() {
		this.status = CompanyStatus.WITHDRAWN;
		this.withdrawnAt = LocalDateTime.now();
	}

	// 탈퇴 취소 메서드
	public void cancelWithdrawal() {
		this.status = CompanyStatus.ACTIVE;
		this.withdrawnAt = null;
	}
}
