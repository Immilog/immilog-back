package com.backend.immilog.shared.infrastructure.event;

import com.backend.immilog.comment.domain.event.CommentCreatedEvent;
import com.backend.immilog.post.application.event.CommentCreatedEventHandler;
import com.backend.immilog.post.domain.events.PostCompensationEvent;
import com.backend.immilog.shared.domain.event.DomainEventHandler;
import com.backend.immilog.shared.infrastructure.event.dto.RedisEventMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.Message;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RedisEventSubscriber 테스트")
class RedisEventSubscriberTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private CommentCreatedEventHandler commentCreatedEventHandler;

    @Mock
    private DomainEventHandler<PostCompensationEvent.CommentCountIncreaseCompensation> compensationEventHandler;

    @Mock
    private Message message;

    private RedisEventSubscriber redisEventSubscriber;

    @BeforeEach
    void setUp() {
        // ApplicationContext에서 핸들러를 찾을 수 있도록 설정
        Map<String, DomainEventHandler> handlers = Map.of(
                "commentCreatedEventHandler", commentCreatedEventHandler,
                "compensationEventHandler", compensationEventHandler
        );

        when(applicationContext.getBeansOfType(DomainEventHandler.class)).thenReturn(handlers);
        when(commentCreatedEventHandler.getEventType()).thenReturn(CommentCreatedEvent.class);
        when(compensationEventHandler.getEventType()).thenReturn(PostCompensationEvent.CommentCountIncreaseCompensation.class);

        redisEventSubscriber = new RedisEventSubscriber(objectMapper, applicationContext);
    }

    @Test
    @DisplayName("도메인 이벤트 메시지 처리 성공")
    void handleDomainEvent_Success() throws Exception {
        // given
        CommentCreatedEvent event = new CommentCreatedEvent("comment1", "post1", "user1");
        RedisEventMessage eventMessage = new RedisEventMessage(
                "msg-123",
                CommentCreatedEvent.class.getName(),
                "{\"commentId\":\"comment1\"}",
                LocalDateTime.now()
        );
        String messageBody = "{\"messageId\":\"msg-123\",\"eventType\":\"CommentCreatedEvent\"}";

        when(objectMapper.readValue(messageBody, RedisEventMessage.class)).thenReturn(eventMessage);
        when(objectMapper.readValue(eventMessage.payload(), CommentCreatedEvent.class)).thenReturn(event);

        // when
        redisEventSubscriber.handleDomainEvent(messageBody);

        // then
        verify(commentCreatedEventHandler).handle(event);
    }

    @Test
    @DisplayName("보상 이벤트 메시지 처리 성공")
    void handleCompensationEvent_Success() throws Exception {
        // given
        PostCompensationEvent.CommentCountIncreaseCompensation event =
                new PostCompensationEvent.CommentCountIncreaseCompensation("tx-123", "event-456", "post-789");
        RedisEventMessage eventMessage = new RedisEventMessage(
                "msg-123",
                PostCompensationEvent.CommentCountIncreaseCompensation.class.getName(),
                "{\"transactionId\":\"tx-123\"}",
                LocalDateTime.now()
        );
        String messageBody = "{\"messageId\":\"msg-123\",\"eventType\":\"CompensationEvent\"}";

        when(objectMapper.readValue(messageBody, RedisEventMessage.class)).thenReturn(eventMessage);
        when(objectMapper.readValue(eventMessage.payload(), PostCompensationEvent.CommentCountIncreaseCompensation.class)).thenReturn(event);

        // when
        redisEventSubscriber.handleCompensationEvent(messageBody);

        // then
        verify(compensationEventHandler).handle(event);
    }

    @Test
    @DisplayName("Redis 메시지 채널별 라우팅")
    void onMessage_ChannelRouting() throws Exception {
        // given - 도메인 이벤트 채널
        when(message.getBody()).thenReturn("test message".getBytes());
        when(message.getChannel()).thenReturn("domain-events".getBytes());

        RedisEventSubscriber spySubscriber = spy(redisEventSubscriber);

        // when
        spySubscriber.onMessage(message, null);

        // then
        verify(spySubscriber).handleDomainEvent("test message");
    }

    @Test
    @DisplayName("Redis 메시지 보상 채널 라우팅")
    void onMessage_CompensationChannelRouting() throws Exception {
        // given - 보상 이벤트 채널
        when(message.getBody()).thenReturn("compensation message".getBytes());
        when(message.getChannel()).thenReturn("compensation-events".getBytes());

        RedisEventSubscriber spySubscriber = spy(redisEventSubscriber);

        // when
        spySubscriber.onMessage(message, null);

        // then
        verify(spySubscriber).handleCompensationEvent("compensation message");
    }

    @Test
    @DisplayName("등록되지 않은 이벤트 타입 처리")
    void processEventMessage_UnregisteredEventType() throws Exception {
        // given
        RedisEventMessage eventMessage = new RedisEventMessage(
                "msg-123",
                "com.example.UnknownEvent",
                "{\"test\":\"data\"}",
                LocalDateTime.now()
        );
        String messageBody = "{\"messageId\":\"msg-123\",\"eventType\":\"UnknownEvent\"}";

        when(objectMapper.readValue(messageBody, RedisEventMessage.class)).thenReturn(eventMessage);

        // when
        redisEventSubscriber.handleDomainEvent(messageBody);

        // then - 핸들러가 없어도 예외가 발생하지 않아야 함
        verify(commentCreatedEventHandler, never()).handle(any());
        verify(compensationEventHandler, never()).handle(any());
    }

    @Test
    @DisplayName("잘못된 이벤트 메시지 처리")
    void handleDomainEvent_InvalidMessage() throws Exception {
        // given
        String invalidMessageBody = "invalid json";

        when(objectMapper.readValue(invalidMessageBody, RedisEventMessage.class))
                .thenThrow(new RuntimeException("Invalid JSON"));

        // when
        redisEventSubscriber.handleDomainEvent(invalidMessageBody);

        // then - 예외가 발생해도 애플리케이션이 중단되지 않아야 함
        verify(commentCreatedEventHandler, never()).handle(any());
    }

    @Test
    @DisplayName("이벤트 역직렬화 실패 처리")
    void processEventMessage_DeserializationFailure() throws Exception {
        // given
        RedisEventMessage eventMessage = new RedisEventMessage(
                "msg-123",
                CommentCreatedEvent.class.getName(),
                "invalid event data",
                LocalDateTime.now()
        );
        String messageBody = "{\"messageId\":\"msg-123\",\"eventType\":\"CommentCreatedEvent\"}";

        when(objectMapper.readValue(messageBody, RedisEventMessage.class)).thenReturn(eventMessage);
        when(objectMapper.readValue(eventMessage.payload(), CommentCreatedEvent.class))
                .thenThrow(new RuntimeException("Deserialization failed"));

        // when
        redisEventSubscriber.handleDomainEvent(messageBody);

        // then
        verify(commentCreatedEventHandler, never()).handle(any());
    }
}