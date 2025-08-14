package com.backend.immilog.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;

@Configuration
public class MongoConfig extends AbstractReactiveMongoConfiguration {
    
    @Override
    protected String getDatabaseName() {
        return "immilog_chat";
    }
}