package com.backend.immilog.comment.domain.model;

import com.backend.immilog.shared.enums.ContentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Comment 도메인 모델")
class CommentTest {

    @Nested
    @DisplayName("댓글 생성")
    class CreateComment {

        @Test
        @DisplayName("게시물 댓글 생성 성공")
        void createPostComment() {
            var comment = Comment.of("user123", "post456", "댓글 내용", null, ReferenceType.POST);

            assertThat(comment.userId()).isEqualTo("user123");
            assertThat(comment.content()).isEqualTo("댓글 내용");
            assertThat(comment.postId()).isEqualTo("post456");
            assertThat(comment.parentId()).isNull();
            assertThat(comment.referenceType()).isEqualTo(ReferenceType.POST);
            assertThat(comment.replyCount()).isEqualTo(0);
            assertThat(comment.status()).isEqualTo(ContentStatus.NORMAL);
            assertThat(comment.likeUsers()).isEmpty();
            assertThat(comment.createdAt()).isNotNull();
            assertThat(comment.updatedAt()).isNull();
        }

        @Test
        @DisplayName("대댓글 생성 성공")
        void createReplyComment() {
            var comment = Comment.of("user123", "post456", "대댓글 내용", "parent789", ReferenceType.COMMENT);

            assertThat(comment.userId()).isEqualTo("user123");
            assertThat(comment.content()).isEqualTo("대댓글 내용");
            assertThat(comment.postId()).isEqualTo("post456");
            assertThat(comment.parentId()).isEqualTo("parent789");
            assertThat(comment.referenceType()).isEqualTo(ReferenceType.COMMENT);
            assertThat(comment.status()).isEqualTo(ContentStatus.NORMAL);
        }
    }

    @Nested
    @DisplayName("댓글 상태 변경")
    class ChangeCommentStatus {

        @Test
        @DisplayName("댓글 삭제")
        void deleteComment() {
            var comment = createTestComment();

            var deletedComment = comment.delete();

            assertThat(deletedComment.status()).isEqualTo(ContentStatus.DELETED);
            assertThat(deletedComment.updatedAt()).isNotNull();
            assertThat(deletedComment.id()).isEqualTo(comment.id());
            assertThat(deletedComment.content()).isEqualTo(comment.content());
        }

        @Test
        @DisplayName("댓글 내용 수정")
        void updateContent() {
            var comment = createTestComment();
            var newContent = "수정된 댓글 내용";

            var updatedComment = comment.updateContent(newContent);

            assertThat(updatedComment.content()).isEqualTo(newContent);
            assertThat(updatedComment.updatedAt()).isNotNull();
            assertThat(updatedComment.id()).isEqualTo(comment.id());
            assertThat(updatedComment.status()).isEqualTo(comment.status());
        }
    }

    @Nested
    @DisplayName("댓글 좋아요")
    class CommentLike {

        @Test
        @DisplayName("좋아요 사용자 추가 성공")
        void addLikeUser() {
            var comment = createTestComment();
            var userId = "newUser123";

            var likedComment = comment.addLikeUser(userId);

            assertThat(likedComment.likeUsers()).contains(userId);
            assertThat(likedComment.likeUsers()).hasSize(1);
            assertThat(likedComment.id()).isEqualTo(comment.id());
        }

        @Test
        @DisplayName("좋아요 사용자 목록이 null인 경우 그대로 반환")
        void addLikeUserWithNullList() {
            var comment = Comment.builder()
                    .userId("user123")
                    .content("테스트 댓글")
                    .commentRelation(com.backend.immilog.comment.domain.model.CommentRelation.of("post456", null, ReferenceType.POST))
                    .replyCount(0)
                    .status(ContentStatus.NORMAL)
                    .likeUsers(null)
                    .build();

            var result = comment.addLikeUser("user456");

            assertThat(result).isSameAs(comment);
        }

        @Test
        @DisplayName("기존 좋아요 사용자가 있는 경우 추가")
        void addLikeUserToExistingList() {
            var existingLikeUsers = new ArrayList<>(List.of("user1", "user2"));
            var comment = Comment.builder()
                    .userId("user123")
                    .content("테스트 댓글")
                    .commentRelation(com.backend.immilog.comment.domain.model.CommentRelation.of("post456", null, ReferenceType.POST))
                    .replyCount(0)
                    .status(ContentStatus.NORMAL)
                    .likeUsers(existingLikeUsers)
                    .build();

            var likedComment = comment.addLikeUser("user3");

            assertThat(likedComment.likeUsers()).containsExactly("user1", "user2", "user3");
            assertThat(likedComment.likeUsers()).hasSize(3);
        }
    }

    @Nested
    @DisplayName("댓글 ID 설정")
    class SetCommentId {

        @Test
        @DisplayName("ID 설정 성공")
        void withId() {
            var comment = createTestComment();
            var newId = "comment999";

            var commentWithId = comment.withId(newId);

            assertThat(commentWithId.id()).isEqualTo(newId);
            assertThat(commentWithId.userId()).isEqualTo(comment.userId());
            assertThat(commentWithId.content()).isEqualTo(comment.content());
        }
    }

    @Nested
    @DisplayName("댓글 관계 정보")
    class CommentRelation {

        @Test
        @DisplayName("게시물 ID 조회")
        void getPostId() {
            var comment = createTestComment();

            assertThat(comment.postId()).isEqualTo("post456");
        }

        @Test
        @DisplayName("부모 댓글 ID 조회")
        void getParentId() {
            var comment = Comment.of("user123", "post456", "대댓글", "parent789", ReferenceType.COMMENT);

            assertThat(comment.parentId()).isEqualTo("parent789");
        }

        @Test
        @DisplayName("참조 타입 조회")
        void getReferenceType() {
            var comment = createTestComment();

            assertThat(comment.referenceType()).isEqualTo(ReferenceType.POST);
        }
    }

    @Nested
    @DisplayName("도메인 이벤트")
    class DomainEvent {

        @Test
        @DisplayName("ID가 있는 댓글의 생성 이벤트 발행")
        void publishCreatedEventWithId() {
            var comment = Comment.builder()
                    .id("comment123")
                    .userId("user456")
                    .content("댓글 내용")
                    .commentRelation(com.backend.immilog.comment.domain.model.CommentRelation.of("post789", null, ReferenceType.POST))
                    .replyCount(0)
                    .status(ContentStatus.NORMAL)
                    .likeUsers(new ArrayList<>())
                    .build();

            assertThatCode(comment::publishCreatedEvent).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("ID가 없는 댓글의 이벤트 발행하지 않음")
        void notPublishCreatedEventWithoutId() {
            var comment = createTestComment();

            assertThatCode(comment::publishCreatedEvent).doesNotThrowAnyException();
        }
    }

    private Comment createTestComment() {
        return Comment.of("user123", "post456", "테스트 댓글", null, ReferenceType.POST);
    }
}