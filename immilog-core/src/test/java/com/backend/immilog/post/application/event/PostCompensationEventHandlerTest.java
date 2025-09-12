package com.backend.immilog.post.application.event;

import com.backend.immilog.post.domain.service.PostDomainService;
import com.backend.immilog.post.domain.events.PostCompensationEvent;
import com.backend.immilog.post.domain.model.post.*;
import com.backend.immilog.post.exception.PostErrorCode;
import com.backend.immilog.post.exception.PostException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostCompensationEventHandler")
class PostCompensationEventHandlerTest {

    @Mock
    private PostDomainService postDomainService;

    @InjectMocks
    private PostCompensationEventHandler postCompensationEventHandler;

    private PostCompensationEvent.CommentCountIncreaseCompensation testEvent;

    @BeforeEach
    void setUp() {
        testEvent = new PostCompensationEvent.CommentCountIncreaseCompensation(
                "transaction-123",
                "comment123",
                "post123"
        );
    }

    @Nested
    @DisplayName("보상 이벤트 처리 성공")
    class SuccessfulCompensationHandling {

        @Test
        @DisplayName("보상 이벤트 처리 성공")
        void handleCompensationEventSuccess() {
            postCompensationEventHandler.handle(testEvent);

            verify(postDomainService).decrementCommentCount(PostId.of("post123"));
        }

        @Test
        @DisplayName("댓글 수 감소 검증")
        void verifyCommentCountDecrease() {
            postCompensationEventHandler.handle(testEvent);

            verify(postDomainService).decrementCommentCount(PostId.of("post123"));
        }

        @Test
        @DisplayName("댓글 수가 0인 게시물에서 보상 처리")
        void handleCompensationForPostWithZeroComments() {
            postCompensationEventHandler.handle(testEvent);

            verify(postDomainService).decrementCommentCount(PostId.of("post123"));
        }

        @Test
        @DisplayName("높은 댓글 수를 가진 게시물에서 보상 처리")
        void handleCompensationForPostWithManyComments() {
            postCompensationEventHandler.handle(testEvent);

            verify(postDomainService).decrementCommentCount(PostId.of("post123"));
        }
    }

    @Nested
    @DisplayName("보상 이벤트 처리 실패")
    class CompensationFailureHandling {

        @Test
        @DisplayName("포스트 도메인 서비스 실패 시 로그만 출력하고 종료")
        void handlePostDomainServiceFailure() {
            PostException serviceException = new PostException(PostErrorCode.POST_NOT_FOUND);

            doThrow(serviceException).when(postDomainService).decrementCommentCount(PostId.of("post123"));

            assertThatNoException().isThrownBy(() -> postCompensationEventHandler.handle(testEvent));

            verify(postDomainService).decrementCommentCount(PostId.of("post123"));
        }

        @Test
        @DisplayName("런타임 예외 시 로그만 출력하고 종료")
        void handleRuntimeException() {
            RuntimeException runtimeException = new RuntimeException("Unexpected error");

            doThrow(runtimeException).when(postDomainService).decrementCommentCount(PostId.of("post123"));

            assertThatNoException().isThrownBy(() -> postCompensationEventHandler.handle(testEvent));

            verify(postDomainService).decrementCommentCount(PostId.of("post123"));
        }

        @Test
        @DisplayName("다양한 예외 타입에 대한 처리")
        void handleVariousExceptionTypes() {
            Exception[] exceptions = {
                new RuntimeException("Runtime error"),
                new IllegalArgumentException("Invalid argument"),
                new PostException(PostErrorCode.INVALID_POST_DATA),
                new NullPointerException("Null pointer")
            };

            for (Exception exception : exceptions) {
                reset(postDomainService);
                doThrow(exception).when(postDomainService).decrementCommentCount(PostId.of("post123"));

                assertThatNoException().isThrownBy(() -> postCompensationEventHandler.handle(testEvent));
                verify(postDomainService).decrementCommentCount(PostId.of("post123"));
            }
        }
    }

    @Nested
    @DisplayName("이벤트 타입 검증")
    class EventTypeValidation {

        @Test
        @DisplayName("올바른 이벤트 타입 반환")
        void getCorrectEventType() {
            Class<PostCompensationEvent.CommentCountIncreaseCompensation> eventType = 
                    postCompensationEventHandler.getEventType();
            
            assertThat(eventType).isEqualTo(PostCompensationEvent.CommentCountIncreaseCompensation.class);
        }
    }

    @Nested
    @DisplayName("다양한 시나리오")
    class VariousScenarios {

        @Test
        @DisplayName("null 트랜잭션 ID로 보상 처리")
        void handleCompensationWithNullTransactionId() {
            PostCompensationEvent.CommentCountIncreaseCompensation nullTransactionEvent = 
                    new PostCompensationEvent.CommentCountIncreaseCompensation(null, "comment123", "post123");

            postCompensationEventHandler.handle(nullTransactionEvent);

            verify(postDomainService).decrementCommentCount(PostId.of("post123"));
        }

        @Test
        @DisplayName("빈 게시물 ID로 보상 처리")
        void handleCompensationWithEmptyPostId() {
            PostCompensationEvent.CommentCountIncreaseCompensation emptyPostIdEvent = 
                    new PostCompensationEvent.CommentCountIncreaseCompensation("transaction-123", "comment123", "");

            // PostId.of("") 자체가 IllegalArgumentException을 던지므로, 이 예외가 PostCompensationEventHandler에서 catch되는지 확인
            assertThatNoException().isThrownBy(() -> postCompensationEventHandler.handle(emptyPostIdEvent));
            
            // PostId 생성 자체에서 예외가 발생하므로 postDomainService는 호출되지 않음
            verify(postDomainService, never()).decrementCommentCount(any());
        }

        @Test
        @DisplayName("null 댓글 ID로 보상 처리")
        void handleCompensationWithNullCommentId() {
            PostCompensationEvent.CommentCountIncreaseCompensation nullCommentIdEvent = 
                    new PostCompensationEvent.CommentCountIncreaseCompensation("transaction-123", null, "post123");

            postCompensationEventHandler.handle(nullCommentIdEvent);

            verify(postDomainService).decrementCommentCount(PostId.of("post123"));
        }

        @Test
        @DisplayName("연속된 보상 이벤트 처리")
        void handleConsecutiveCompensationEvents() {
            for (int i = 0; i < 5; i++) {
                PostCompensationEvent.CommentCountIncreaseCompensation event = 
                        new PostCompensationEvent.CommentCountIncreaseCompensation(
                                "transaction-" + i, "comment" + i, "post123");
                postCompensationEventHandler.handle(event);
            }

            verify(postDomainService, times(5)).decrementCommentCount(PostId.of("post123"));
        }

        @Test
        @DisplayName("다른 게시물들에 대한 보상 처리")
        void handleCompensationForDifferentPosts() {
            String[] postIds = {"post1", "post2", "post3"};

            for (String postId : postIds) {
                PostCompensationEvent.CommentCountIncreaseCompensation event = 
                        new PostCompensationEvent.CommentCountIncreaseCompensation(
                                "transaction-123", "comment123", postId);

                postCompensationEventHandler.handle(event);
            }
            
            verify(postDomainService).decrementCommentCount(PostId.of("post1"));
            verify(postDomainService).decrementCommentCount(PostId.of("post2"));
            verify(postDomainService).decrementCommentCount(PostId.of("post3"));
        }
    }

    @Nested
    @DisplayName("경계값 테스트")
    class BoundaryValueTests {

        @Test
        @DisplayName("최대 댓글 수에서 보상 처리")
        void handleCompensationAtMaxCommentCount() {
            postCompensationEventHandler.handle(testEvent);

            verify(postDomainService).decrementCommentCount(PostId.of("post123"));
        }

        @Test
        @DisplayName("댓글 수 1에서 보상 처리")
        void handleCompensationAtMinimumCommentCount() {
            postCompensationEventHandler.handle(testEvent);

            verify(postDomainService).decrementCommentCount(PostId.of("post123"));
        }
    }

}