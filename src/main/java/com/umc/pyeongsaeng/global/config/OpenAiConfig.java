package com.umc.pyeongsaeng.global.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class OpenAiConfig {

	@Bean(name = "openAiRestTemplate")
	public RestTemplate openAiRestTemplate() {
		Dotenv dotenv = Dotenv.configure()
			.ignoreIfMissing()
			.load();

		String apiKey = dotenv.get("OPENAI_API_KEY");

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add((request, body, execution) -> {
			request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
			request.getHeaders().set("Authorization", "Bearer " + apiKey);
			return execution.execute(request, body);
		});
		return restTemplate;
	}
}
