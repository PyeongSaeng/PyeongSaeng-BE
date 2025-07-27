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
	@Setter
	private String password;

	@Column(nullable = false, length = 50)
	@Setter
	private String name;

	@Column(nullable = false, length = 20)
	@Setter
	private String phone;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "VARCHAR(20)")
	private Role role;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE'")
	@Setter
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
	@Setter
	private LocalDateTime withdrawnAt;
}
