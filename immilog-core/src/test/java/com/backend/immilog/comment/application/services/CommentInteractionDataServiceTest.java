package com.backend.immilog.comment.application.services;

import com.backend.immilog.shared.domain.event.DomainEventPublisher;
import com.backend.immilog.shared.domain.event.InteractionDataRequestedEvent;
import com.backend.immilog.shared.domain.model.InteractionData;
import com.backend.immilog.shared.enums.ContentType;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentInteractionDataService 테스트")
class CommentInteractionDataServiceTest {

    @Mock
    private DomainEventPublisher eventPublisher;
    
    @Mock
    private EventResultStorageService eventResultStorageService;

    private CommentInteractionDataService commentInteractionDataService;

    @BeforeEach
    void setUp() {
        commentInteractionDataService = new CommentInteractionDataService(eventPublisher, eventResultStorageService);
    }

    @Test
    @DisplayName("댓글 인터랙션 데이터 조회 성공")
    void getCommentInteractionDataSuccessfully() {
        // given
        List<String> commentIds = List.of("comment1", "comment2");
        ContentType contentType = ContentType.COMMENT;
        String requestId = "comment-interaction-123";
        
        List<InteractionData> expectedInteractionData = List.of(
            new InteractionData("interaction1", "comment1", "user1", "ACTIVE", "LIKE", "COMMENT"),
            new InteractionData("interaction2", "comment2", "user2", "ACTIVE", "BOOKMARK", "COMMENT")
        );

        when(eventResultStorageService.generateRequestId("interaction")).thenReturn(requestId);
        when(eventResultStorageService.waitForInteractionData(requestId, Duration.ofSeconds(3)))
            .thenReturn(expectedInteractionData);

        // when
        List<InteractionData> result = commentInteractionDataService.getInteractionData(commentIds, contentType);

        // then
        assertThat(result).isEqualTo(expectedInteractionData);
        
        ArgumentCaptor<InteractionDataRequestedEvent> eventCaptor = 
            ArgumentCaptor.forClass(InteractionDataRequestedEvent.class);
        verify(eventPublisher).publishDomainEvent(eventCaptor.capture());
        
        InteractionDataRequestedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getRequestId()).isEqualTo(requestId);
        assertThat(capturedEvent.getPostIds()).isEqualTo(commentIds); // postIds field는 범용적으로 사용됨
        assertThat(capturedEvent.getContentType()).isEqualTo(contentType.name());
        assertThat(capturedEvent.getRequestingDomain()).isEqualTo("comment");
        
        verify(eventResultStorageService).registerEventProcessing(requestId);
    }

    @Test
    @DisplayName("댓글 인터랙션 데이터 비동기 조회 성공")
    void getCommentInteractionDataAsyncSuccessfully() {
        // given
        List<String> commentIds = List.of("comment1");
        ContentType contentType = ContentType.COMMENT;
        String requestId = "async-comment-interaction-123";
        
        List<InteractionData> expectedData = List.of(
            new InteractionData("interaction1", "comment1", "user1", "ACTIVE", "LIKE", "COMMENT")
        );

        when(eventResultStorageService.generateRequestId("interaction")).thenReturn(requestId);
        when(eventResultStorageService.waitForInteractionData(requestId, Duration.ofSeconds(3)))
            .thenReturn(expectedData);

        // when
        CompletableFuture<List<InteractionData>> resultFuture = 
            commentInteractionDataService.getInteractionDataAsync(commentIds, contentType);

        // then
        assertThat(resultFuture).isNotNull();
        assertThat(resultFuture.join()).isEqualTo(expectedData);
        
        verify(eventPublisher).publishDomainEvent(any(InteractionDataRequestedEvent.class));
    }

    @Test
    @DisplayName("이벤트 타임아웃 시 빈 리스트 반환")
    void getCommentInteractionDataTimeoutReturnsEmptyList() {
        // given
        List<String> commentIds = List.of("comment1");
        ContentType contentType = ContentType.COMMENT;
        String requestId = "timeout-comment-request-123";

        when(eventResultStorageService.generateRequestId("interaction")).thenReturn(requestId);
        when(eventResultStorageService.waitForInteractionData(requestId, Duration.ofSeconds(3)))
            .thenThrow(new RuntimeException("Event processing timeout"));

        // when
        List<InteractionData> result = commentInteractionDataService.getInteractionData(commentIds, contentType);

        // then
        assertThat(result).isEmpty();
        
        verify(eventPublisher).publishDomainEvent(any(InteractionDataRequestedEvent.class));
        verify(eventResultStorageService).registerEventProcessing(requestId);
    }

    @Test
    @DisplayName("댓글별 좋아요 수 계산 성공")
    void getLikeCountsByCommentIdsSuccessfully() {
        // given
        List<String> commentIds = List.of("comment1", "comment2");
        List<InteractionData> interactionData = List.of(
            new InteractionData("int1", "comment1", "user1", "ACTIVE", "LIKE", "COMMENT"),
            new InteractionData("int2", "comment1", "user2", "ACTIVE", "LIKE", "COMMENT"),
            new InteractionData("int3", "comment2", "user3", "ACTIVE", "LIKE", "COMMENT"),
            new InteractionData("int4", "comment2", "user4", "ACTIVE", "BOOKMARK", "COMMENT")
        );

        when(eventResultStorageService.generateRequestId("interaction")).thenReturn("request-123");
        when(eventResultStorageService.waitForInteractionData(any(), any()))
            .thenReturn(interactionData);

        // when
        var result = commentInteractionDataService.getLikeCountsByCommentIds(commentIds);

        // then
        assertThat(result).containsEntry("comment1", 2L);
        assertThat(result).containsEntry("comment2", 1L);
    }

    @Test
    @DisplayName("댓글별 좋아요 사용자 목록 조회 성공")
    void getLikeUsersByCommentIdsSuccessfully() {
        // given
        List<String> commentIds = List.of("comment1");
        List<InteractionData> interactionData = List.of(
            new InteractionData("int1", "comment1", "user1", "ACTIVE", "LIKE", "COMMENT"),
            new InteractionData("int2", "comment1", "user2", "ACTIVE", "LIKE", "COMMENT"),
            new InteractionData("int3", "comment1", "user3", "ACTIVE", "BOOKMARK", "COMMENT")
        );

        when(eventResultStorageService.generateRequestId("interaction")).thenReturn("request-123");
        when(eventResultStorageService.waitForInteractionData(any(), any()))
            .thenReturn(interactionData);

        // when
        var result = commentInteractionDataService.getLikeUsersByCommentIds(commentIds);

        // then
        assertThat(result).containsEntry("comment1", List.of("user1", "user2"));
    }

    @Test
    @DisplayName("빈 댓글 ID 목록으로 조회 시 빈 결과 반환")
    void getInteractionDataWithEmptyCommentIds() {
        // given
        List<String> emptyCommentIds = List.of();
        ContentType contentType = ContentType.COMMENT;

        // when
        List<InteractionData> result = commentInteractionDataService.getInteractionData(emptyCommentIds, contentType);

        // then
        assertThat(result).isEmpty();
        
        verifyNoInteractions(eventPublisher, eventResultStorageService);
    }

    @Test
    @DisplayName("null 댓글 ID로 조회 시 NullPointerException")
    void getInteractionDataWithNullCommentIds() {
        // given
        ContentType contentType = ContentType.COMMENT;

        // when & then
        assertThatThrownBy(() -> commentInteractionDataService.getInteractionData(null, contentType))
            .isInstanceOf(NullPointerException.class);
    }
}