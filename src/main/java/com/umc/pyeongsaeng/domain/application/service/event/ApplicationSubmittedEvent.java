package com.umc.pyeongsaeng.domain.application.service.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApplicationSubmittedEvent {
	private final Long jobPostId;

}
