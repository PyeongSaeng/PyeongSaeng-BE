package com.umc.pyeongsaeng.domain.job.search.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.umc.pyeongsaeng.domain.job.search.annotation.GenericElkIndex;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@GenericElkIndex(indexName = "jobposts")
public class JobPostDocument extends BaseElkDocument {

	private String title;
	private String description;
	private String note;

	private String address;
	private String sido;     // 시/도
	private String sigungu;  // 시/군/구
	private String bname;    // 동/읍/면
	//private String loc_cd;   // 지역 코드

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate deadline;
	private Instant createdAt;

	private String geoPoint; // 근무지 위/경도

	private Integer applicationCount; // 지원서 수

	private String keyname;
}

