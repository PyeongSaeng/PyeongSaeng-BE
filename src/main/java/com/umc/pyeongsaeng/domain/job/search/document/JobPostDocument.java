package com.umc.pyeongsaeng.domain.job.search.document;

import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(indexName = "jobposts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostDocument {

	@Id
	private String id;

	@Field(type = FieldType.Text)
	private String title;

	@Field(type = FieldType.Text)
	private String description;

	@Field(type = FieldType.Text)
	private String note;

	//@Field(type = FieldType.Keyword)
	//private String companyName;

	@Field(type = FieldType.Integer)
	private Integer hourlyWage;

	@Field(type = FieldType.Integer)
	private Integer monthlySalary;

	@Field(type = FieldType.Integer)
	private Integer yearSalary;

	@Field(type = FieldType.Integer)
	private Integer recruitCount;

	@Field(type = FieldType.Text)
	private String address;

	@Field(type = FieldType.Keyword)
	private String sido; // 시/도

	@Field(type = FieldType.Keyword)
	private String sigungu; // 시/군/구

	@Field(type = FieldType.Keyword)
	private String bname; // 동/읍/면

	@Field(type = FieldType.Keyword)
	private String loc_cd; // 지역 코드

	@Field(type = FieldType.Date, format = DateFormat.date)
	private LocalDate deadline;

	@Field(type = FieldType.Date, format = DateFormat.date_time)
	private Instant createdAt;

	@GeoPointField
	private GeoPoint geoLocation; // 위도,경도

	@Field(type = FieldType.Integer)
	private Integer applicationCount;  // 지원 수

}

