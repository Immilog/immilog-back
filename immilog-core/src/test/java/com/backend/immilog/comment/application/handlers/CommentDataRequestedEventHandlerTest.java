package com.backend.immilog.comment.application.handlers;

import com.backend.immilog.comment.application.dto.CommentResult;
import com.backend.immilog.comment.application.services.CommentQueryService;
import com.backend.immilog.comment.domain.model.ReferenceType;
import com.backend.immilog.shared.domain.event.CommentDataRequestedEvent;
import com.backend.immilog.shared.domain.model.CommentData;
import com.backend.immilog.shared.enums.ContentStatus;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentDataRequestedEventHandler 테스트")
class CommentDataRequestedEventHandlerTest {

    @Mock
    private CommentQueryService commentQueryService;
    
    @Mock
    private EventResultStorageService eventResultStorageService;

    private CommentDataRequestedEventHandler commentDataRequestedEventHandler;

    @BeforeEach
    void setUp() {
        commentDataRequestedEventHandler = new CommentDataRequestedEventHandler(
            commentQueryService, eventResultStorageService);
    }

    @Test
    @DisplayName("댓글 데이터 요청 이벤트 처리 성공")
    void handleCommentDataRequestedEventSuccessfully() {
        // given
        String requestId = "comment-request-123";
        List<String> postIds = List.of("post1", "post2");
        String requestingDomain = "post";
        
        CommentDataRequestedEvent event = new CommentDataRequestedEvent(requestId, postIds, requestingDomain);
        
        List<CommentResult> commentsForPost1 = List.of(
            createMockCommentResult("comment1", "post1", "user1", "댓글1", 0),
            createMockCommentResult("comment2", "post1", "user2", "댓글2", 1)
        );
        
        List<CommentResult> commentsForPost2 = List.of(
            createMockCommentResult("comment3", "post2", "user3", "댓글3", 0)
        );
        
        when(commentQueryService.getCommentsByPostId("post1")).thenReturn(commentsForPost1);
        when(commentQueryService.getCommentsByPostId("post2")).thenReturn(commentsForPost2);

        // when
        commentDataRequestedEventHandler.handle(event);

        // then
        ArgumentCaptor<List<CommentData>> commentDataCaptor = ArgumentCaptor.forClass(List.class);
        verify(eventResultStorageService).storeCommentData(eq(requestId), commentDataCaptor.capture());
        
        List<CommentData> capturedCommentData = commentDataCaptor.getValue();
        assertThat(capturedCommentData).hasSize(3);
        
        // 첫 번째 댓글 검증
        CommentData comment1 = capturedCommentData.get(0);
        assertThat(comment1.commentId()).isEqualTo("comment1");
        assertThat(comment1.postId()).isEqualTo("post1");
        assertThat(comment1.userId()).isEqualTo("user1");
        assertThat(comment1.content()).isEqualTo("댓글1");
        assertThat(comment1.replyCount()).isEqualTo(0);
        assertThat(comment1.status()).isEqualTo("ACTIVE");
        
        // 두 번째 댓글 검증
        CommentData comment2 = capturedCommentData.get(1);
        assertThat(comment2.commentId()).isEqualTo("comment2");
        assertThat(comment2.replyCount()).isEqualTo(1);
        
        // 세 번째 댓글 검증
        CommentData comment3 = capturedCommentData.get(2);
        assertThat(comment3.commentId()).isEqualTo("comment3");
        assertThat(comment3.postId()).isEqualTo("post2");
        
        verify(commentQueryService).getCommentsByPostId("post1");
        verify(commentQueryService).getCommentsByPostId("post2");
    }

    @Test
    @DisplayName("댓글이 없는 게시물 처리")
    void handleCommentDataRequestedEventWithNoComments() {
        // given
        String requestId = "comment-request-empty";
        List<String> postIds = List.of("post1", "post2");
        String requestingDomain = "post";
        
        CommentDataRequestedEvent event = new CommentDataRequestedEvent(requestId, postIds, requestingDomain);
        
        when(commentQueryService.getCommentsByPostId("post1")).thenReturn(List.of());
        when(commentQueryService.getCommentsByPostId("post2")).thenReturn(List.of());

        // when
        commentDataRequestedEventHandler.handle(event);

        // then
        ArgumentCaptor<List<CommentData>> commentDataCaptor = ArgumentCaptor.forClass(List.class);
        verify(eventResultStorageService).storeCommentData(eq(requestId), commentDataCaptor.capture());
        
        List<CommentData> capturedCommentData = commentDataCaptor.getValue();
        assertThat(capturedCommentData).isEmpty();
    }

    @Test
    @DisplayName("댓글 조회 실패 시 로그만 출력하고 종료")
    void handleCommentDataRequestedEventWithFailure() {
        // given
        String requestId = "comment-request-fail";
        List<String> postIds = List.of("post1");
        String requestingDomain = "post";
        
        CommentDataRequestedEvent event = new CommentDataRequestedEvent(requestId, postIds, requestingDomain);
        
        when(commentQueryService.getCommentsByPostId("post1"))
            .thenThrow(new RuntimeException("Database connection error"));

        // when
        commentDataRequestedEventHandler.handle(event);

        // then - 예외가 던져지지 않고 정상 종료됨
        verify(commentQueryService).getCommentsByPostId("post1");
        
        // storeCommentData가 호출되지 않음을 확인
        verify(eventResultStorageService, never()).storeCommentData(any(), any());
    }

    @Test
    @DisplayName("빈 게시물 ID 목록 처리")
    void handleCommentDataRequestedEventWithEmptyPostIds() {
        // given
        String requestId = "comment-request-empty-posts";
        List<String> emptyPostIds = List.of();
        String requestingDomain = "post";
        
        CommentDataRequestedEvent event = new CommentDataRequestedEvent(requestId, emptyPostIds, requestingDomain);

        // when
        commentDataRequestedEventHandler.handle(event);

        // then
        ArgumentCaptor<List<CommentData>> commentDataCaptor = ArgumentCaptor.forClass(List.class);
        verify(eventResultStorageService).storeCommentData(eq(requestId), commentDataCaptor.capture());
        
        List<CommentData> capturedCommentData = commentDataCaptor.getValue();
        assertThat(capturedCommentData).isEmpty();
        
        verifyNoInteractions(commentQueryService);
    }

    @Test
    @DisplayName("혼합된 상황 처리 - 일부 성공, 일부 실패")
    void handleCommentDataRequestedEventWithMixedResults() {
        // given
        String requestId = "comment-request-mixed";
        List<String> postIds = List.of("post1", "post2");
        String requestingDomain = "post";
        
        CommentDataRequestedEvent event = new CommentDataRequestedEvent(requestId, postIds, requestingDomain);
        
        List<CommentResult> commentsForPost1 = List.of(
            createMockCommentResult("comment1", "post1", "user1", "댓글1", 0)
        );
        
        when(commentQueryService.getCommentsByPostId("post1")).thenReturn(commentsForPost1);
        when(commentQueryService.getCommentsByPostId("post2"))
            .thenThrow(new RuntimeException("Post2 error"));

        // when
        commentDataRequestedEventHandler.handle(event);

        // then - 전체가 실패로 처리됨 (현재 구현상 전역 try-catch)
        verify(commentQueryService).getCommentsByPostId("post1");
        verify(commentQueryService).getCommentsByPostId("post2");
        
        // 실패 시 storeCommentData가 호출되지 않음
        verify(eventResultStorageService, never()).storeCommentData(any(), any());
    }

    @Test
    @DisplayName("이벤트 타입 반환 확인")
    void getEventType() {
        // when
        Class<CommentDataRequestedEvent> eventType = commentDataRequestedEventHandler.getEventType();

        // then
        assertThat(eventType).isEqualTo(CommentDataRequestedEvent.class);
    }

    private CommentResult createMockCommentResult(String commentId, String postId, String userId, 
                                                String content, int replyCount) {
        return new CommentResult(
            commentId,
            userId,
            "닉네임",
            "http://profile.jpg",
            "KR",
            "Seoul",
            content,
            postId,
            null, // parentId
            ReferenceType.POST,
            replyCount,
            ContentStatus.ACTIVE,
            LocalDateTime.now(),
            null // updatedAt
        );
    }
}