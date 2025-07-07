package com.umc.pyeongsaeng.domain.terms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.umc.pyeongsaeng.domain.user.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserTerms {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "terms_id")
	private Terms terms;

	private LocalDateTime agreedAt;
}
