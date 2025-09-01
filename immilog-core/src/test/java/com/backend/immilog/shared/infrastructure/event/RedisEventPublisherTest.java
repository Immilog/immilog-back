package com.backend.immilog.shared.infrastructure.event;

import com.backend.immilog.comment.domain.event.CommentCreatedEvent;
import com.backend.immilog.post.domain.events.PostCompensationEvent;
import com.backend.immilog.shared.config.event.RedisEventConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RedisEventPublisher 테스트")
class RedisEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> eventRedisTemplate;
    
    @Mock
    private StreamOperations<String, Object, Object> streamOperations;
    
    @Mock
    private ObjectMapper objectMapper;

    private RedisEventPublisher redisEventPublisher;

    @BeforeEach
    void setUp() {
        redisEventPublisher = new RedisEventPublisher(eventRedisTemplate, objectMapper);
    }

    @Test
    @DisplayName("도메인 이벤트 발행 성공")
    void publishDomainEvent_Success() throws JsonProcessingException {
        // given
        CommentCreatedEvent event = new CommentCreatedEvent("comment1", "post1", "user1");
        String eventPayload = "{\"test\":\"data\"}";
        RecordId recordId = RecordId.of("1234567890-0");

        when(eventRedisTemplate.opsForStream()).thenReturn(streamOperations);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"eventMessage\":\"test\"}");
        when(streamOperations.add(eq(RedisEventConfig.DOMAIN_EVENT_STREAM), any(Map.class))).thenReturn(recordId);

        // when
        redisEventPublisher.publishDomainEvent(event);

        // then
        ArgumentCaptor<Map<String, String>> fieldsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(streamOperations).add(eq(RedisEventConfig.DOMAIN_EVENT_STREAM), fieldsCaptor.capture());
        
        Map<String, String> capturedFields = fieldsCaptor.getValue();
        assertThat(capturedFields).containsKey("event");
        assertThat(capturedFields).containsKey("messageId");
        assertThat(capturedFields).containsKey("eventType");
    }

    @Test
    @DisplayName("보상 이벤트 발행 성공")
    void publishCompensationEvent_Success() throws JsonProcessingException {
        // given
        PostCompensationEvent.CommentCountIncreaseCompensation event = 
            new PostCompensationEvent.CommentCountIncreaseCompensation("tx-123", "event-456", "post-789");
        String eventPayload = "{\"transactionId\":\"tx-123\"}";
        RecordId recordId = RecordId.of("1234567890-1");

        when(eventRedisTemplate.opsForStream()).thenReturn(streamOperations);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"eventMessage\":\"compensation\"}");
        when(streamOperations.add(eq(RedisEventConfig.COMPENSATION_EVENT_STREAM), any(Map.class))).thenReturn(recordId);

        // when
        redisEventPublisher.publishCompensationEvent(event);

        // then
        ArgumentCaptor<Map<String, String>> fieldsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(streamOperations).add(eq(RedisEventConfig.COMPENSATION_EVENT_STREAM), fieldsCaptor.capture());
        
        Map<String, String> capturedFields = fieldsCaptor.getValue();
        assertThat(capturedFields).containsKey("event");
        assertThat(capturedFields).containsKey("messageId");
        assertThat(capturedFields).containsKey("eventType");
    }

    @Test
    @DisplayName("도메인 이벤트 직렬화 실패 시 예외 발생")
    void publishDomainEvent_SerializationFailure() throws JsonProcessingException {
        // given
        CommentCreatedEvent event = new CommentCreatedEvent("comment1", "post1", "user1");
        
        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonProcessingException("Serialization error") {});

        // when & then
        assertThatThrownBy(() -> redisEventPublisher.publishDomainEvent(event))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to publish domain event");

        verify(streamOperations, never()).add(any(String.class), any(Map.class));
    }
}