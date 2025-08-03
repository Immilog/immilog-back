package com.backend.immilog.shared.config.properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        JwtProperties.class,
        WebProperties.class,
        RedisProperties.class,
        GeocodeProperties.class
})
public class PropertiesConfig {
}