package com.umc.pyeongsaeng.domain.senior.entity;

import com.umc.pyeongsaeng.domain.senior.enums.ExperiencePeriod;
import com.umc.pyeongsaeng.domain.senior.enums.Gender;
import com.umc.pyeongsaeng.domain.senior.enums.JobType;
import com.umc.pyeongsaeng.domain.user.entity.User;
import com.umc.pyeongsaeng.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SeniorProfile extends BaseEntity {

	@Id
	private Long seniorId;

	@MapsId
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "senior_id")
	private User senior;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "protector_id")
	private User protector;

	@Column(length = 20)
	private String relation;

	@Column(nullable = false)
	private Integer age;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Gender gender;

	@Column(nullable = false, length = 20)
	private String phoneNum;

	@Column(nullable = false, length = 10)
	private String zipcode;

	@Column(nullable = false, length = 255)
	private String roadAddress;

	@Column(length = 255)
	private String detailAddress;

	@Column(nullable = true)
	private Double latitude;

	@Column(nullable = true)
	private Double longitude;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private JobType job;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private ExperiencePeriod experiencePeriod;

	//위도 경도 업데이트
	public void updateLocation(Double latitude, Double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	// 프로필 정보 업데이트
	public void updateProfileInfo(String roadAddress, String detailAddress, JobType job, ExperiencePeriod experiencePeriod) {
		if (roadAddress != null) {
			this.roadAddress = roadAddress;
		}
		if (detailAddress != null) {
			this.detailAddress = detailAddress;
		}
		if (job != null) {
			this.job = job;
		}
		if (experiencePeriod != null) {
			this.experiencePeriod = experiencePeriod;
		}
	}

	// 보호자 설정
	public void updateProtector(User protector) {
		this.protector = protector;
	}

	// 보호자 연결 해제
	public void removeProtector() {
		this.protector = null;
	}
}

