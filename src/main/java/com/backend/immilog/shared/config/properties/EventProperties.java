package com.backend.immilog.shared.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "event")
public record EventProperties(
        boolean simulateFailure,
        double failureRate,
        boolean enableCompensation
) {
    public EventProperties() {
        this(false, 0.0, true);
    }
}