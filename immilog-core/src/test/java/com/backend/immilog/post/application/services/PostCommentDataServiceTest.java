package com.backend.immilog.post.application.services;

import com.backend.immilog.shared.domain.event.CommentDataRequestedEvent;
import com.backend.immilog.shared.domain.event.DomainEventPublisher;
import com.backend.immilog.shared.domain.model.CommentData;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostCommentDataService 테스트")
class PostCommentDataServiceTest {

    @Mock
    private DomainEventPublisher eventPublisher;
    
    @Mock
    private EventResultStorageService eventResultStorageService;

    private PostCommentDataService postCommentDataService;

    @BeforeEach
    void setUp() {
        postCommentDataService = new PostCommentDataService(eventPublisher, eventResultStorageService);
    }

    @Test
    @DisplayName("댓글 데이터 조회 성공")
    void getCommentDataSuccessfully() {
        // given
        List<String> postIds = List.of("post1", "post2");
        String requestId = "request-123";
        List<CommentData> expectedCommentData = List.of(
            new CommentData("comment1", "post1", "user1", "댓글 내용1", 0, "ACTIVE"),
            new CommentData("comment2", "post2", "user2", "댓글 내용2", 1, "ACTIVE")
        );

        when(eventResultStorageService.generateRequestId("comment")).thenReturn(requestId);
        when(eventResultStorageService.waitForCommentData(requestId, Duration.ofSeconds(3)))
            .thenReturn(expectedCommentData);

        // when
        List<CommentData> result = postCommentDataService.getCommentData(postIds);

        // then
        assertThat(result).isEqualTo(expectedCommentData);
        
        ArgumentCaptor<CommentDataRequestedEvent> eventCaptor = ArgumentCaptor.forClass(CommentDataRequestedEvent.class);
        verify(eventPublisher).publishDomainEvent(eventCaptor.capture());
        
        CommentDataRequestedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getRequestId()).isEqualTo(requestId);
        assertThat(capturedEvent.getPostIds()).isEqualTo(postIds);
        assertThat(capturedEvent.getRequestingDomain()).isEqualTo("post");
        
        verify(eventResultStorageService).registerEventProcessing(requestId);
        verify(eventResultStorageService).waitForCommentData(requestId, Duration.ofSeconds(3));
    }

    @Test
    @DisplayName("댓글 데이터 조회 비동기 성공")
    void getCommentDataAsyncSuccessfully() {
        // given
        List<String> postIds = List.of("post1");
        String requestId = "async-request-123";
        List<CommentData> expectedCommentData = List.of(
            new CommentData("comment1", "post1", "user1", "댓글 내용", 0, "ACTIVE")
        );

        when(eventResultStorageService.generateRequestId("comment")).thenReturn(requestId);
        when(eventResultStorageService.waitForCommentData(requestId, Duration.ofSeconds(3)))
            .thenReturn(expectedCommentData);

        // when
        CompletableFuture<List<CommentData>> resultFuture = 
            postCommentDataService.getCommentDataAsync(postIds);

        // then
        assertThat(resultFuture).isNotNull();
        assertThat(resultFuture.join()).isEqualTo(expectedCommentData);
        
        verify(eventPublisher).publishDomainEvent(any(CommentDataRequestedEvent.class));
    }

    @Test
    @DisplayName("이벤트 타임아웃 시 빈 리스트 반환")
    void getCommentDataTimeoutReturnsEmptyList() {
        // given
        List<String> postIds = List.of("post1");
        String requestId = "timeout-request-123";

        when(eventResultStorageService.generateRequestId("comment")).thenReturn(requestId);
        when(eventResultStorageService.waitForCommentData(requestId, Duration.ofSeconds(3)))
            .thenThrow(new RuntimeException("Event processing timeout"));

        // when
        List<CommentData> result = postCommentDataService.getCommentData(postIds);

        // then
        assertThat(result).isEmpty();
        
        verify(eventPublisher).publishDomainEvent(any(CommentDataRequestedEvent.class));
        verify(eventResultStorageService).registerEventProcessing(requestId);
    }

    @Test
    @DisplayName("빈 postIds 목록으로 조회 시 빈 리스트 반환")
    void getCommentDataWithEmptyPostIds() {
        // given
        List<String> emptyPostIds = List.of();

        // when
        List<CommentData> result = postCommentDataService.getCommentData(emptyPostIds);

        // then
        assertThat(result).isEmpty();
        
        verifyNoInteractions(eventPublisher, eventResultStorageService);
    }

    @Test
    @DisplayName("null postIds로 조회 시 NullPointerException")
    void getCommentDataWithNullPostIds() {
        // when & then
        assertThatThrownBy(() -> postCommentDataService.getCommentData(null))
            .isInstanceOf(NullPointerException.class);
    }
}