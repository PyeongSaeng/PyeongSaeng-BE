package com.umc.pyeongsaeng.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class OpenAiConfig {

	@Value("${openai.api.key}")
	private String apiKey;

	@Bean(name = "openAiRestTemplate")
	public RestTemplate openAiRestTemplate() {

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add((request, body, execution) -> {
			request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
			request.getHeaders().set("Authorization", "Bearer " + apiKey);
			return execution.execute(request, body);
		});
		return restTemplate;
	}
}
