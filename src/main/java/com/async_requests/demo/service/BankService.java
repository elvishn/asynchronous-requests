package com.async_requests.demo.service;

import com.async_requests.demo.configuration.CacheProperties;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.time.Duration;
import java.util.UUID;

@Service
@EnableConfigurationProperties(CacheProperties.class)
// поднимает Bean CacheProperties, без нее пришлось
// бы писать @Component в самом CacheProperties
public class BankService {
    private final WebClient webClient;
    private final Cache<Object, Object> cache;
    private final CacheProperties cacheProperties;

    public BankService(WebClient webClient, CacheProperties cacheProperties) {
        this.webClient = webClient;
        this.cacheProperties = cacheProperties;
        System.out.println("CACHE INIT WITH " + cacheProperties.ttlSec + " TTL in sec");
        this.cache = Caffeine.newBuilder()
                .expireAfter(Expiry.creating((key, value) -> Duration.ofSeconds(cacheProperties.ttlSec)))
                .maximumSize(1000)
                .build();
    }

    private Mono<String> goToBankService() {
        return webClient.get()
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> getInformation(Integer ttl) {
        String id = UUID.randomUUID().toString();
        if (ttl == null) {
            return Mono
                    .fromCallable(() ->
                            goToBankService()
                                    .subscribeOn(Schedulers.boundedElastic())
                                    .subscribe(it -> cache.put(id, it))
                    )
                    .thenReturn(id);
        }

        return Mono
                .fromCallable(() ->
                        goToBankService()
                                .subscribeOn(Schedulers.boundedElastic())
                                .subscribe(it -> cache.policy().expireVariably().orElseThrow()
                                        .put(id, it, Duration.ofSeconds(ttl)))
                )
                .thenReturn(id);
    }


    public String longPolling(String id) {
         return cache.getIfPresent(id).toString();
    }
}
