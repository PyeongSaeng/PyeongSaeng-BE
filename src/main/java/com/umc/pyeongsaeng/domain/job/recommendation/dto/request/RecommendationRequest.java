package com.umc.pyeongsaeng.domain.job.recommendation.dto.request;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendationRequest {
	private Long userId; // 혹시라도 명시적으로 ID를 받을 일이 있다면
}
