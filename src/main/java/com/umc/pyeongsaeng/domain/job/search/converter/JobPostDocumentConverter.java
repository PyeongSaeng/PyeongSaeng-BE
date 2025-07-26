package com.umc.pyeongsaeng.domain.job.search.converter;

import com.umc.pyeongsaeng.domain.job.search.document.JobPostDocument;
import com.umc.pyeongsaeng.domain.job.search.dto.JobSearchResponse;

public class JobPostDocumentConverter {

	public static JobSearchResponse toJobSearchResponse(JobPostDocument doc, Double distance) {
		return JobSearchResponse.builder()
			.id(doc.getId())
			.title(doc.getTitle())
			//.companyName(doc.getCompany())
			.address(doc.getAddress())
			.hourlyWage(doc.getHourlyWage())
			.monthlySalary(doc.getMonthlySalary())
			.yearSalary(doc.getYearSalary())
			.displayDistance(distance != null ? String.format("%.1fkm", distance) : null)
			.build();
	}
}
