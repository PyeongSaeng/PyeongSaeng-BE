package com.umc.pyeongsaeng.global.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.servers.Server;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Value("${swagger.server-url}")
	private String serverUrl;

	@Bean
	public OpenAPI openAPI() {
		String jwtSchemeName = "JWT Authentication";

		SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
		Components components = new Components()
			.addSecuritySchemes(jwtSchemeName, new SecurityScheme()
				.name(jwtSchemeName)
				.type(SecurityScheme.Type.HTTP)
				.scheme("bearer")
				.bearerFormat("JWT"));

		Server server = new Server();
		server.setUrl(serverUrl);

		return new OpenAPI()
			.info(apiInfo())
			.addSecurityItem(securityRequirement)
			.components(components)
			.servers(List.of(server));
	}

	private Info apiInfo() {
		return new Info()
			.title("평생 API 명세서")
			.description("평생의 주요 API 기능을 제공합니다.")
			.version("1.0.0");
	}
}

