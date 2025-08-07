package com.umc.pyeongsaeng.domain.job.search.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobSearchResponse {
	private String id;
	private String title;
	private String address;
	private String imageUrl;
	private String displayDistance;
	private Integer applicationCount;
}

