package com.umc.pyeongsaeng.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import com.umc.pyeongsaeng.global.common.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SocialAccount extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 20)
	private String providerType;

	@Column(nullable = false, length = 50)
	private String providerUserId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
}
