package com.backend.immilog.comment.domain.service;

import com.backend.immilog.comment.domain.model.Comment;
import com.backend.immilog.comment.domain.model.ReferenceType;
import com.backend.immilog.shared.enums.ContentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CommentDomainService")
@ExtendWith(MockitoExtension.class)
class CommentDomainServiceTest {

    @InjectMocks
    private CommentDomainService commentDomainService;

    @Nested
    @DisplayName("댓글 생성")
    class CreateComment {

        @Test
        @DisplayName("게시물 댓글 생성 성공")
        void createPostComment() {
            var userId = "user123";
            var postId = "post456";
            var content = "댓글 내용";
            String parentId = null;
            var referenceType = ReferenceType.POST;

            var comment = commentDomainService.createComment(userId, postId, content, parentId, referenceType);

            assertThat(comment.userId()).isEqualTo(userId);
            assertThat(comment.postId()).isEqualTo(postId);
            assertThat(comment.content()).isEqualTo(content);
            assertThat(comment.parentId()).isNull();
            assertThat(comment.referenceType()).isEqualTo(ReferenceType.POST);
            assertThat(comment.status()).isEqualTo(ContentStatus.NORMAL);
            assertThat(comment.replyCount()).isEqualTo(0);
            assertThat(comment.likeUsers()).isEmpty();
            assertThat(comment.createdAt()).isNotNull();
            assertThat(comment.updatedAt()).isNull();
        }

        @Test
        @DisplayName("대댓글 생성 성공")
        void createReplyComment() {
            var userId = "user123";
            var postId = "post456";
            var content = "대댓글 내용";
            var parentId = "parent789";
            var referenceType = ReferenceType.COMMENT;

            var comment = commentDomainService.createComment(userId, postId, content, parentId, referenceType);

            assertThat(comment.userId()).isEqualTo(userId);
            assertThat(comment.postId()).isEqualTo(postId);
            assertThat(comment.content()).isEqualTo(content);
            assertThat(comment.parentId()).isEqualTo(parentId);
            assertThat(comment.referenceType()).isEqualTo(ReferenceType.COMMENT);
            assertThat(comment.status()).isEqualTo(ContentStatus.NORMAL);
        }

        @Test
        @DisplayName("빈 내용으로 댓글 생성")
        void createCommentWithEmptyContent() {
            var comment = commentDomainService.createComment(
                    "user123", "post456", "", null, ReferenceType.POST
            );

            assertThat(comment.content()).isEmpty();
            assertThat(comment.status()).isEqualTo(ContentStatus.NORMAL);
        }

        @Test
        @DisplayName("긴 내용으로 댓글 생성")
        void createCommentWithLongContent() {
            var longContent = "a".repeat(1000);

            var comment = commentDomainService.createComment(
                    "user123", "post456", longContent, null, ReferenceType.POST
            );

            assertThat(comment.content()).isEqualTo(longContent);
            assertThat(comment.content()).hasSize(1000);
        }

        @Test
        @DisplayName("특수문자가 포함된 내용으로 댓글 생성")
        void createCommentWithSpecialCharacters() {
            var specialContent = "댓글 내용 !@#$%^&*()_+{}|:\"<>?[];',./`~";

            var comment = commentDomainService.createComment(
                    "user123", "post456", specialContent, null, ReferenceType.POST
            );

            assertThat(comment.content()).isEqualTo(specialContent);
        }
    }

    @Nested
    @DisplayName("댓글 생성 이벤트 발행")
    class PublishCommentCreatedEvent {

        @Test
        @DisplayName("ID가 있는 댓글 이벤트 발행")
        void publishEventWithId() {
            var comment = Comment.of("user123", "post456", "댓글 내용", null, ReferenceType.POST)
                    .withId("comment789");

            assertThatCode(() -> commentDomainService.publishCommentCreatedEvent(comment))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("ID가 없는 댓글 이벤트 발행")
        void publishEventWithoutId() {
            var comment = Comment.of("user123", "post456", "댓글 내용", null, ReferenceType.POST);

            assertThatCode(() -> commentDomainService.publishCommentCreatedEvent(comment))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("대댓글 이벤트 발행")
        void publishReplyCommentEvent() {
            var comment = Comment.of("user123", "post456", "대댓글 내용", "parent789", ReferenceType.COMMENT)
                    .withId("reply123");

            assertThatCode(() -> commentDomainService.publishCommentCreatedEvent(comment))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("삭제된 댓글 이벤트 발행")
        void publishDeletedCommentEvent() {
            var comment = Comment.of("user123", "post456", "댓글 내용", null, ReferenceType.POST)
                    .withId("comment789")
                    .delete();

            assertThatCode(() -> commentDomainService.publishCommentCreatedEvent(comment))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("서비스 통합 시나리오")
    class IntegrationScenarios {

        @Test
        @DisplayName("댓글 생성과 이벤트 발행 연계")
        void createCommentAndPublishEvent() {
            var userId = "user123";
            var postId = "post456";
            var content = "통합 테스트 댓글";

            var comment = commentDomainService.createComment(userId, postId, content, null, ReferenceType.POST);
            var commentWithId = comment.withId("generated123");

            assertThat(comment.userId()).isEqualTo(userId);
            assertThat(commentWithId.id()).isEqualTo("generated123");

            assertThatCode(() -> commentDomainService.publishCommentCreatedEvent(commentWithId))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("다양한 참조 타입으로 댓글 생성")
        void createCommentsWithDifferentReferenceTypes() {
            var postComment = commentDomainService.createComment(
                    "user1", "post1", "게시물 댓글", null, ReferenceType.POST
            );

            var replyComment = commentDomainService.createComment(
                    "user2", "post1", "대댓글", "comment1", ReferenceType.COMMENT
            );

            assertThat(postComment.referenceType()).isEqualTo(ReferenceType.POST);
            assertThat(postComment.parentId()).isNull();

            assertThat(replyComment.referenceType()).isEqualTo(ReferenceType.COMMENT);
            assertThat(replyComment.parentId()).isEqualTo("comment1");
        }
    }
}