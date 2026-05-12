package com.async_requests.demo.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "app.cache")
@Configuration
public class CacheConfig {
    private Duration ttl;

    @Bean
    public Cache<Object, Object> cache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(ttl)
                .maximumSize(1000)
                .build();
    }
}
