package com.async_requests.demo.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
// ConfigurationProperties это != Configuration, Properties в Spring
// это совершенно другое понятие, в просторечие это - проперти, Confugration - конфиг
@ConfigurationProperties(prefix = "app.cache")
public class CacheProperties {
    public Long ttlSec;
}
