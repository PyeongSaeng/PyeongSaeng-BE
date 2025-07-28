package com.umc.pyeongsaeng.global.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClient;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
@RequiredArgsConstructor
public class ElasticsearchConfig {

	@Value("${elasticsearch.host}")
	private String host;

	@Value("${elasticsearch.port}")
	private int port;

	@Value("${elasticsearch.scheme}")
	private String scheme;

	@Value("${elasticsearch.username}")
	private String username;

	@Value("${elasticsearch.password}")
	private String password;

	@Bean
	public ElasticsearchClient elasticsearchClient() {

		// 보안 설정
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY,
			new UsernamePasswordCredentials(username, password));

		RestClientBuilder.HttpClientConfigCallback httpClientConfigCallback = new RestClientBuilder.HttpClientConfigCallback() {
			@Override
			public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
				httpClientBuilder.disableAuthCaching();
				return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
			}
		};

		RestClientBuilder.RequestConfigCallback requestConfigCallBack = new RestClientBuilder.RequestConfigCallback() {
			@Override
			public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
				return requestConfigBuilder
					.setConnectTimeout(4000)
					.setSocketTimeout(60000);
			}
		};

		RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(host, port,scheme));

		restClientBuilder.setHttpClientConfigCallback(httpClientConfigCallback);

		restClientBuilder.setRequestConfigCallback(requestConfigCallBack);

		RestClient restClient = restClientBuilder.build();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper(objectMapper));

		return new ElasticsearchClient(transport);

	}
}

