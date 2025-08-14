package com.backend.immilog.shared.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "geocode")
public record GeocodeProperties(
        String url,
        String key
) {}