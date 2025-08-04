package com.backend.immilog.shared.infrastructure.event;

import com.backend.immilog.comment.domain.event.CommentCreatedEvent;
import com.backend.immilog.post.domain.events.PostCompensationEvent;
import com.backend.immilog.shared.config.event.RedisEventConfig;
import com.backend.immilog.shared.infrastructure.event.dto.RedisEventMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

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
        RedisEventMessage eventMessage = new RedisEventMessage("msg-123", event.getClass().getName(), "{\"test\":\"data\"}", event.occurredAt());
        String serializedMessage = "{\"messageId\":\"msg-123\",\"eventType\":\"CommentCreatedEvent\"}";

        when(objectMapper.writeValueAsString(any(RedisEventMessage.class))).thenReturn(serializedMessage);
        when(objectMapper.writeValueAsString(event)).thenReturn("{\"test\":\"data\"}");

        // when
        redisEventPublisher.publishDomainEvent(event);

        // then
        ArgumentCaptor<String> channelCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        
        verify(eventRedisTemplate).convertAndSend(channelCaptor.capture(), messageCaptor.capture());
        
        assertThat(channelCaptor.getValue()).isEqualTo(RedisEventConfig.DOMAIN_EVENT_CHANNEL);
        assertThat(messageCaptor.getValue()).isEqualTo(serializedMessage);
    }

    @Test
    @DisplayName("보상 이벤트 발행 성공")
    void publishCompensationEvent_Success() throws JsonProcessingException {
        // given
        PostCompensationEvent.CommentCountIncreaseCompensation event = 
            new PostCompensationEvent.CommentCountIncreaseCompensation("tx-123", "event-456", "post-789");
        String serializedMessage = "{\"messageId\":\"msg-123\",\"eventType\":\"CompensationEvent\"}";

        when(objectMapper.writeValueAsString(any(RedisEventMessage.class))).thenReturn(serializedMessage);
        when(objectMapper.writeValueAsString(event)).thenReturn("{\"transactionId\":\"tx-123\"}");

        // when
        redisEventPublisher.publishCompensationEvent(event);

        // then
        ArgumentCaptor<String> channelCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        
        verify(eventRedisTemplate).convertAndSend(channelCaptor.capture(), messageCaptor.capture());
        
        assertThat(channelCaptor.getValue()).isEqualTo(RedisEventConfig.COMPENSATION_EVENT_CHANNEL);
        assertThat(messageCaptor.getValue()).isEqualTo(serializedMessage);
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

        verify(eventRedisTemplate, never()).convertAndSend(any(), any());
    }

    @Test
    @DisplayName("보상 이벤트 직렬화 실패 시 예외 발생")
    void publishCompensationEvent_SerializationFailure() throws JsonProcessingException {
        // given
        PostCompensationEvent.CommentCountIncreaseCompensation event = 
            new PostCompensationEvent.CommentCountIncreaseCompensation("tx-123", "event-456", "post-789");
        
        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonProcessingException("Serialization error") {});

        // when & then
        assertThatThrownBy(() -> redisEventPublisher.publishCompensationEvent(event))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to publish compensation event");

        verify(eventRedisTemplate, never()).convertAndSend(any(), any());
    }

    @Test
    @DisplayName("이벤트 메시지 생성 시 필수 필드 확인")
    void createEventMessage_ContainsRequiredFields() throws JsonProcessingException {
        // given
        CommentCreatedEvent event = new CommentCreatedEvent("comment1", "post1", "user1");
        String eventPayload = "{\"commentId\":\"comment1\",\"postId\":\"post1\"}";
        
        when(objectMapper.writeValueAsString(event)).thenReturn(eventPayload);
        when(objectMapper.writeValueAsString(any(RedisEventMessage.class))).thenAnswer(invocation -> {
            RedisEventMessage msg = invocation.getArgument(0);
            // 메시지가 필수 필드를 포함하는지 검증
            assertThat(msg.messageId()).isNotNull();
            assertThat(msg.eventType()).isEqualTo(event.getClass().getName());
            assertThat(msg.payload()).isEqualTo(eventPayload);
            assertThat(msg.publishedAt()).isNotNull();
            return "{\"test\":\"serialized\"}";
        });

        // when
        redisEventPublisher.publishDomainEvent(event);

        // then
        verify(objectMapper).writeValueAsString(any(RedisEventMessage.class));
    }
}