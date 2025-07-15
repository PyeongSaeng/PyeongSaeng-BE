package com.umc.pyeongsaeng.domain.job.entity;

import com.umc.pyeongsaeng.domain.application.entity.Application;
import com.umc.pyeongsaeng.domain.application.entity.ApplicationQuestion;
import com.umc.pyeongsaeng.domain.company.entity.Company;
import com.umc.pyeongsaeng.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class JobPost extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id")
	private Company company;

	@OneToOne(mappedBy = "jobPost", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private JobPostField jobPostField;

	@OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Application> applications = new ArrayList<>();

	@OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ApplicationQuestion> questions = new ArrayList<>();

	@OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<JobPostImage> images = new ArrayList<>();

	private String title;
	private String location;
	private String distanceFromHome;
	private Integer hourlyWage;
	private Integer monthlySalary;
	private Integer yearSalary;

	@Column(columnDefinition = "TEXT")
	private String description;

	private String workingTime;
	private LocalDate deadline;
	private Integer recruitCount;

	@Column(columnDefinition = "TEXT")
	private String note;

	private String url;

	@Column(nullable = false, length = 10)
	private String zipcode;

	@Column(nullable = false, length = 255)
	private String roadAddress;

	@Column(length = 255)
	private String detailAddress;
}
