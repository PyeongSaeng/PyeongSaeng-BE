package com.umc.pyeongsaeng.domain.job.search.converter;

import com.umc.pyeongsaeng.domain.job.search.document.JobPostDocument;
import com.umc.pyeongsaeng.domain.job.search.dto.response.JobSearchResponse;

public class JobPostDocumentConverter {

	public static JobSearchResponse toJobSearchResponse(JobPostDocument doc, Double distance, String imageUrl) {

		return JobSearchResponse.builder()
			.id(doc.getId())
			.title(doc.getTitle())
			.address(doc.getAddress())
			.displayDistance(distance != null ? String.format("%.1fkm", distance) : null)
			.applicationCount(doc.getApplicationCount())
			.imageUrl(imageUrl)
			.build();
	}
}
