package com.umc.pyeongsaeng.domain.job.entity;

import com.umc.pyeongsaeng.domain.application.entity.Application;
import com.umc.pyeongsaeng.domain.company.entity.Company;
import com.umc.pyeongsaeng.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
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

	@OneToMany(cascade = CascadeType.ALL,  fetch = FetchType.LAZY, orphanRemoval = true)
	private List<FormField> formField = new ArrayList<>();

	@OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Application> applications = new ArrayList<>();


	@OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<JobPostImage> images = new ArrayList<>();

	private String title;
	private String address;
	// private String distanceFromHome;
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

	//우편번호
	@Column(nullable = false, length = 10)
	private String zipcode;

	// 도로명 주소
	@Column(nullable = false, length = 255)
	private String roadAddress;

	// 상세 주소
	@Column(length = 255)
	private String detailAddress;

	// 위도
	private Double latitude;

	// 경도
	private Double longitude;

}
