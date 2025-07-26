package com.backend.immilog.shared.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "spring.data.redis")
public record RedisProperties(
        String host,
        int port
) {

    public RedisProperties {
        if (host == null) {
            host = "localhost";
        }
        if (port <= 0) {
            port = 6379;
        }
    }

    public record Cache(
            Duration ttl,
            String keyPrefix
    ) {
        public Cache {
            if (ttl == null) {
                ttl = Duration.ofHours(3);
            }
            if (keyPrefix == null) {
                keyPrefix = "";
            }
        }
    }
}
