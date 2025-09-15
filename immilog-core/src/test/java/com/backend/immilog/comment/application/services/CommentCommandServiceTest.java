package com.backend.immilog.comment.application.services;

import com.backend.immilog.comment.domain.model.Comment;
import com.backend.immilog.comment.domain.model.CommentRelation;
import com.backend.immilog.comment.domain.model.ReferenceType;
import com.backend.immilog.comment.domain.repositories.CommentRepository;
import com.backend.immilog.comment.exception.CommentErrorCode;
import com.backend.immilog.comment.exception.CommentException;
import com.backend.immilog.shared.enums.ContentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("CommentCommandService")
@ExtendWith(MockitoExtension.class)
class CommentCommandServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentCommandService commentCommandService;

    @Nested
    @DisplayName("댓글 생성")
    class CreateComment {

        @Test
        @DisplayName("댓글 생성 성공")
        void createCommentSuccess() {
            var comment = createTestComment();
            var expectedComment = comment.withId("comment123");

            when(commentRepository.save(any(Comment.class))).thenReturn(expectedComment);

            var result = commentCommandService.createComment(comment);

            assertThat(result.id()).isEqualTo("comment123");
            assertThat(result.userId()).isEqualTo(comment.userId());
            assertThat(result.content()).isEqualTo(comment.content());
            verify(commentRepository).save(comment);
        }

        @Test
        @DisplayName("댓글 저장 실패")
        void createCommentFailure() {
            var comment = createTestComment();

            when(commentRepository.save(any(Comment.class))).thenThrow(new RuntimeException("Database error"));

            assertThatThrownBy(() -> commentCommandService.createComment(comment))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database error");

            verify(commentRepository).save(comment);
        }
    }

    @Nested
    @DisplayName("댓글 수정")
    class UpdateComment {

        @Test
        @DisplayName("댓글 수정 성공")
        void updateCommentSuccess() {
            var commentId = "comment123";
            var newContent = "수정된 댓글 내용";
            var existingComment = createTestComment().withId(commentId);
            var updatedComment = existingComment.updateContent(newContent);

            when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
            when(commentRepository.save(any(Comment.class))).thenReturn(updatedComment);

            var result = commentCommandService.updateComment(commentId, newContent);

            assertThat(result.content()).isEqualTo(newContent);
            assertThat(result.updatedAt()).isNotNull();
            verify(commentRepository).findById(commentId);
            verify(commentRepository).save(any(Comment.class));
        }

        @Test
        @DisplayName("존재하지 않는 댓글 수정 시도")
        void updateNonExistentComment() {
            var commentId = "nonexistent";
            var newContent = "수정된 내용";

            when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> commentCommandService.updateComment(commentId, newContent))
                    .isInstanceOf(CommentException.class)
                    .hasFieldOrPropertyWithValue("errorCode", CommentErrorCode.COMMENT_NOT_FOUND);

            verify(commentRepository).findById(commentId);
            verify(commentRepository, never()).save(any(Comment.class));
        }

        @Test
        @DisplayName("댓글 수정 시 저장 실패")
        void updateCommentSaveFailure() {
            var commentId = "comment123";
            var newContent = "수정된 댓글 내용";
            var existingComment = createTestComment().withId(commentId);

            when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
            when(commentRepository.save(any(Comment.class))).thenThrow(new RuntimeException("Save failed"));

            assertThatThrownBy(() -> commentCommandService.updateComment(commentId, newContent))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Save failed");

            verify(commentRepository).findById(commentId);
            verify(commentRepository).save(any(Comment.class));
        }
    }

    @Nested
    @DisplayName("댓글 삭제")
    class DeleteComment {

        @Test
        @DisplayName("댓글 삭제 성공")
        void deleteCommentSuccess() {
            var commentId = "comment123";

            commentCommandService.deleteComment(commentId);

            verify(commentRepository).deleteById(commentId);
        }

        @Test
        @DisplayName("댓글 삭제 실패")
        void deleteCommentFailure() {
            var commentId = "comment123";

            doThrow(new RuntimeException("Delete failed")).when(commentRepository).deleteById(commentId);

            assertThatThrownBy(() -> commentCommandService.deleteComment(commentId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Delete failed");

            verify(commentRepository).deleteById(commentId);
        }

        @Test
        @DisplayName("null ID로 삭제 시도")
        void deleteCommentWithNullId() {
            String commentId = null;

            commentCommandService.deleteComment(commentId);

            verify(commentRepository).deleteById(commentId);
        }
    }

    private Comment createTestComment() {
        return Comment.builder()
                .userId("user123")
                .content("테스트 댓글")
                .commentRelation(CommentRelation.of("post456", null, ReferenceType.POST))
                .replyCount(0)
                .status(ContentStatus.NORMAL)
                .likeUsers(new ArrayList<>())
                .build();
    }
}