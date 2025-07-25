package com.backend.immilog.global.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final WebProperties webProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        webProperties.cors().mappings().forEach(mapping -> {
            var corsRegistration = registry.addMapping(mapping.pathPattern())
                    .allowedOrigins(mapping.allowedOrigins().toArray(String[]::new))
                    .allowedMethods(mapping.allowedMethods().toArray(String[]::new))
                    .allowedHeaders(mapping.allowedHeaders().toArray(String[]::new));

            if (mapping.allowCredentials()) {
                corsRegistration.allowCredentials(true);
            }
        });
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        var fileStorage = webProperties.fileStorage();
        registry.addResourceHandler(fileStorage.resourcePattern())
                .addResourceLocations("file:" + fileStorage.directory() + "/");
    }
}