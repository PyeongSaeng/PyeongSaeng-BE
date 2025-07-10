package com.umc.pyeongsaeng.domain.user.entity;

import java.util.ArrayList;
import java.util.List;

import com.umc.pyeongsaeng.domain.application.entity.Application;
import com.umc.pyeongsaeng.domain.terms.entity.UserTerms;
import com.umc.pyeongsaeng.domain.user.enums.Role;
import com.umc.pyeongsaeng.domain.user.enums.UserStatus;
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
}
