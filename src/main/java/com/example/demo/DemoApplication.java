package com.example.demo;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.apache.http.client.config.RequestConfig;

@SpringBootApplication
@Configuration
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean(name = "restTemplete")
	public RestTemplate restTemplate() {
		RequestConfig config = RequestConfig.custom()
				.setConnectTimeout(20000)
				.setConnectionRequestTimeout(20000)
				.setSocketTimeout(20000)
				.build();
		CloseableHttpClient client = HttpClientBuilder
				.create()
				.setDefaultRequestConfig(config)
				.build();
		return new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
	}

}

