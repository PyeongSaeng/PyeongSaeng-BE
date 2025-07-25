package com.umc.pyeongsaeng.domain.company.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class NtsResponse {

	@Setter
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class NtsBusinessmanStatusResponseDto {

		@JsonProperty("status_code")
		private String statusCode;

		@JsonProperty("match_cnt")
		private Integer matchCount;

		@JsonProperty("request_cnt")
		private Integer requestCount;

		private List<BusinessData> data;

		@Getter
		@NoArgsConstructor
		public static class BusinessData {
			@JsonProperty("b_no")
			private String businessNo;

			@JsonProperty("b_stt")
			private String businessStatus;

			@JsonProperty("b_stt_cd")
			private String businessStatusCode;

			@JsonProperty("tax_type")
			@Getter
			private String taxType;
		}
	}
}
