package com.async_requests.demo.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;


@Service
public class BankService {
    private final WebClient webClient;
    private final Cache<Object, Object> cache;
    private Cache<Object, Object> ttlCache;

    public BankService(WebClient webClient, Cache<Object, Object> cache) {
        this.webClient = webClient;
        this.cache = cache;
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
        ttlCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(ttl))
                .maximumSize(1000)
                .build();
        return Mono
                .fromCallable(() ->
                        goToBankService()
                                .subscribeOn(Schedulers.boundedElastic())
                                .subscribe(it -> ttlCache.put(id, it))
                )
                .thenReturn(id);
    }


    public String loadPulling(String id) {
        if (ttlCache != null) {
            for (Map.Entry<Object, @NonNull Object> entry : ttlCache.asMap().entrySet()) {
                Object value = ttlCache.getIfPresent(id);
                if (value != null) {
                    return value.toString();
                }
            }
        }
         return cache.getIfPresent(id).toString();
    }
}
