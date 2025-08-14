package com.backend.immilog.shared.infrastructure.event;

import com.backend.immilog.comment.domain.event.CommentCreatedEvent;
import com.backend.immilog.post.application.event.CommentCreatedEventHandler;
import com.backend.immilog.post.application.event.PostCompensationEventHandler;
import com.backend.immilog.post.application.services.PostCommandService;
import com.backend.immilog.post.application.services.PostQueryService;
import com.backend.immilog.post.domain.events.PostCompensationEvent;
import com.backend.immilog.post.domain.model.post.Post;
import com.backend.immilog.shared.config.properties.EventProperties;
import com.backend.immilog.shared.domain.event.DomainEvents;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("보상 트랜잭션 통합 테스트")
class CompensationTransactionIntegrationTest {

    @Mock
    private PostQueryService postQueryService;
    
    @Mock
    private PostCommandService postCommandService;
    
    @Mock
    private RedisTemplate<String, Object> eventRedisTemplate;
    
    @Mock
    private ApplicationContext applicationContext;
    
    @Mock
    private Post mockPost;
    
    @Mock
    private Post updatedPost;
    
    @Mock
    private Post compensatedPost;

    private ObjectMapper objectMapper;
    private EventProperties eventProperties;
    private RedisEventPublisher redisEventPublisher;
    private CommentCreatedEventHandler commentCreatedEventHandler;
    private PostCompensationEventHandler compensationEventHandler;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        eventProperties = new EventProperties(true, 1.0, true); // 100% 실패, 보상 활성화
        
        redisEventPublisher = new RedisEventPublisher(eventRedisTemplate, objectMapper);
        commentCreatedEventHandler = new CommentCreatedEventHandler(postQueryService, postCommandService, eventProperties);
        compensationEventHandler = new PostCompensationEventHandler(postQueryService, postCommandService);
    }

    @Test
    @DisplayName("전체 보상 트랜잭션 플로우 테스트 - 실패 -> 보상 이벤트 발행 -> 보상 처리")
    void fullCompensationTransactionFlow() {
        // given - 100% 실패율, 보상 활성화로 핸들러 생성
        EventProperties failureProperties = new EventProperties(true, 1.0, true);
        CommentCreatedEventHandler failureHandler = new CommentCreatedEventHandler(postQueryService, postCommandService, failureProperties);
        CommentCreatedEvent originalEvent = new CommentCreatedEvent("comment1", "post1", "user1");

        // 보상 이벤트 처리 시 성공 설정
        when(postQueryService.getPostById("post1")).thenReturn(mockPost);
        when(mockPost.decreaseCommentCount()).thenReturn(compensatedPost);

        try (MockedStatic<DomainEvents> domainEventsMock = mockStatic(DomainEvents.class)) {
            // when - 1단계: 원본 이벤트 처리 (실패 예상)
            failureHandler.handle(originalEvent);

            // then - 1단계: 보상 이벤트가 발행되었는지 확인
            domainEventsMock.verify(() -> DomainEvents.raiseCompensationEvent(any(PostCompensationEvent.CommentCountIncreaseCompensation.class)), times(1));

            // when - 2단계: 보상 이벤트 처리 시뮬레이션
            PostCompensationEvent.CommentCountIncreaseCompensation compensationEvent = 
                new PostCompensationEvent.CommentCountIncreaseCompensation("tx-123", "comment1", "post1");
            
            compensationEventHandler.handle(compensationEvent);

            // then - 2단계: 보상 처리가 정상적으로 실행되었는지 확인
            verify(postQueryService).getPostById("post1");
            verify(mockPost).decreaseCommentCount();
            verify(postCommandService).save(compensatedPost);
        }
    }

    @Test
    @DisplayName("실패 시뮬레이션 비활성화 시 정상 처리")
    void normalProcessingWhenSimulationDisabled() {
        // given
        EventProperties normalEventProperties = new EventProperties(false, 0.0, true);
        CommentCreatedEventHandler normalHandler = new CommentCreatedEventHandler(postQueryService, postCommandService, normalEventProperties);
        CommentCreatedEvent event = new CommentCreatedEvent("comment1", "post1", "user1");
        
        when(postQueryService.getPostById("post1")).thenReturn(mockPost);
        when(mockPost.increaseCommentCount()).thenReturn(updatedPost);

        try (MockedStatic<DomainEvents> domainEventsMock = mockStatic(DomainEvents.class)) {
            // when
            normalHandler.handle(event);

            // then - 정상 처리되고 보상 이벤트는 발행되지 않음
            verify(postQueryService).getPostById("post1");
            verify(mockPost).increaseCommentCount();
            verify(postCommandService).save(updatedPost);
            
            domainEventsMock.verify(() -> DomainEvents.raiseCompensationEvent(any()), never());
        }
    }

    @Test
    @DisplayName("보상 트랜잭션 비활성화 시 보상 이벤트 발행하지 않음")
    void noCompensationWhenDisabled() {
        // given
        EventProperties noCompensationProperties = new EventProperties(true, 1.0, false); // 실패하지만 보상 비활성화
        CommentCreatedEventHandler handlerWithoutCompensation = new CommentCreatedEventHandler(postQueryService, postCommandService, noCompensationProperties);
        CommentCreatedEvent event = new CommentCreatedEvent("comment1", "post1", "user1");

        try (MockedStatic<DomainEvents> domainEventsMock = mockStatic(DomainEvents.class)) {
            // when
            handlerWithoutCompensation.handle(event);

            // then - 실패했지만 보상 이벤트는 발행되지 않음
            domainEventsMock.verify(() -> DomainEvents.raiseCompensationEvent(any()), never());
        }
    }

    @Test
    @DisplayName("보상 처리 중 실패 시에도 시스템 안정성 유지")
    void compensationFailureHandling() {
        // given
        PostCompensationEvent.CommentCountIncreaseCompensation compensationEvent = 
            new PostCompensationEvent.CommentCountIncreaseCompensation("tx-123", "comment1", "post1");
        
        // 보상 처리 중 실패 설정
        when(postQueryService.getPostById("post1")).thenThrow(new RuntimeException("Database connection failed"));

        // when - 보상 처리 실패해도 예외가 전파되지 않아야 함
        compensationEventHandler.handle(compensationEvent);

        // then - 실패했지만 시스템이 중단되지 않음
        verify(postQueryService).getPostById("post1");
        verify(postCommandService, never()).save(any());
    }

    @Test
    @DisplayName("부분 실패율 테스트 - 50% 실패율")
    void partialFailureRateTest() {
        // given
        EventProperties partialFailureProperties = new EventProperties(true, 0.5, true); // 50% 실패율
        CommentCreatedEventHandler partialFailureHandler = new CommentCreatedEventHandler(postQueryService, postCommandService, partialFailureProperties);
        CommentCreatedEvent event = new CommentCreatedEvent("comment1", "post1", "user1");
        
        // 정상 처리를 위한 설정 (lenient 모드로 불필요한 stubbing 경고 방지)
        lenient().when(postQueryService.getPostById("post1")).thenReturn(mockPost);
        lenient().when(mockPost.increaseCommentCount()).thenReturn(updatedPost);

        int totalAttempts = 100;

        try (MockedStatic<DomainEvents> domainEventsMock = mockStatic(DomainEvents.class)) {
            // when - 여러 번 실행하여 확률적 실패 테스트
            for (int i = 0; i < totalAttempts; i++) {
                partialFailureHandler.handle(event);
            }

            // then - 보상 이벤트 발행 여부로 실패율 확인
            // 50% 실패율이므로 대략 30~70회 정도 보상 이벤트가 발행되어야 함 (확률적 변동 고려)
            domainEventsMock.verify(() -> DomainEvents.raiseCompensationEvent(any()), atLeast(20));
            domainEventsMock.verify(() -> DomainEvents.raiseCompensationEvent(any()), atMost(80));
        }
    }
}