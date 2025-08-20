package com.umc.pyeongsaeng.domain.job.entity;

import com.umc.pyeongsaeng.domain.application.entity.Application;
import com.umc.pyeongsaeng.domain.bookmark.entity.Bookmark;
import com.umc.pyeongsaeng.domain.company.entity.Company;
import com.umc.pyeongsaeng.domain.job.dto.request.JobPostRequestDTO;
import com.umc.pyeongsaeng.domain.job.enums.JobPostState;
import com.umc.pyeongsaeng.global.client.google.GoogleGeocodingResult;
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

	@OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FormField> formFields = new ArrayList<>();

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

	@Enumerated(EnumType.STRING)
	private JobPostState state;

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

	@OneToMany(mappedBy = "jobPost", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Bookmark> bookmarks = new ArrayList<>();

	public void update(JobPostRequestDTO.UpdateDTO requestDTO, GoogleGeocodingResult convertedAddress) {
		this.state = JobPostState.RECRUITING;
		if (requestDTO.getTitle() != null) {
			this.title = requestDTO.getTitle();
		}
		if (requestDTO.getAddress() != null) {
			this.address = requestDTO.getAddress();
		}
		if (requestDTO.getDetailAddress() != null) {
			this.detailAddress = requestDTO.getDetailAddress();
		}
		if (requestDTO.getRoadAddress() != null) {
			this.roadAddress = requestDTO.getRoadAddress();
		}
		if (requestDTO.getZipcode() != null) {
			this.zipcode = requestDTO.getZipcode();
		}
		if (requestDTO.getHourlyWage() != null) {
			this.hourlyWage = requestDTO.getHourlyWage();
		}
		if (requestDTO.getMonthlySalary() != null) {
			this.monthlySalary = requestDTO.getMonthlySalary();
		}
		if (requestDTO.getYearSalary() != null) {
			this.yearSalary = requestDTO.getYearSalary();
		}
		if (requestDTO.getDescription() != null) {
			this.description = requestDTO.getDescription();
		}
		if (requestDTO.getWorkingTime() != null) {
			this.workingTime = requestDTO.getWorkingTime();
		}
		if (requestDTO.getDeadline() != null) {
			this.deadline = requestDTO.getDeadline();
		}
		if (requestDTO.getRecruitCount() != null) {
			this.recruitCount = requestDTO.getRecruitCount();
		}
		if (requestDTO.getNote() != null) {
			this.note = requestDTO.getNote();
		}
		if (convertedAddress != null) {
			this.latitude = convertedAddress.lat();
			this.longitude = convertedAddress.lon();
		}
	}
}
