package com.umc.pyeongsaeng.domain.company;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import com.umc.pyeongsaeng.global.common.BaseEntity;

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
	private String name;

	@Column(nullable = false, length = 20, unique = true)
	private String businessNo;

	@Column(nullable = false, length = 50, unique = true)
	private String username;

	@Column(nullable = false, length = 100)
	private String password;

	@Column(length = 100)
	private String email;

	@Column(nullable = false, length = 20)
	private String phone;

	@OneToMany(mappedBy = "company")
	private List<JobPost> jobPosts = new ArrayList<>();
}
