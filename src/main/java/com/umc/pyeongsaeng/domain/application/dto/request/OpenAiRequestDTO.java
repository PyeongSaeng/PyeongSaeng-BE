package com.umc.pyeongsaeng.domain.application.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpenAiRequestDTO {
	private String experience;
	private String jobDescription;
	private String question;
}
