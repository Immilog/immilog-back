package com.backend.immilog.post.application.services;

import com.backend.immilog.post.application.config.PostConfiguration;
import com.backend.immilog.post.domain.events.PostEvent;
import com.backend.immilog.shared.domain.model.CommentData;
import com.backend.immilog.shared.domain.model.InteractionData;
import com.backend.immilog.shared.domain.model.UserData;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("PostEventService")
class PostEventServiceTest {

    private final EventResultStorageService eventResultStorageService = mock(EventResultStorageService.class);
    private final PostConfiguration postConfiguration = mock(PostConfiguration.class);
    private final PostEventService postEventService = new PostEventService(eventResultStorageService, postConfiguration);

    private List<String> testUserIds;
    private List<String> testPostIds;
    private Duration testTimeout;

    @BeforeEach
    void setUp() {
        testUserIds = List.of("user1", "user2", "user3");
        testPostIds = List.of("post1", "post2", "post3");
        testTimeout = Duration.ofSeconds(2);
    }

    @Nested
    @DisplayName("사용자 데이터 요청")
    class UserDataRequest {

        @Test
        @DisplayName("사용자 데이터 요청 성공")
        void getUserDataSuccess() {
            String requestId = "user-req-123";
            List<UserData> userData = List.of(
                    new UserData("user1", "User One", "profile1.jpg", "KR", "Seoul"),
                    new UserData("user2", "User Two", "profile2.jpg", "US", "New York")
            );

            when(postConfiguration.getEventTimeout()).thenReturn(testTimeout);
            when(eventResultStorageService.generateRequestId("user")).thenReturn(requestId);
            when(eventResultStorageService.waitForUserData(requestId, testTimeout)).thenReturn(userData);

            List<UserData> result = postEventService.getUserData(testUserIds);

            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyElementsOf(userData);
            verify(eventResultStorageService).registerEventProcessing(requestId);
            verify(eventResultStorageService).waitForUserData(requestId, testTimeout);
        }

        @Test
        @DisplayName("빈 사용자 ID 리스트로 요청")
        void getUserDataWithEmptyList() {
            String requestId = "user-req-123";
            List<String> emptyUserIds = Collections.emptyList();
            List<UserData> emptyUserData = Collections.emptyList();

            when(postConfiguration.getEventTimeout()).thenReturn(testTimeout);
            when(eventResultStorageService.generateRequestId("user")).thenReturn(requestId);
            when(eventResultStorageService.waitForUserData(requestId, testTimeout)).thenReturn(emptyUserData);

            List<UserData> result = postEventService.getUserData(emptyUserIds);

            assertThat(result).isEmpty();
            verify(eventResultStorageService).registerEventProcessing(requestId);
        }

        @Test
        @DisplayName("사용자 데이터 직접 요청")
        void requestUserDataDirectly() {
            String requestId = "user-req-456";
            List<UserData> userData = List.of(new UserData("user1", "User One", "profile1.jpg", "KR", "Seoul"));

            when(postConfiguration.getEventTimeout()).thenReturn(testTimeout);
            when(eventResultStorageService.generateRequestId("user")).thenReturn(requestId);
            when(eventResultStorageService.waitForUserData(requestId, testTimeout)).thenReturn(userData);

            List<UserData> result = postEventService.requestUserData(testUserIds);

            assertThat(result).hasSize(1);
            verify(eventResultStorageService).registerEventProcessing(requestId);
        }
    }

    @Nested
    @DisplayName("인터랙션 데이터 요청")
    class InteractionDataRequest {

        @Test
        @DisplayName("인터랙션 데이터 요청 성공")
        void getInteractionDataSuccess() {
            String requestId = "interaction-req-123";
            String contentType = "POST";
            List<InteractionData> interactionData = List.of(
                    new InteractionData("interaction1", "post1", "user1", "ACTIVE", "LIKE", "POST"),
                    new InteractionData("interaction2", "post1", "user2", "ACTIVE", "BOOKMARK", "POST")
            );

            when(postConfiguration.getEventTimeout()).thenReturn(testTimeout);
            when(eventResultStorageService.generateRequestId("interaction")).thenReturn(requestId);
            when(eventResultStorageService.waitForInteractionData(requestId, testTimeout)).thenReturn(interactionData);

            List<InteractionData> result = postEventService.getInteractionData(testPostIds, contentType);

            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyElementsOf(interactionData);
            verify(eventResultStorageService).registerEventProcessing(requestId);
            verify(eventResultStorageService).waitForInteractionData(requestId, testTimeout);
        }

        @Test
        @DisplayName("인터랙션 데이터 직접 요청")
        void requestInteractionDataDirectly() {
            String requestId = "interaction-req-456";
            String contentType = "POST";
            List<InteractionData> interactionData = Collections.emptyList();

            when(postConfiguration.getEventTimeout()).thenReturn(testTimeout);
            when(eventResultStorageService.generateRequestId("interaction")).thenReturn(requestId);
            when(eventResultStorageService.waitForInteractionData(requestId, testTimeout)).thenReturn(interactionData);

            List<InteractionData> result = postEventService.requestInteractionData(testPostIds, contentType);

            assertThat(result).isEmpty();
            verify(eventResultStorageService).registerEventProcessing(requestId);
        }

        @Test
        @DisplayName("다양한 컨텐츠 타입으로 요청")
        void requestInteractionDataWithDifferentContentTypes() {
            String requestId = "interaction-req-789";
            String[] contentTypes = {"POST", "COMMENT"};

            when(postConfiguration.getEventTimeout()).thenReturn(testTimeout);
            when(eventResultStorageService.generateRequestId("interaction")).thenReturn(requestId);
            when(eventResultStorageService.waitForInteractionData(requestId, testTimeout)).thenReturn(Collections.emptyList());

            for (String contentType : contentTypes) {
                List<InteractionData> result = postEventService.getInteractionData(testPostIds, contentType);
                assertThat(result).isNotNull();
            }

            verify(eventResultStorageService, times(contentTypes.length)).registerEventProcessing(requestId);
        }
    }

    @Nested
    @DisplayName("댓글 데이터 요청")
    class CommentDataRequest {

        @Test
        @DisplayName("댓글 데이터 요청 성공")
        void getCommentDataSuccess() {
            String requestId = "comment-req-123";
            List<CommentData> commentData = List.of(
                    new CommentData("comment1", "post1", "user1", "Great post!", 0, "ACTIVE"),
                    new CommentData("comment2", "post1", "user2", "Thanks for sharing!", 1, "ACTIVE")
            );

            when(postConfiguration.getEventTimeout()).thenReturn(testTimeout);
            when(eventResultStorageService.generateRequestId("comment")).thenReturn(requestId);
            when(eventResultStorageService.waitForCommentData(requestId, testTimeout)).thenReturn(commentData);

            List<CommentData> result = postEventService.getCommentData(testPostIds);

            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyElementsOf(commentData);
            verify(eventResultStorageService).registerEventProcessing(requestId);
            verify(eventResultStorageService).waitForCommentData(requestId, testTimeout);
        }

        @Test
        @DisplayName("댓글 데이터 직접 요청")
        void requestCommentDataDirectly() {
            String requestId = "comment-req-456";
            List<CommentData> commentData = Collections.emptyList();

            when(postConfiguration.getEventTimeout()).thenReturn(testTimeout);
            when(eventResultStorageService.generateRequestId("comment")).thenReturn(requestId);
            when(eventResultStorageService.waitForCommentData(requestId, testTimeout)).thenReturn(commentData);

            List<CommentData> result = postEventService.requestCommentData(testPostIds);

            assertThat(result).isEmpty();
            verify(eventResultStorageService).registerEventProcessing(requestId);
        }

        @Test
        @DisplayName("빈 게시물 ID 리스트로 댓글 데이터 요청")
        void getCommentDataWithEmptyPostIds() {
            String requestId = "comment-req-789";
            List<String> emptyPostIds = Collections.emptyList();
            List<CommentData> emptyCommentData = Collections.emptyList();

            when(postConfiguration.getEventTimeout()).thenReturn(testTimeout);
            when(eventResultStorageService.generateRequestId("comment")).thenReturn(requestId);
            when(eventResultStorageService.waitForCommentData(requestId, testTimeout)).thenReturn(emptyCommentData);

            List<CommentData> result = postEventService.getCommentData(emptyPostIds);

            assertThat(result).isEmpty();
            verify(eventResultStorageService).registerEventProcessing(requestId);
        }
    }

    @Nested
    @DisplayName("북마크 데이터 요청")
    class BookmarkDataRequest {

        @Test
        @DisplayName("북마크 데이터 요청 성공")
        void getBookmarkDataSuccess() {
            String requestId = "bookmark-req-123";
            String userId = "user123";
            String contentType = "POST";
            List<String> bookmarkedPostIds = List.of("post1", "post3", "post5");

            when(postConfiguration.getEventTimeout()).thenReturn(testTimeout);
            when(eventResultStorageService.generateRequestId("bookmark")).thenReturn(requestId);
            when(eventResultStorageService.waitForBookmarkData(requestId, testTimeout)).thenReturn(bookmarkedPostIds);

            List<String> result = postEventService.getBookmarkData(userId, contentType);

            assertThat(result).hasSize(3);
            assertThat(result).containsExactlyElementsOf(bookmarkedPostIds);
            verify(eventResultStorageService).registerEventProcessing(requestId);
            verify(eventResultStorageService).waitForBookmarkData(requestId, testTimeout);
        }

        @Test
        @DisplayName("북마크 데이터 직접 요청")
        void requestBookmarkDataDirectly() {
            String requestId = "bookmark-req-456";
            String userId = "user123";
            String contentType = "POST";
            List<String> bookmarkedPostIds = Collections.emptyList();

            when(postConfiguration.getEventTimeout()).thenReturn(testTimeout);
            when(eventResultStorageService.generateRequestId("bookmark")).thenReturn(requestId);
            when(eventResultStorageService.waitForBookmarkData(requestId, testTimeout)).thenReturn(bookmarkedPostIds);

            List<String> result = postEventService.requestBookmarkData(userId, contentType);

            assertThat(result).isEmpty();
            verify(eventResultStorageService).registerEventProcessing(requestId);
        }

        @Test
        @DisplayName("다양한 컨텐츠 타입으로 북마크 요청")
        void requestBookmarkDataWithDifferentContentTypes() {
            String requestId = "bookmark-req-789";
            String userId = "user123";
            String[] contentTypes = {"POST", "COMMENT"};

            when(postConfiguration.getEventTimeout()).thenReturn(testTimeout);
            when(eventResultStorageService.generateRequestId("bookmark")).thenReturn(requestId);
            when(eventResultStorageService.waitForBookmarkData(requestId, testTimeout)).thenReturn(Collections.emptyList());

            for (String contentType : contentTypes) {
                List<String> result = postEventService.getBookmarkData(userId, contentType);
                assertThat(result).isNotNull();
            }

            verify(eventResultStorageService, times(contentTypes.length)).registerEventProcessing(requestId);
        }
    }

    @Nested
    @DisplayName("이벤트 발행 검증")
    class EventPublishingValidation {

        @Test
        @DisplayName("사용자 데이터 요청 이벤트 발행")
        void verifyUserDataRequestEvent() {
            try (MockedStatic<com.backend.immilog.shared.domain.event.DomainEvents> mockedDomainEvents = mockStatic(com.backend.immilog.shared.domain.event.DomainEvents.class)) {
                String requestId = "user-req-123";

                when(postConfiguration.getEventTimeout()).thenReturn(testTimeout);
                when(eventResultStorageService.generateRequestId("user")).thenReturn(requestId);
                when(eventResultStorageService.waitForUserData(requestId, testTimeout)).thenReturn(Collections.emptyList());

                postEventService.getUserData(testUserIds);

                ArgumentCaptor<PostEvent.UserDataRequested> eventCaptor = ArgumentCaptor.forClass(PostEvent.UserDataRequested.class);
                mockedDomainEvents.verify(() -> com.backend.immilog.shared.domain.event.DomainEvents.raise(eventCaptor.capture()));

                PostEvent.UserDataRequested capturedEvent = eventCaptor.getValue();
                assertThat(capturedEvent.getRequestId()).isEqualTo(requestId);
                assertThat(capturedEvent.getUserIds()).isEqualTo(testUserIds);
            }
        }

        @Test
        @DisplayName("인터랙션 데이터 요청 이벤트 발행")
        void verifyInteractionDataRequestEvent() {
            try (MockedStatic<com.backend.immilog.shared.domain.event.DomainEvents> mockedDomainEvents = mockStatic(com.backend.immilog.shared.domain.event.DomainEvents.class)) {
                String requestId = "interaction-req-123";
                String contentType = "POST";

                when(postConfiguration.getEventTimeout()).thenReturn(testTimeout);
                when(eventResultStorageService.generateRequestId("interaction")).thenReturn(requestId);
                when(eventResultStorageService.waitForInteractionData(requestId, testTimeout)).thenReturn(Collections.emptyList());

                postEventService.getInteractionData(testPostIds, contentType);

                ArgumentCaptor<PostEvent.InteractionDataRequested> eventCaptor = ArgumentCaptor.forClass(PostEvent.InteractionDataRequested.class);
                mockedDomainEvents.verify(() -> com.backend.immilog.shared.domain.event.DomainEvents.raise(eventCaptor.capture()));

                PostEvent.InteractionDataRequested capturedEvent = eventCaptor.getValue();
                assertThat(capturedEvent.getRequestId()).isEqualTo(requestId);
                assertThat(capturedEvent.getPostIds()).isEqualTo(testPostIds);
                assertThat(capturedEvent.getContentType()).isEqualTo(contentType);
            }
        }
    }

    @Nested
    @DisplayName("예외 처리")
    class ExceptionHandling {

        @Test
        @DisplayName("이벤트 스토리지 서비스 예외 전파")
        void eventStorageServiceExceptionPropagation() {
            String requestId = "user-req-123";
            RuntimeException storageException = new RuntimeException("Event storage error");

            when(postConfiguration.getEventTimeout()).thenReturn(testTimeout);
            when(eventResultStorageService.generateRequestId("user")).thenReturn(requestId);
            when(eventResultStorageService.waitForUserData(requestId, testTimeout)).thenThrow(storageException);

            assertThatThrownBy(() -> postEventService.getUserData(testUserIds))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Event storage error");

            verify(eventResultStorageService).registerEventProcessing(requestId);
        }

        @Test
        @DisplayName("타임아웃 예외 처리")
        void timeoutExceptionHandling() {
            String requestId = "interaction-req-123";
            String contentType = "POST";
            RuntimeException timeoutException = new RuntimeException("Timeout waiting for event");

            when(postConfiguration.getEventTimeout()).thenReturn(testTimeout);
            when(eventResultStorageService.generateRequestId("interaction")).thenReturn(requestId);
            when(eventResultStorageService.waitForInteractionData(requestId, testTimeout)).thenThrow(timeoutException);

            assertThatThrownBy(() -> postEventService.getInteractionData(testPostIds, contentType))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Timeout waiting for event");
        }
    }
}