package com.async_requests.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class BankService {
    private final WebClient webClient;

    public BankService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<String> getInformation() {
        return webClient.get()
                .retrieve()
                .bodyToMono(String.class);
    }
}
