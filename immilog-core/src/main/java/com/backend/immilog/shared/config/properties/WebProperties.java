package com.backend.immilog.shared.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.web")
public record WebProperties(
        FileStorage fileStorage,
        Cors cors
) {
    public record FileStorage(
            String directory,
            String resourcePattern
    ) {
        public FileStorage {
            if (resourcePattern == null) {
                resourcePattern = "/images/**";
            }
        }
    }

    public record Cors(
            List<CorsMapping> mappings
    ) {}

    public record CorsMapping(
            String pathPattern,
            List<String> allowedOrigins,
            List<String> allowedMethods,
            List<String> allowedHeaders,
            boolean allowCredentials
    ) {
        public CorsMapping {
            if (allowedMethods == null) {
                allowedMethods = List.of("*");
            }
            if (allowedHeaders == null) {
                allowedHeaders = List.of("*");
            }
        }
    }
}