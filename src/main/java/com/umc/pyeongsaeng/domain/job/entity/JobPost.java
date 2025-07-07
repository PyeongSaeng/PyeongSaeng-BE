package com.umc.pyeongsaeng.domain.job.entity;

import com.umc.pyeongsaeng.domain.company.Company;
import com.umc.pyeongsaeng.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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
}
