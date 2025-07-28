package com.umc.pyeongsaeng.domain.job.search.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobSearchResponse {
	private String id;
	private String title;
	private String address;
	private Integer hourlyWage;
	private Integer monthlySalary;
	private Integer yearSalary;
	private String displayDistance;
}

