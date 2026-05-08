package com.async_requests.demo.controller;

import com.async_requests.demo.service.BankService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.time.Duration;
import java.util.UUID;


@RestController
@RequestMapping("/api")
public class Controller {
    private final BankService service;
    private final Cache<Object, Object> cache;

    public Controller(BankService service) {
        this.service = service;
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(100))
                .maximumSize(1000)
                .build();
    }

    @GetMapping
    public Mono<String> getInformation() {
        String id = UUID.randomUUID().toString();
        return Mono
                .fromCallable(() ->
                        service.getInformation()
                                .subscribeOn(Schedulers.boundedElastic())
                                .subscribe(it -> cache.put(id, it))
                )
                .thenReturn(id); // add in cashe guava
    }
/*
    @GetMapping
    public Mono<String> getInformation() {
        String id = UUID.randomUUID().toString();
        return service.getInformation()
                .flatMap(result ->
                        Mono.fromRunnable(() -> cache.put(id, result))
                                .thenReturn(id)
                )
                .subscribeOn(Schedulers.boundedElastic());
    }*/

    @GetMapping("/{id}")
    public String loadPulling(@PathVariable String id) {
        return cache.getIfPresent(id).toString();
    }
}
