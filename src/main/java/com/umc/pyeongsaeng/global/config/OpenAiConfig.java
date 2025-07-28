package com.umc.pyeongsaeng.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAiConfig {

	@Bean
	public RestTemplate template() {
		// Render 등 배포 환경에서도 잘 작동하도록 System.getenv 사용
		String apiKey = System.getenv("OPENAI_API_KEY");

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add((request, body, execution) -> {
			request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
			request.getHeaders().set("Authorization", "Bearer " + apiKey);
			return execution.execute(request, body);
		});
		return restTemplate;
	}
}
