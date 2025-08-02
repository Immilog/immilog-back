package com.backend.immilog.comment.application.services;

import com.backend.immilog.comment.application.dto.CommentResult;
import com.backend.immilog.shared.enums.ContentStatus;
import com.backend.immilog.comment.domain.model.ReferenceType;
import com.backend.immilog.comment.domain.repositories.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CommentQueryServiceTest {

    private final CommentRepository mockCommentRepository = mock(CommentRepository.class);

    private CommentQueryService commentQueryService;

    @BeforeEach
    void setUp() {
        commentQueryService = new CommentQueryService(mockCommentRepository);
    }

    @Test
    @DisplayName("게시물별 댓글 조회 - 정상 케이스")
    void getCommentsSuccessfully() {
        //given
        String postId = "postId";
        List<CommentResult> expectedComments = List.of(
                createTestCommentResult("comment1"),
                createTestCommentResult("comment2")
        );
        
        when(mockCommentRepository.findCommentsByPostId(postId)).thenReturn(expectedComments);

        //when
        List<CommentResult> result = commentQueryService.getComments(postId);

        //then
        assertThat(result).isEqualTo(expectedComments);
        assertThat(result).hasSize(2);
        verify(mockCommentRepository).findCommentsByPostId(postId);
    }

    @Test
    @DisplayName("게시물별 댓글 조회 - 빈 결과")
    void getCommentsEmptyResult() {
        //given
        String postId = "postId";
        List<CommentResult> emptyComments = List.of();
        
        when(mockCommentRepository.findCommentsByPostId(postId)).thenReturn(emptyComments);

        //when
        List<CommentResult> result = commentQueryService.getComments(postId);

        //then
        assertThat(result).isEmpty();
        verify(mockCommentRepository).findCommentsByPostId(postId);
    }

    @Test
    @DisplayName("게시물별 댓글 조회 - null postId")
    void getCommentsWithNullPostId() {
        //given
        String postId = null;
        List<CommentResult> expectedComments = List.of();
        
        when(mockCommentRepository.findCommentsByPostId(postId)).thenReturn(expectedComments);

        //when
        List<CommentResult> result = commentQueryService.getComments(postId);

        //then
        assertThat(result).isEmpty();
        verify(mockCommentRepository).findCommentsByPostId(postId);
    }

    @Test
    @DisplayName("게시물별 댓글 조회 - 빈 문자열 postId")
    void getCommentsWithEmptyPostId() {
        //given
        String postId = "";
        List<CommentResult> expectedComments = List.of();
        
        when(mockCommentRepository.findCommentsByPostId(postId)).thenReturn(expectedComments);

        //when
        List<CommentResult> result = commentQueryService.getComments(postId);

        //then
        assertThat(result).isEmpty();
        verify(mockCommentRepository).findCommentsByPostId(postId);
    }

    @Test
    @DisplayName("대량의 댓글 조회")
    void getCommentsLargeResult() {
        //given
        String postId = "postId";
        List<CommentResult> largeCommentList = java.util.stream.IntStream.range(0, 100)
                .mapToObj(i -> createTestCommentResult("comment" + i))
                .toList();
        
        when(mockCommentRepository.findCommentsByPostId(postId)).thenReturn(largeCommentList);

        //when
        List<CommentResult> result = commentQueryService.getComments(postId);

        //then
        assertThat(result).hasSize(100);
        assertThat(result).isEqualTo(largeCommentList);
        verify(mockCommentRepository).findCommentsByPostId(postId);
    }

    @Test
    @DisplayName("단일 댓글 조회")
    void getSingleComment() {
        //given
        String postId = "postId";
        List<CommentResult> singleComment = List.of(createTestCommentResult("comment1"));
        
        when(mockCommentRepository.findCommentsByPostId(postId)).thenReturn(singleComment);

        //when
        List<CommentResult> result = commentQueryService.getComments(postId);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo("comment1");
        verify(mockCommentRepository).findCommentsByPostId(postId);
    }

    @Test
    @DisplayName("다양한 postId로 댓글 조회")
    void getCommentsWithDifferentPostIds() {
        //given
        String postId1 = "postId1";
        String postId2 = "postId2";
        String postId3 = "postId3";
        
        List<CommentResult> comments1 = List.of(createTestCommentResult("comment1"));
        List<CommentResult> comments2 = List.of(createTestCommentResult("comment2"), createTestCommentResult("comment3"));
        List<CommentResult> comments3 = List.of();
        
        when(mockCommentRepository.findCommentsByPostId(postId1)).thenReturn(comments1);
        when(mockCommentRepository.findCommentsByPostId(postId2)).thenReturn(comments2);
        when(mockCommentRepository.findCommentsByPostId(postId3)).thenReturn(comments3);

        //when
        List<CommentResult> result1 = commentQueryService.getComments(postId1);
        List<CommentResult> result2 = commentQueryService.getComments(postId2);
        List<CommentResult> result3 = commentQueryService.getComments(postId3);

        //then
        assertThat(result1).hasSize(1);
        assertThat(result2).hasSize(2);
        assertThat(result3).isEmpty();
        verify(mockCommentRepository).findCommentsByPostId(postId1);
        verify(mockCommentRepository).findCommentsByPostId(postId2);
        verify(mockCommentRepository).findCommentsByPostId(postId3);
    }

    @Test
    @DisplayName("댓글 조회 결과 내용 검증")
    void verifyCommentResultContent() {
        //given
        String postId = "postId";
        CommentResult expectedComment = createTestCommentResult("commentId");
        List<CommentResult> comments = List.of(expectedComment);
        
        when(mockCommentRepository.findCommentsByPostId(postId)).thenReturn(comments);

        //when
        List<CommentResult> result = commentQueryService.getComments(postId);

        //then
        assertThat(result).hasSize(1);
        CommentResult actualComment = result.get(0);
        assertThat(actualComment.id()).isEqualTo(expectedComment.id());
        assertThat(actualComment.userId()).isEqualTo(expectedComment.userId());
        assertThat(actualComment.content()).isEqualTo(expectedComment.content());
        assertThat(actualComment.postId()).isEqualTo(expectedComment.postId());
        verify(mockCommentRepository).findCommentsByPostId(postId);
    }

    @Test
    @DisplayName("댓글 조회 - 리포지토리 호출 횟수 검증")
    void verifyRepositoryCallCount() {
        //given
        String postId = "postId";
        List<CommentResult> comments = List.of(createTestCommentResult("comment1"));
        
        when(mockCommentRepository.findCommentsByPostId(postId)).thenReturn(comments);

        //when
        commentQueryService.getComments(postId);
        commentQueryService.getComments(postId);
        commentQueryService.getComments(postId);

        //then
        verify(mockCommentRepository, org.mockito.Mockito.times(3)).findCommentsByPostId(postId);
    }

    @Test
    @DisplayName("특수 문자가 포함된 postId로 댓글 조회")
    void getCommentsWithSpecialCharactersInPostId() {
        //given
        String postId = "post@#$%^&*()_+Id";
        List<CommentResult> comments = List.of(createTestCommentResult("comment1"));
        
        when(mockCommentRepository.findCommentsByPostId(postId)).thenReturn(comments);

        //when
        List<CommentResult> result = commentQueryService.getComments(postId);

        //then
        assertThat(result).hasSize(1);
        verify(mockCommentRepository).findCommentsByPostId(postId);
    }

    private CommentResult createTestCommentResult(String commentId) {
        return new CommentResult(
                commentId,
                "userId",
                "댓글 내용",
                "postId",
                null,
                ReferenceType.POST,
                0,
                0,
                ContentStatus.NORMAL,
                java.time.LocalDateTime.now(),
                null
        );
    }
}