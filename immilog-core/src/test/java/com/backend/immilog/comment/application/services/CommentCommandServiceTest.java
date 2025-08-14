package com.backend.immilog.comment.application.services;

import com.backend.immilog.comment.domain.model.Comment;
import com.backend.immilog.comment.domain.model.CommentRelation;
import com.backend.immilog.comment.domain.model.ReferenceType;
import com.backend.immilog.comment.domain.repositories.CommentRepository;
import com.backend.immilog.comment.exception.CommentException;
import com.backend.immilog.shared.enums.ContentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommentCommandServiceTest {

    private final CommentRepository mockCommentRepository = mock(CommentRepository.class);

    private CommentCommandService commentCommandService;

    @BeforeEach
    void setUp() {
        commentCommandService = new CommentCommandService(mockCommentRepository);
    }

    @Test
    @DisplayName("댓글 생성 - 정상 케이스")
    void createCommentSuccessfully() {
        //given
        Comment comment = createTestComment();
        Comment savedComment = createTestCommentWithId();
        
        when(mockCommentRepository.save(comment)).thenReturn(savedComment);

        //when
        Comment result = commentCommandService.createComment(comment);

        //then
        assertThat(result).isEqualTo(savedComment);
        verify(mockCommentRepository).save(comment);
    }

    @Test
    @DisplayName("댓글 업데이트 - 정상 케이스")
    void updateCommentSuccessfully() {
        //given
        String commentId = "commentId";
        String newContent = "수정된 댓글 내용";
        Comment existingComment = createTestCommentWithId();
        Comment updatedComment = existingComment.updateContent(newContent);
        
        when(mockCommentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(mockCommentRepository.save(any(Comment.class))).thenReturn(updatedComment);

        //when
        Comment result = commentCommandService.updateComment(commentId, newContent);

        //then
        assertThat(result.content()).isEqualTo(newContent);
        verify(mockCommentRepository).findById(commentId);
        verify(mockCommentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 업데이트 실패 - 존재하지 않는 댓글")
    void updateCommentFailWhenNotFound() {
        //given
        String commentId = "nonExistentCommentId";
        String newContent = "수정된 댓글 내용";
        
        when(mockCommentRepository.findById(commentId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> commentCommandService.updateComment(commentId, newContent))
                .isInstanceOf(CommentException.class);
        verify(mockCommentRepository).findById(commentId);
    }

    @Test
    @DisplayName("댓글 삭제 - 정상 케이스")
    void deleteCommentSuccessfully() {
        //given
        String commentId = "commentId";

        //when
        commentCommandService.deleteComment(commentId);

        //then
        verify(mockCommentRepository).deleteById(commentId);
    }

    @Test
    @DisplayName("null 댓글 생성")
    void createNullComment() {
        //given
        Comment comment = null;
        
        when(mockCommentRepository.save(comment)).thenReturn(null);

        //when
        Comment result = commentCommandService.createComment(comment);

        //then
        assertThat(result).isNull();
        verify(mockCommentRepository).save(comment);
    }

    @Test
    @DisplayName("빈 내용으로 댓글 업데이트")
    void updateCommentWithEmptyContent() {
        //given
        String commentId = "commentId";
        String newContent = "";
        Comment existingComment = createTestCommentWithId();
        Comment updatedComment = existingComment.updateContent(newContent);
        
        when(mockCommentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(mockCommentRepository.save(any(Comment.class))).thenReturn(updatedComment);

        //when
        Comment result = commentCommandService.updateComment(commentId, newContent);

        //then
        assertThat(result.content()).isEmpty();
        verify(mockCommentRepository).findById(commentId);
        verify(mockCommentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("null 내용으로 댓글 업데이트")
    void updateCommentWithNullContent() {
        //given
        String commentId = "commentId";
        String newContent = null;
        Comment existingComment = createTestCommentWithId();
        Comment updatedComment = existingComment.updateContent(newContent);
        
        when(mockCommentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(mockCommentRepository.save(any(Comment.class))).thenReturn(updatedComment);

        //when
        Comment result = commentCommandService.updateComment(commentId, newContent);

        //then
        assertThat(result.content()).isNull();
        verify(mockCommentRepository).findById(commentId);
        verify(mockCommentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("빈 문자열 ID로 댓글 삭제")
    void deleteCommentWithEmptyId() {
        //given
        String commentId = "";

        //when
        commentCommandService.deleteComment(commentId);

        //then
        verify(mockCommentRepository).deleteById(commentId);
    }

    @Test
    @DisplayName("null ID로 댓글 삭제")
    void deleteCommentWithNullId() {
        //given
        String commentId = null;

        //when
        commentCommandService.deleteComment(commentId);

        //then
        verify(mockCommentRepository).deleteById(commentId);
    }

    @Test
    @DisplayName("여러 댓글 연속 생성")
    void createMultipleCommentsSequentially() {
        //given
        Comment comment1 = createTestComment();
        Comment comment2 = createTestComment();
        Comment savedComment1 = createTestCommentWithId();
        Comment savedComment2 = createTestCommentWithId();
        
        when(mockCommentRepository.save(comment1)).thenReturn(savedComment1);
        when(mockCommentRepository.save(comment2)).thenReturn(savedComment2);

        //when
        Comment result1 = commentCommandService.createComment(comment1);
        Comment result2 = commentCommandService.createComment(comment2);

        //then
        assertThat(result1).isEqualTo(savedComment1);
        assertThat(result2).isEqualTo(savedComment2);
        verify(mockCommentRepository).save(comment1);
        verify(mockCommentRepository).save(comment2);
    }

    @Test
    @DisplayName("여러 댓글 연속 업데이트")
    void updateMultipleCommentsSequentially() {
        //given
        String commentId1 = "commentId1";
        String commentId2 = "commentId2";
        String newContent1 = "수정된 내용1";
        String newContent2 = "수정된 내용2";
        
        Comment existingComment1 = createTestCommentWithId();
        Comment existingComment2 = createTestCommentWithId();
        Comment updatedComment1 = existingComment1.updateContent(newContent1);
        Comment updatedComment2 = existingComment2.updateContent(newContent2);
        
        when(mockCommentRepository.findById(commentId1)).thenReturn(Optional.of(existingComment1));
        when(mockCommentRepository.findById(commentId2)).thenReturn(Optional.of(existingComment2));
        when(mockCommentRepository.save(any(Comment.class))).thenReturn(updatedComment1).thenReturn(updatedComment2);

        //when
        Comment result1 = commentCommandService.updateComment(commentId1, newContent1);
        Comment result2 = commentCommandService.updateComment(commentId2, newContent2);

        //then
        assertThat(result1.content()).isEqualTo(newContent1);
        assertThat(result2.content()).isEqualTo(newContent2);
        verify(mockCommentRepository).findById(commentId1);
        verify(mockCommentRepository).findById(commentId2);
        verify(mockCommentRepository, org.mockito.Mockito.times(2)).save(any(Comment.class));
    }

    @Test
    @DisplayName("생성, 업데이트, 삭제 혼합 작업")
    void mixedCreateUpdateDeleteOperations() {
        //given
        Comment commentToCreate = createTestComment();
        Comment savedComment = createTestCommentWithId();
        String commentIdToUpdate = "updateCommentId";
        String newContent = "수정된 내용";
        Comment existingComment = createTestCommentWithId();
        Comment updatedComment = existingComment.updateContent(newContent);
        String commentIdToDelete = "deleteCommentId";
        
        when(mockCommentRepository.save(any(Comment.class))).thenReturn(savedComment).thenReturn(updatedComment);
        when(mockCommentRepository.findById(commentIdToUpdate)).thenReturn(Optional.of(existingComment));

        //when
        Comment createResult = commentCommandService.createComment(commentToCreate);
        Comment updateResult = commentCommandService.updateComment(commentIdToUpdate, newContent);
        commentCommandService.deleteComment(commentIdToDelete);

        //then
        assertThat(createResult).isEqualTo(savedComment);
        assertThat(updateResult.content()).isEqualTo(newContent);
        verify(mockCommentRepository, org.mockito.Mockito.times(2)).save(any(Comment.class));
        verify(mockCommentRepository).findById(commentIdToUpdate);
        verify(mockCommentRepository).deleteById(commentIdToDelete);
    }

    private Comment createTestComment() {
        return Comment.of(
                "userId",
                "postId",
                "댓글 내용",
                "parentId",
                ReferenceType.POST
        );
    }

    private Comment createTestCommentWithId() {
        return new Comment(
                "commentId",
                "userId",
                "댓글 내용",
                CommentRelation.of("postId", null, ReferenceType.POST),
                0,
                ContentStatus.NORMAL,
                new ArrayList<>(),
                LocalDateTime.now(),
                null
        );
    }
}