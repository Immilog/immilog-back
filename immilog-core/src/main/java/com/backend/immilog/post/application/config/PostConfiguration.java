package com.backend.immilog.post.application.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "post.config")
public class PostConfiguration {
    private Duration eventTimeout = Duration.ofSeconds(2);
    private int defaultPageSize = 10;
    private int popularPostLimit = 5;
    private int weeklyBestLimit = 10;
    private int minViewCount = 10;
    private int minCommentCount = 2;

}