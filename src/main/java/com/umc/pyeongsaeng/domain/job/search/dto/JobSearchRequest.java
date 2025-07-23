package com.umc.pyeongsaeng.domain.job.search.dto;

import java.util.List;

import com.umc.pyeongsaeng.domain.job.search.enums.JobSortType;

import lombok.Data;

@Data
public class JobSearchRequest {
	private String keyword;
	private String loc_cd; // 지역 코드
	private JobSortType sort; // 정렬 기준
	private Double lat; // 유저 위도
	private Double lon; // 유저 경도
	private List<Object> searchAfter;
	private int size = 10;
}

