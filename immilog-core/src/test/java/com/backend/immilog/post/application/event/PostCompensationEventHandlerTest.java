package com.backend.immilog.post.application.event;

import com.backend.immilog.post.application.services.PostCommandService;
import com.backend.immilog.post.application.services.PostQueryService;
import com.backend.immilog.post.domain.events.PostCompensationEvent;
import com.backend.immilog.post.domain.model.post.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostCompensationEventHandler 테스트")
class PostCompensationEventHandlerTest {

    @Mock
    private PostQueryService postQueryService;

    @Mock
    private PostCommandService postCommandService;

    @Mock
    private Post mockPost;

    @Mock
    private Post compensatedPost;

    private PostCompensationEventHandler compensationHandler;

    @BeforeEach
    void setUp() {
        compensationHandler = new PostCompensationEventHandler(postQueryService, postCommandService);
    }

    @Test
    @DisplayName("댓글 수 증가 보상 이벤트 처리 성공 - 댓글 수 감소")
    void handleCommentCountIncreaseCompensation_Success() {
        // given
        String transactionId = "tx-123";
        String originalEventId = "comment-456";
        String postId = "post-789";

        var event = new PostCompensationEvent.CommentCountIncreaseCompensation(transactionId, originalEventId, postId);

        when(postQueryService.getPostById(postId)).thenReturn(mockPost);
        when(mockPost.decreaseCommentCount()).thenReturn(compensatedPost);

        // when
        compensationHandler.handle(event);

        // then
        verify(postQueryService).getPostById(postId);
        verify(mockPost).decreaseCommentCount();
        verify(postCommandService).save(compensatedPost);
    }

    @Test
    @DisplayName("댓글 수 증가 보상 이벤트 처리 실패 - 로그 출력 후 계속 진행")
    void handleCommentCountIncreaseCompensation_Failure() {
        // given
        String transactionId = "tx-123";
        String originalEventId = "comment-456";
        String postId = "post-789";

        PostCompensationEvent.CommentCountIncreaseCompensation event =
                new PostCompensationEvent.CommentCountIncreaseCompensation(transactionId, originalEventId, postId);

        when(postQueryService.getPostById(postId)).thenThrow(new RuntimeException("Database connection failed"));

        compensationHandler.handle(event);

        // then
        verify(postQueryService).getPostById(postId);
        verify(mockPost, never()).decreaseCommentCount();
        verify(postCommandService, never()).save(any());
    }

    @Test
    @DisplayName("보상 이벤트 핸들러의 이벤트 타입 검증")
    void getEventType_ReturnsCorrectType() {
        // when
        Class<PostCompensationEvent.CommentCountIncreaseCompensation> eventType = compensationHandler.getEventType();

        // then
        assert eventType == PostCompensationEvent.CommentCountIncreaseCompensation.class;
    }

    @Test
    @DisplayName("게시글을 찾을 수 없는 경우 보상 처리")
    void handleCommentCountIncreaseCompensation_PostNotFound() {
        // given
        String transactionId = "tx-123";
        String originalEventId = "comment-456";
        String postId = "non-existent-post";

        var event = new PostCompensationEvent.CommentCountIncreaseCompensation(transactionId, originalEventId, postId);

        when(postQueryService.getPostById(postId)).thenThrow(new IllegalArgumentException("Post not found"));

        // when
        compensationHandler.handle(event);

        // then
        verify(postQueryService).getPostById(postId);
        verify(postCommandService, never()).save(any());
    }

    @Test
    @DisplayName("댓글 수 감소 실패 시 보상 처리")
    void handleCommentCountIncreaseCompensation_DecreaseCommentCountFails() {
        // given
        String transactionId = "tx-123";
        String originalEventId = "comment-456";
        String postId = "post-789";

        var event = new PostCompensationEvent.CommentCountIncreaseCompensation(transactionId, originalEventId, postId);

        when(postQueryService.getPostById(postId)).thenReturn(mockPost);
        when(mockPost.decreaseCommentCount()).thenThrow(new IllegalStateException("Comment count cannot be negative"));

        // when
        compensationHandler.handle(event);

        // then
        verify(postQueryService).getPostById(postId);
        verify(mockPost).decreaseCommentCount();
        verify(postCommandService, never()).save(any());
    }
}