package com.backend.immilog.post.application.event;

import com.backend.immilog.comment.domain.event.CommentCreatedEvent;
import com.backend.immilog.post.domain.service.PostDomainService;
import com.backend.immilog.post.domain.model.post.PostId;
import com.backend.immilog.post.domain.events.PostCompensationEvent;
import com.backend.immilog.post.exception.PostErrorCode;
import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.shared.config.properties.EventProperties;
import com.backend.immilog.shared.domain.event.DomainEvents;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentCreatedEventHandler")
class CommentCreatedEventHandlerTest {

    @Mock
    private PostDomainService postDomainService;

    @Mock
    private EventProperties eventProperties;

    @InjectMocks
    private CommentCreatedEventHandler commentCreatedEventHandler;

    private CommentCreatedEvent testEvent;

    @BeforeEach
    void setUp() {
        testEvent = new CommentCreatedEvent("comment123", "post123", "user123");
    }

    @Nested
    @DisplayName("이벤트 처리 성공")
    class SuccessfulEventHandling {

        @Test
        @DisplayName("댓글 생성 이벤트 처리 성공")
        void handleCommentCreatedEventSuccess() {
            when(eventProperties.simulateFailure()).thenReturn(false);

            commentCreatedEventHandler.handle(testEvent);

            verify(postDomainService).incrementCommentCount(PostId.of("post123"));
        }

        @Test
        @DisplayName("댓글 수 증가 검증")
        void verifyCommentCountIncrease() {
            when(eventProperties.simulateFailure()).thenReturn(false);

            commentCreatedEventHandler.handle(testEvent);

            verify(postDomainService).incrementCommentCount(PostId.of("post123"));
        }

        @Test
        @DisplayName("시뮬레이션 실패 비활성화 시 정상 처리")
        void handleEventWithSimulationDisabled() {
            when(eventProperties.simulateFailure()).thenReturn(false);

            assertThatNoException().isThrownBy(() -> commentCreatedEventHandler.handle(testEvent));

            verify(postDomainService).incrementCommentCount(PostId.of("post123"));
        }
    }

    @Nested
    @DisplayName("시뮬레이션 실패 처리")
    class SimulatedFailureHandling {

        @Test
        @DisplayName("시뮬레이션 실패 활성화 시 보상 이벤트 발행")
        void handleEventWithSimulatedFailure() {
            try (MockedStatic<DomainEvents> mockedDomainEvents = mockStatic(DomainEvents.class)) {
                when(eventProperties.simulateFailure()).thenReturn(true);
                when(eventProperties.failureRate()).thenReturn(1.0);
                when(eventProperties.enableCompensation()).thenReturn(true);

                commentCreatedEventHandler.handle(testEvent);

                var eventCaptor = ArgumentCaptor.forClass(PostCompensationEvent.CommentCountIncreaseCompensation.class);
                mockedDomainEvents.verify(() -> DomainEvents.raiseCompensationEvent(eventCaptor.capture()));

                PostCompensationEvent.CommentCountIncreaseCompensation capturedEvent = eventCaptor.getValue();
                assertThat(capturedEvent.getPostId()).isEqualTo("post123");
                assertThat(capturedEvent.getTransactionId()).isNotNull();
            }
        }

        @Test
        @DisplayName("보상 비활성화 시 보상 이벤트 발행하지 않음")
        void handleEventWithCompensationDisabled() {
            try (MockedStatic<DomainEvents> mockedDomainEvents = mockStatic(DomainEvents.class)) {
                when(eventProperties.simulateFailure()).thenReturn(true);
                when(eventProperties.failureRate()).thenReturn(1.0);
                when(eventProperties.enableCompensation()).thenReturn(false);

                commentCreatedEventHandler.handle(testEvent);

                mockedDomainEvents.verify(() -> DomainEvents.raiseCompensationEvent(any()), never());
            }
        }

        @Test
        @DisplayName("실패율 0일 때 정상 처리")
        void handleEventWithZeroFailureRate() {
            when(eventProperties.simulateFailure()).thenReturn(true);
            when(eventProperties.failureRate()).thenReturn(0.0);

            commentCreatedEventHandler.handle(testEvent);

            verify(postDomainService).incrementCommentCount(PostId.of("post123"));
        }
    }

    @Nested
    @DisplayName("실제 예외 처리")
    class ActualExceptionHandling {

        @Test
        @DisplayName("포스트 도메인 서비스 실패 시 보상 이벤트 발행")
        void handlePostDomainServiceException() {
            try (MockedStatic<DomainEvents> mockedDomainEvents = mockStatic(DomainEvents.class)) {
                PostException postException = new PostException(PostErrorCode.POST_NOT_FOUND);
                
                when(eventProperties.simulateFailure()).thenReturn(false);
                when(eventProperties.enableCompensation()).thenReturn(true);
                doThrow(postException).when(postDomainService).incrementCommentCount(PostId.of("post123"));

                commentCreatedEventHandler.handle(testEvent);

                ArgumentCaptor<PostCompensationEvent.CommentCountIncreaseCompensation> eventCaptor = 
                        ArgumentCaptor.forClass(PostCompensationEvent.CommentCountIncreaseCompensation.class);
                mockedDomainEvents.verify(() -> DomainEvents.raiseCompensationEvent(eventCaptor.capture()));

                PostCompensationEvent.CommentCountIncreaseCompensation capturedEvent = eventCaptor.getValue();
                assertThat(capturedEvent.getPostId()).isEqualTo("post123");
            }
        }

        @Test
        @DisplayName("런타임 예외 발생 시 보상 이벤트 발행")
        void handleRuntimeException() {
            try (MockedStatic<DomainEvents> mockedDomainEvents = mockStatic(DomainEvents.class)) {
                RuntimeException runtimeException = new RuntimeException("Unexpected error");
                
                when(eventProperties.simulateFailure()).thenReturn(false);
                when(eventProperties.enableCompensation()).thenReturn(true);
                doThrow(runtimeException).when(postDomainService).incrementCommentCount(PostId.of("post123"));

                commentCreatedEventHandler.handle(testEvent);

                mockedDomainEvents.verify(() -> DomainEvents.raiseCompensationEvent(any(PostCompensationEvent.CommentCountIncreaseCompensation.class)));
            }
        }

        @Test
        @DisplayName("예외 발생 시 보상 비활성화되어 있으면 보상 이벤트 발행하지 않음")
        void handleExceptionWithCompensationDisabled() {
            try (MockedStatic<DomainEvents> mockedDomainEvents = mockStatic(DomainEvents.class)) {
                PostException postException = new PostException(PostErrorCode.POST_NOT_FOUND);
                
                when(eventProperties.simulateFailure()).thenReturn(false);
                when(eventProperties.enableCompensation()).thenReturn(false);
                doThrow(postException).when(postDomainService).incrementCommentCount(PostId.of("post123"));

                commentCreatedEventHandler.handle(testEvent);

                mockedDomainEvents.verify(() -> DomainEvents.raiseCompensationEvent(any()), never());
            }
        }
    }

    @Nested
    @DisplayName("이벤트 타입 검증")
    class EventTypeValidation {

        @Test
        @DisplayName("올바른 이벤트 타입 반환")
        void getCorrectEventType() {
            Class<CommentCreatedEvent> eventType = commentCreatedEventHandler.getEventType();
            
            assertThat(eventType).isEqualTo(CommentCreatedEvent.class);
        }
    }

    @Nested
    @DisplayName("다양한 시나리오")
    class VariousScenarios {

        @Test
        @DisplayName("null 댓글 ID로 이벤트 처리")
        void handleEventWithNullCommentId() {
            CommentCreatedEvent nullCommentEvent = new CommentCreatedEvent(null, "post123", "user123");
            
            when(eventProperties.simulateFailure()).thenReturn(false);

            commentCreatedEventHandler.handle(nullCommentEvent);

            verify(postDomainService).incrementCommentCount(PostId.of("post123"));
        }

        @Test
        @DisplayName("여러 연속 이벤트 처리")
        void handleMultipleConsecutiveEvents() {
            when(eventProperties.simulateFailure()).thenReturn(false);

            for (int i = 0; i < 3; i++) {
                CommentCreatedEvent event = new CommentCreatedEvent("comment" + i, "post123", "user123");
                commentCreatedEventHandler.handle(event);
            }

            verify(postDomainService, times(3)).incrementCommentCount(PostId.of("post123"));
        }

        @Test
        @DisplayName("서로 다른 게시물 ID로 이벤트 처리")
        void handleEventForDifferentPostIds() {
            CommentCreatedEvent event1 = new CommentCreatedEvent("comment1", "post123", "user123");
            CommentCreatedEvent event2 = new CommentCreatedEvent("comment2", "post456", "user456");
            
            when(eventProperties.simulateFailure()).thenReturn(false);

            commentCreatedEventHandler.handle(event1);
            commentCreatedEventHandler.handle(event2);

            verify(postDomainService).incrementCommentCount(PostId.of("post123"));
            verify(postDomainService).incrementCommentCount(PostId.of("post456"));
        }
    }
}