package com.umc.pyeongsaeng.domain.job.search.dto;

import lombok.Data;

@Data
public class JobSearchResponse {
	private String id;
	private String title;
	private String companyName;
	private String address;
	private String displayWage;
	private String displayDistance;
}
