package com.backend.immilog.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class WebfluxExecutorConfig {

    @Bean
    @Primary
    public Executor webfluxExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
