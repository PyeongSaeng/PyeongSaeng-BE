package com.umc.pyeongsaeng.global.common;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/actuator")
public class HealthController {

	@Getter
	@AllArgsConstructor
	private class Status {
		String status;
	}

	@GetMapping("/health")
	public Status health() {
		Status healthStatus = new Status("UP");
		return healthStatus;
	}
}
