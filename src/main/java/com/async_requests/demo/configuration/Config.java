package com.async_requests.demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class Config {
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://search.worldbank.org/api/v3/wds?format=json&qterm=dog&rows=1")
                .build();
    }
}
