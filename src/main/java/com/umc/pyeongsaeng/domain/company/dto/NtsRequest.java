package com.umc.pyeongsaeng.domain.company.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class NtsRequest {
	@Getter
	@AllArgsConstructor
	public static class NtsBusinessmanStatusRequestDto {
		@JsonProperty("b_no")
		private List<String> businessNumbers;
	}
}
