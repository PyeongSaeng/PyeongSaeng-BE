package com.umc.pyeongsaeng.domain.job.recommendation.dto.request;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendationRequest {
	private Long userId;
}
