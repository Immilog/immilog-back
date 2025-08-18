package com.backend.immilog.shared.config.event;

import com.backend.immilog.shared.infrastructure.event.RedisStreamsPushEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import jakarta.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;

@Slf4j
@Configuration
public class RedisEventConfig {

    // Redis Streams 설정
    public static final String DOMAIN_EVENT_STREAM = "domain-events-stream";
    public static final String COMPENSATION_EVENT_STREAM = "compensation-events-stream";
    
    // Consumer Group 이름들
    public static final String DOMAIN_EVENT_GROUP = "domain-event-handlers";
    public static final String COMPENSATION_EVENT_GROUP = "compensation-event-handlers";

    @Value("${spring.application.name:immilog}")
    private String applicationName;
    
    private final RedisTemplate<String, Object> eventRedisTemplate;
    private final RedisStreamsPushEventListener eventListener;

    public RedisEventConfig(
            RedisTemplate<String, Object> eventRedisTemplate,
            RedisStreamsPushEventListener eventListener
    ) {
        this.eventRedisTemplate = eventRedisTemplate;
        this.eventListener = eventListener;
    }

    @PostConstruct
    public void initializeStreamsAndGroups() {
        // 스트림과 Consumer Group 초기화
        createStreamAndGroup(DOMAIN_EVENT_STREAM, DOMAIN_EVENT_GROUP);
        createStreamAndGroup(COMPENSATION_EVENT_STREAM, COMPENSATION_EVENT_GROUP);
    }

    @Bean
    @SuppressWarnings({"unchecked", "rawtypes"})
    public StreamMessageListenerContainer streamMessageListenerContainer(
            RedisConnectionFactory connectionFactory) {
        
        // 제네릭 타입 문제를 우회하기 위해 raw type 사용
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions options = 
            StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                .batchSize(10)
                .pollTimeout(Duration.ofSeconds(1))
                .build();

        StreamMessageListenerContainer container = 
            StreamMessageListenerContainer.create(connectionFactory, options);

        // Consumer 이름을 서버별로 고유하게 생성
        String consumerName = generateConsumerName();

        // 도메인 이벤트 스트림 구독 - Raw StreamListener 사용
        StreamListener rawListener = (StreamListener) eventListener;
        
        Subscription domainSubscription = container.receive(
            Consumer.from(DOMAIN_EVENT_GROUP, consumerName),
            StreamOffset.create(DOMAIN_EVENT_STREAM, ReadOffset.lastConsumed()),
            rawListener
        );

        // 보상 이벤트 스트림 구독
        Subscription compensationSubscription = container.receive(
            Consumer.from(COMPENSATION_EVENT_GROUP, consumerName),
            StreamOffset.create(COMPENSATION_EVENT_STREAM, ReadOffset.lastConsumed()),
            rawListener
        );

        log.info("Configured Redis Streams Push listeners with consumer: {}", consumerName);

        return container;
    }

    private String generateConsumerName() {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            return applicationName + "-" + hostname + "-" + System.currentTimeMillis();
        } catch (UnknownHostException e) {
            return applicationName + "-" + System.currentTimeMillis();
        }
    }

    private void createStreamAndGroup(String streamName, String groupName) {
        try {
            // Consumer Group 생성 (스트림이 없으면 자동 생성)
            eventRedisTemplate.opsForStream().createGroup(streamName, ReadOffset.from("0-0"), groupName);
            log.info("Created consumer group '{}' for stream '{}'", groupName, streamName);
        } catch (Exception e) {
            // 이미 존재하는 경우 무시
            log.debug("Consumer group '{}' already exists for stream '{}'", groupName, streamName);
        }
    }
}