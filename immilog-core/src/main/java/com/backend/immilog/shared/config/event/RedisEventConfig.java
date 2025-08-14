package com.backend.immilog.shared.config.event;

import com.backend.immilog.shared.infrastructure.event.RedisEventSubscriber;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisEventConfig {

    public static final String DOMAIN_EVENT_CHANNEL = "domain-events";
    public static final String COMPENSATION_EVENT_CHANNEL = "compensation-events";

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            @Qualifier("domainEventListenerAdapter") MessageListenerAdapter domainEventListenerAdapter,
            @Qualifier("compensationEventListenerAdapter") MessageListenerAdapter compensationEventListenerAdapter) {
        
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        
        // 도메인 이벤트 채널 구독
        container.addMessageListener(domainEventListenerAdapter, new ChannelTopic(DOMAIN_EVENT_CHANNEL));
        
        // 보상 이벤트 채널 구독
        container.addMessageListener(compensationEventListenerAdapter, new ChannelTopic(COMPENSATION_EVENT_CHANNEL));
        
        return container;
    }

    @Bean
    public MessageListenerAdapter domainEventListenerAdapter(RedisEventSubscriber redisEventSubscriber) {
        return new MessageListenerAdapter(redisEventSubscriber, "handleDomainEvent");
    }

    @Bean
    public MessageListenerAdapter compensationEventListenerAdapter(RedisEventSubscriber redisEventSubscriber) {
        return new MessageListenerAdapter(redisEventSubscriber, "handleCompensationEvent");
    }

    @Bean
    public ChannelTopic domainEventTopic() {
        return new ChannelTopic(DOMAIN_EVENT_CHANNEL);
    }

    @Bean
    public ChannelTopic compensationEventTopic() {
        return new ChannelTopic(COMPENSATION_EVENT_CHANNEL);
    }
}