package com.backend.immilog.comment.application.usecase;

import com.backend.immilog.comment.application.dto.CommentCreateCommand;
import com.backend.immilog.comment.application.dto.CommentResult;
import com.backend.immilog.comment.application.services.CommentCommandService;
import com.backend.immilog.comment.application.services.CommentQueryService;
import com.backend.immilog.comment.domain.model.Comment;
import com.backend.immilog.comment.domain.model.CommentRelation;
import com.backend.immilog.comment.domain.model.ReferenceType;
import com.backend.immilog.shared.application.event.DomainEventPublisher;
import com.backend.immilog.shared.enums.ContentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommentCreateUseCaseTest {

    private final CommentCommandService mockCommentCommandService = mock(CommentCommandService.class);
    private final CommentQueryService mockCommentQueryService = mock(CommentQueryService.class);
    private final DomainEventPublisher mockDomainEventPublisher = mock(DomainEventPublisher.class);

    private CommentCreateUseCase.CommentCreator commentCreator;

    @BeforeEach
    void setUp() {
        commentCreator = new CommentCreateUseCase.CommentCreator(
                mockCommentCommandService,
                mockCommentQueryService,
                mockDomainEventPublisher
        );
    }

    @Test
    @DisplayName("댓글 생성 - 정상 케이스")
    void createCommentSuccessfully() {
        //given
        CommentCreateCommand command = new CommentCreateCommand(
                "userId",
                "postId",
                "댓글 내용",
                "parentId",
                ReferenceType.POST
        );
        Comment savedComment = createTestCommentWithId();
        CommentResult expectedResult = createCommentResultFromCommand(command);
        
        when(mockCommentCommandService.createComment(any(Comment.class))).thenReturn(savedComment);
        when(mockCommentQueryService.getCommentByCommentId(savedComment.id())).thenReturn(expectedResult);

        //when
        CommentResult result = commentCreator.createComment(command);

        //then
        assertThat(result.id()).isEqualTo(expectedResult.id());
        assertThat(result.userId()).isEqualTo(command.userId());
        assertThat(result.content()).isEqualTo(command.content());
        assertThat(result.postId()).isEqualTo(command.postId());
        assertThat(result.referenceType()).isEqualTo(command.referenceType());
        verify(mockCommentCommandService).createComment(any(Comment.class));
        verify(mockCommentQueryService).getCommentByCommentId(savedComment.id());
        verify(mockDomainEventPublisher).publishEvents();
    }

    @Test
    @DisplayName("댓글 생성 - POST 타입")
    void createCommentWithPostType() {
        //given
        CommentCreateCommand command = new CommentCreateCommand(
                "userId",
                "postId",
                "게시물 댓글",
                "parentId",
                ReferenceType.POST
        );
        Comment savedComment = new Comment(
                "commentId",
                "userId",
                "게시물 댓글",
                CommentRelation.of("postId", null, ReferenceType.POST),
                0,
                ContentStatus.NORMAL,
                new ArrayList<>(),
                LocalDateTime.now(),
                null
        );
        CommentResult expectedResult = createCommentResultFromCommand(command);
        
        when(mockCommentCommandService.createComment(any(Comment.class))).thenReturn(savedComment);
        when(mockCommentQueryService.getCommentByCommentId(savedComment.id())).thenReturn(expectedResult);

        //when
        CommentResult result = commentCreator.createComment(command);

        //then
        assertThat(result.referenceType()).isEqualTo(ReferenceType.POST);
        assertThat(result.content()).isEqualTo("게시물 댓글");
        verify(mockCommentCommandService).createComment(any(Comment.class));
        verify(mockCommentQueryService).getCommentByCommentId(savedComment.id());
        verify(mockDomainEventPublisher).publishEvents();
    }

    @Test
    @DisplayName("댓글 생성 - COMMENT 타입 (대댓글)")
    void createCommentWithCommentType() {
        //given
        CommentCreateCommand command = new CommentCreateCommand(
                "userId",
                "postId",
                "대댓글 내용",
                "parentId",
                ReferenceType.COMMENT
        );
        Comment savedComment = createTestCommentWithCommentType();
        CommentResult expectedResult = createCommentResultFromCommand(command);
        
        when(mockCommentCommandService.createComment(any(Comment.class))).thenReturn(savedComment);
        when(mockCommentQueryService.getCommentByCommentId(savedComment.id())).thenReturn(expectedResult);

        //when
        CommentResult result = commentCreator.createComment(command);

        //then
        assertThat(result.referenceType()).isEqualTo(ReferenceType.COMMENT);
        assertThat(result.content()).isEqualTo("대댓글 내용");
        verify(mockCommentCommandService).createComment(any(Comment.class));
        verify(mockCommentQueryService).getCommentByCommentId(savedComment.id());
        verify(mockDomainEventPublisher).publishEvents();
    }


    @Test
    @DisplayName("댓글 생성 - 빈 내용")
    void createCommentWithEmptyContent() {
        //given
        CommentCreateCommand command = new CommentCreateCommand(
                "userId",
                "postId",
                "",
                "parentId",
                ReferenceType.POST
        );
        Comment savedComment = createTestCommentWithEmptyContent();
        CommentResult expectedResult = createCommentResultFromCommand(command);
        
        when(mockCommentCommandService.createComment(any(Comment.class))).thenReturn(savedComment);
        when(mockCommentQueryService.getCommentByCommentId(savedComment.id())).thenReturn(expectedResult);

        //when
        CommentResult result = commentCreator.createComment(command);

        //then
        assertThat(result.content()).isEmpty();
        assertThat(result.userId()).isEqualTo(command.userId());
        assertThat(result.postId()).isEqualTo(command.postId());
        verify(mockCommentCommandService).createComment(any(Comment.class));
        verify(mockCommentQueryService).getCommentByCommentId(savedComment.id());
        verify(mockDomainEventPublisher).publishEvents();
    }

    @Test
    @DisplayName("댓글 생성 - null 내용")
    void createCommentWithNullContent() {
        //given
        CommentCreateCommand command = new CommentCreateCommand(
                "userId",
                "postId",
                null,
                "parentId",
                ReferenceType.POST
        );
        Comment savedComment = createTestCommentWithNullContent();
        CommentResult expectedResult = createCommentResultFromCommand(command);
        
        when(mockCommentCommandService.createComment(any(Comment.class))).thenReturn(savedComment);
        when(mockCommentQueryService.getCommentByCommentId(savedComment.id())).thenReturn(expectedResult);

        //when
        CommentResult result = commentCreator.createComment(command);

        //then
        assertThat(result.content()).isNull();
        assertThat(result.userId()).isEqualTo(command.userId());
        assertThat(result.postId()).isEqualTo(command.postId());
        verify(mockCommentCommandService).createComment(any(Comment.class));
        verify(mockCommentQueryService).getCommentByCommentId(savedComment.id());
        verify(mockDomainEventPublisher).publishEvents();
    }

    @Test
    @DisplayName("댓글 생성 - 도메인 이벤트 발행 검증")
    void verifyDomainEventPublishing() {
        //given
        CommentCreateCommand command = new CommentCreateCommand(
                "userId",
                "postId",
                "댓글 내용",
                "parentId",
                ReferenceType.POST
        );
        Comment savedComment = createTestCommentWithId();
        CommentResult expectedResult = createCommentResultFromCommand(command);
        
        when(mockCommentCommandService.createComment(any(Comment.class))).thenReturn(savedComment);
        when(mockCommentQueryService.getCommentByCommentId(savedComment.id())).thenReturn(expectedResult);

        //when
        commentCreator.createComment(command);

        //then
        verify(mockDomainEventPublisher).publishEvents();
    }

    @Test
    @DisplayName("댓글 생성 - 서비스 호출 순서 검증")
    void verifyServiceCallOrder() {
        //given
        CommentCreateCommand command = new CommentCreateCommand(
                "userId",
                "postId",
                "댓글 내용",
                "parentId",
                ReferenceType.POST
        );
        Comment savedComment = createTestCommentWithId();
        CommentResult expectedResult = createCommentResultFromCommand(command);
        
        when(mockCommentCommandService.createComment(any(Comment.class))).thenReturn(savedComment);
        when(mockCommentQueryService.getCommentByCommentId(savedComment.id())).thenReturn(expectedResult);

        //when
        commentCreator.createComment(command);

        //then
        var inOrder = org.mockito.Mockito.inOrder(mockCommentCommandService, mockCommentQueryService, mockDomainEventPublisher);
        inOrder.verify(mockCommentCommandService).createComment(any(Comment.class));
        inOrder.verify(mockDomainEventPublisher).publishEvents();
        inOrder.verify(mockCommentQueryService).getCommentByCommentId(savedComment.id());
    }

    @Test
    @DisplayName("다양한 사용자로 댓글 생성")
    void createCommentsWithDifferentUsers() {
        //given
        String[] userIds = {"user1", "user2", "user3"};
        Comment savedComment = createTestCommentWithId();
        
        when(mockCommentCommandService.createComment(any(Comment.class))).thenReturn(savedComment);

        //when & then
        for (String userId : userIds) {
            CommentCreateCommand command = new CommentCreateCommand(
                    userId,
                    "postId",
                    "댓글 내용",
                    "parentId",
                    ReferenceType.POST
            );
            CommentResult expectedResult = createCommentResultFromCommand(command);
            when(mockCommentQueryService.getCommentByCommentId(savedComment.id())).thenReturn(expectedResult);
            
            CommentResult result = commentCreator.createComment(command);
            
            assertThat(result.userId()).isEqualTo(command.userId());
        }
        
        verify(mockCommentCommandService, org.mockito.Mockito.times(3)).createComment(any(Comment.class));
        verify(mockCommentQueryService, org.mockito.Mockito.times(3)).getCommentByCommentId(savedComment.id());
        verify(mockDomainEventPublisher, org.mockito.Mockito.times(3)).publishEvents();
    }

    @Test
    @DisplayName("다양한 게시물에 댓글 생성")
    void createCommentsForDifferentPosts() {
        //given
        String[] postIds = {"post1", "post2", "post3"};
        Comment savedComment = createTestCommentWithId();
        
        when(mockCommentCommandService.createComment(any(Comment.class))).thenReturn(savedComment);

        //when & then
        for (String postId : postIds) {
            CommentCreateCommand command = new CommentCreateCommand(
                    "userId",
                    postId,
                    "댓글 내용",
                    "parentId",
                    ReferenceType.POST
            );
            CommentResult expectedResult = createCommentResultFromCommand(command);
            when(mockCommentQueryService.getCommentByCommentId(savedComment.id())).thenReturn(expectedResult);
            
            CommentResult result = commentCreator.createComment(command);
            
            assertThat(result.postId()).isEqualTo(command.postId());
        }
        
        verify(mockCommentCommandService, org.mockito.Mockito.times(3)).createComment(any(Comment.class));
        verify(mockCommentQueryService, org.mockito.Mockito.times(3)).getCommentByCommentId(savedComment.id());
        verify(mockDomainEventPublisher, org.mockito.Mockito.times(3)).publishEvents();
    }

    @Test
    @DisplayName("모든 ReferenceType으로 댓글 생성")
    void createCommentsWithAllReferenceTypes() {
        //given
        Comment savedComment = createTestCommentWithId();
        
        when(mockCommentCommandService.createComment(any(Comment.class))).thenReturn(savedComment);

        //when & then
        for (ReferenceType type : ReferenceType.values()) {
            CommentCreateCommand command = new CommentCreateCommand(
                    "userId",
                    "postId",
                    "댓글 내용",
                    "parentId",
                    type
            );
            CommentResult expectedResult = createCommentResultFromCommand(command);
            when(mockCommentQueryService.getCommentByCommentId(savedComment.id())).thenReturn(expectedResult);
            
            CommentResult result = commentCreator.createComment(command);
            
            assertThat(result.referenceType()).isEqualTo(command.referenceType());
        }
        
        verify(mockCommentCommandService, org.mockito.Mockito.times(2)).createComment(any(Comment.class));
        verify(mockCommentQueryService, org.mockito.Mockito.times(2)).getCommentByCommentId(savedComment.id());
        verify(mockDomainEventPublisher, org.mockito.Mockito.times(2)).publishEvents();
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

    private Comment createTestCommentWithEmptyContent() {
        return new Comment(
                "commentId",
                "userId",
                "",
                CommentRelation.of("postId", null, ReferenceType.POST),
                0,
                ContentStatus.NORMAL,
                new ArrayList<>(),
                LocalDateTime.now(),
                null
        );
    }

    private Comment createTestCommentWithNullContent() {
        return new Comment(
                "commentId",
                "userId",
                null,
                CommentRelation.of("postId", null, ReferenceType.POST),
                0,
                ContentStatus.NORMAL,
                new ArrayList<>(),
                LocalDateTime.now(),
                null
        );
    }

    private Comment createTestCommentWithCommentType() {
        return new Comment(
                "commentId",
                "userId",
                "대댓글 내용",
                CommentRelation.of("postId", null, ReferenceType.COMMENT),
                0,
                ContentStatus.NORMAL,
                new ArrayList<>(),
                LocalDateTime.now(),
                null
        );
    }


    private CommentResult createCommentResultFromCommand(CommentCreateCommand command) {
        return new CommentResult(
                "commentId",
                command.userId(),
                "testNickname",
                "testProfileUrl",
                "KR",
                "testRegion",
                command.content(),
                command.postId(),
                null,
                command.referenceType(),
                0,
                0,
                ContentStatus.NORMAL,
                LocalDateTime.now(),
                null
        );
    }
}