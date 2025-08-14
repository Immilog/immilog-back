package com.backend.immilog.shared.config.web;

import com.backend.immilog.shared.config.properties.WebProperties;
import com.backend.immilog.shared.resolver.CurrentUserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final WebProperties webProperties;
    private final CurrentUserArgumentResolver currentUserArgumentResolver;

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
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

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserArgumentResolver);
    }
}