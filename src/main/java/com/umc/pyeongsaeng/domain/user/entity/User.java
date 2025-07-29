package com.umc.pyeongsaeng.domain.user.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.umc.pyeongsaeng.domain.application.entity.Application;
import com.umc.pyeongsaeng.domain.terms.entity.UserTerms;
import com.umc.pyeongsaeng.domain.token.entity.RefreshToken;
import com.umc.pyeongsaeng.domain.user.enums.Role;
import com.umc.pyeongsaeng.domain.user.enums.UserStatus;
import com.umc.pyeongsaeng.global.common.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 50)
	private String username;

	@Column(length = 100)
	private String password;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(nullable = false, length = 20)
	private String phone;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "VARCHAR(20)")
	private Role role;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE'")
	private UserStatus status;

	@OneToMany(mappedBy = "applicant")
	private List<Application> applications = new ArrayList<>();

	@OneToMany(mappedBy = "senior")
	private List<Application> seniorApplications = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<SocialAccount> socialAccounts = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<RefreshToken> refreshTokens = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<UserTerms> userTerms = new ArrayList<>();

	@Column
	private LocalDateTime withdrawnAt;

	// 기본 정보 업데이트
	public void updateBasicInfo(String name, String phone) {
		if (name != null) {
			this.name = name;
		}
		if (phone != null) {
			this.phone = phone;
		}
	}

	// 비밀번호 업데이트
	public void updatePassword(String encodedPassword) {
		this.password = encodedPassword;
	}

	// 상태 변경
	public void withdraw() {
		this.status = UserStatus.WITHDRAWN;
		this.withdrawnAt = LocalDateTime.now();
	}

	public void cancelWithdrawal() {
		this.status = UserStatus.ACTIVE;
		this.withdrawnAt = null;
	}
}
