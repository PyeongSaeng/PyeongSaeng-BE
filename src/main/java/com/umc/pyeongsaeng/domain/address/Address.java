package com.umc.pyeongsaeng.domain.address;

import jakarta.persistence.*;
import lombok.*;
import com.umc.pyeongsaeng.global.common.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Address extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 10)
	private String zipcode;

	@Column(nullable = false, length = 255)
	private String roadAddress;

	@Column(length = 255)
	private String jibunAddress;

	@Column(length = 255)
	private String detailAddress;

	@Column(length = 255)
	private String extraAddress;

	@Column(length = 50)
	private String sido;

	@Column(length = 50)
	private String sigungu;

	@Column(length = 50)
	private String eupmyundong;
}
