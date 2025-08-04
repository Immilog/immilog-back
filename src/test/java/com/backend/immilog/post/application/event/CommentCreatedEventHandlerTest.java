package com.backend.immilog.post.application.event;

import com.backend.immilog.comment.domain.event.CommentCreatedEvent;
import com.backend.immilog.post.application.services.PostCommandService;
import com.backend.immilog.post.application.services.PostQueryService;
import com.backend.immilog.post.domain.model.post.Post;
import com.backend.immilog.shared.config.properties.EventProperties;
import com.backend.immilog.shared.domain.event.DomainEvents;
import com.backend.immilog.shared.infrastructure.event.RedisEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentCreatedEventHandler 테스트")
class CommentCreatedEventHandlerTest {

    @Mock private PostQueryService postQueryService;

    @Mock private PostCommandService postCommandService;

    @Mock private EventProperties eventProperties;

    @Mock private Post mockPost;

    @Mock private Post updatedPost;

    @Mock private ApplicationContext applicationContext;

    @Mock private RedisEventPublisher redisEventPublisher;

    private CommentCreatedEventHandler eventHandler;

    @BeforeEach
    void setUp() {
        eventHandler = new CommentCreatedEventHandler(postQueryService, postCommandService, eventProperties);
    }

    @Test
    @DisplayName("댓글 생성 이벤트 처리 성공 - 게시글 댓글 수 증가")
    void handleCommentCreatedEvent_Success() {
        // given
        CommentCreatedEvent event = new CommentCreatedEvent("comment1", "post1", "user1");

        when(eventProperties.simulateFailure()).thenReturn(false);
        when(postQueryService.getPostById("post1")).thenReturn(mockPost);
        when(mockPost.increaseCommentCount()).thenReturn(updatedPost);

        // when
        eventHandler.handle(event);

        // then
        verify(postQueryService).getPostById("post1");
        verify(mockPost).increaseCommentCount();
        verify(postCommandService).save(updatedPost);
        verify(eventProperties, never()).enableCompensation();
    }

    @Test
    @DisplayName("댓글 생성 이벤트 처리 실패 - 보상 이벤트 발행")
    void handleCommentCreatedEvent_FailureWithCompensation() {
        // given
        CommentCreatedEvent event = new CommentCreatedEvent("comment1", "post1", "user1");

        when(eventProperties.simulateFailure()).thenReturn(false);
        when(eventProperties.enableCompensation()).thenReturn(true);
        when(postQueryService.getPostById("post1")).thenThrow(new RuntimeException("Database error"));

        try (MockedStatic<DomainEvents> domainEventsMock = mockStatic(DomainEvents.class)) {
            // when
            eventHandler.handle(event);

            // then
            verify(postQueryService).getPostById("post1");
            verify(postCommandService, never()).save(any());
            verify(eventProperties).enableCompensation();

            domainEventsMock.verify(() -> DomainEvents.raiseCompensationEvent(any()), times(1));
        }
    }

    @Test
    @DisplayName("실패 시뮬레이션 활성화 - 보상 이벤트 발행")
    void handleCommentCreatedEvent_SimulatedFailure() {
        // given
        CommentCreatedEvent event = new CommentCreatedEvent("comment1", "post1", "user1");

        when(eventProperties.simulateFailure()).thenReturn(true);
        when(eventProperties.failureRate()).thenReturn(1.0);
        when(eventProperties.enableCompensation()).thenReturn(true);

        try (MockedStatic<DomainEvents> domainEventsMock = mockStatic(DomainEvents.class)) {
            // when
            eventHandler.handle(event);

            // then
            verify(postQueryService, never()).getPostById(any());
            verify(eventProperties).enableCompensation();

            domainEventsMock.verify(() -> DomainEvents.raiseCompensationEvent(any()), times(1));
        }
    }

    @Test
    @DisplayName("보상 이벤트 비활성화 - 보상 이벤트 발행하지 않음")
    void handleCommentCreatedEvent_CompensationDisabled() {
        // given
        CommentCreatedEvent event = new CommentCreatedEvent("comment1", "post1", "user1");

        when(eventProperties.simulateFailure()).thenReturn(false);
        when(eventProperties.enableCompensation()).thenReturn(false);
        when(postQueryService.getPostById("post1")).thenThrow(new RuntimeException("Database error"));

        try (MockedStatic<DomainEvents> domainEventsMock = mockStatic(DomainEvents.class)) {
            // when
            eventHandler.handle(event);

            // then
            verify(eventProperties).enableCompensation();

            // 보상 이벤트가 발행되지 않았는지 검증
            domainEventsMock.verify(() -> DomainEvents.raiseCompensationEvent(any()), never());
        }
    }

    @Test
    @DisplayName("실패 시뮬레이션 비활성화 - 낮은 실패율로도 실패하지 않음")
    void handleCommentCreatedEvent_SimulationDisabled() {
        // given
        CommentCreatedEvent event = new CommentCreatedEvent("comment1", "post1", "user1");

        when(eventProperties.simulateFailure()).thenReturn(false);
        when(postQueryService.getPostById("post1")).thenReturn(mockPost);
        when(mockPost.increaseCommentCount()).thenReturn(updatedPost);

        // when
        eventHandler.handle(event);

        // then
        verify(postQueryService).getPostById("post1");
        verify(mockPost).increaseCommentCount();
        verify(postCommandService).save(updatedPost);
    }
}