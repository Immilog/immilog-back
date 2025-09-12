package com.backend.immilog.post.domain.model;

import com.backend.immilog.post.domain.model.post.*;
import com.backend.immilog.post.exception.PostErrorCode;
import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.shared.enums.ContentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Post Aggregate")
class PostTest {

    @Nested
    @DisplayName("Post 생성")
    class PostCreation {

        @Test
        @DisplayName("of 팩토리 메서드로 새 Post 생성 성공")
        void createPostWithFactory() {
            Post post = Post.of(
                    "user123",
                    "US",
                    "California",
                    "Test Title",
                    "Test Content",
                    Categories.QNA,
                    true
            );

            assertThat(post.id()).isNotNull();
            assertThat(post.userId()).isEqualTo("user123");
            assertThat(post.title()).isEqualTo("Test Title");
            assertThat(post.content()).isEqualTo("Test Content");
            assertThat(post.category()).isEqualTo(Categories.QNA);
            assertThat(post.isPublic()).isTrue();
            assertThat(post.commentCount().value()).isEqualTo(0L);
            assertThat(post.status()).isEqualTo(ContentStatus.NORMAL);
            assertThat(post.createdAt()).isNotNull();
            assertThat(post.updatedAt()).isNotNull();
        }

        @Test
        @DisplayName("private 게시물 생성")
        void createPrivatePost() {
            Post post = Post.of(
                    "user123",
                    "US",
                    "California",
                    "Private Title",
                    "Private Content",
                    Categories.COMMUNICATION,
                    false
            );

            assertThat(post.isPublic()).isFalse();
            assertThat(post.publicStatus()).isEqualTo(PublicStatus.PRIVATE);
            assertThat(post.isPublicValue()).isEqualTo("N");
        }
    }

    @Nested
    @DisplayName("댓글 수 관리")
    class CommentCountManagement {

        @Test
        @DisplayName("댓글 수 증가 성공")
        void increaseCommentCount() {
            Post post = createTestPost();

            Post updated = post.increaseCommentCount();

            assertThat(updated.commentCount().value()).isEqualTo(1L);
            assertThat(updated).isSameAs(post);
        }

        @Test
        @DisplayName("댓글 수 감소 성공")
        void decreaseCommentCount() {
            Post post = createTestPost();
            post.increaseCommentCount();
            post.increaseCommentCount();

            Post updated = post.decreaseCommentCount();

            assertThat(updated.commentCount().value()).isEqualTo(1L);
            assertThat(updated).isSameAs(post);
        }

        @Test
        @DisplayName("댓글 수 0에서 감소 시도 시 0 유지")
        void decreaseCommentCountFromZero() {
            Post post = createTestPost();

            Post updated = post.decreaseCommentCount();

            assertThat(updated.commentCount().value()).isEqualTo(0L);
        }

        @Test
        @DisplayName("삭제된 게시물에서 댓글 수 증가 시 예외 발생")
        void increaseCommentCountOnDeletedPost() {
            Post post = createTestPost();
            post.delete();

            assertThatThrownBy(post::increaseCommentCount)
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.POST_ALREADY_DELETED);
        }

        @Test
        @DisplayName("삭제된 게시물에서 댓글 수 감소 시 예외 발생")
        void decreaseCommentCountOnDeletedPost() {
            Post post = createTestPost();
            post.delete();

            assertThatThrownBy(post::decreaseCommentCount)
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.POST_ALREADY_DELETED);
        }
    }

    @Nested
    @DisplayName("공개 상태 변경")
    class PublicStatusUpdate {

        @Test
        @DisplayName("공개 상태를 비공개로 변경")
        void updateToPrivate() {
            Post post = createTestPost();

            Post updated = post.updatePublicStatus(false);

            assertThat(updated.isPublic()).isFalse();
            assertThat(updated.publicStatus()).isEqualTo(PublicStatus.PRIVATE);
            assertThat(updated.updatedAt()).isAfter(post.createdAt());
            assertThat(updated).isSameAs(post);
        }

        @Test
        @DisplayName("비공개 상태를 공개로 변경")
        void updateToPublic() {
            Post post = Post.of(
                    "user123", "US", "CA",
                    "Title", "Content", Categories.QNA, false
            );

            Post updated = post.updatePublicStatus(true);

            assertThat(updated.isPublic()).isTrue();
            assertThat(updated.publicStatus()).isEqualTo(PublicStatus.PUBLIC);
        }

        @Test
        @DisplayName("같은 공개 상태로 변경 시 변화 없음")
        void updateToSamePublicStatus() {
            Post post = createTestPost();
            var originalUpdatedAt = post.updatedAt();

            Post updated = post.updatePublicStatus(true);

            assertThat(updated.updatedAt()).isEqualTo(originalUpdatedAt);
            assertThat(updated).isSameAs(post);
        }

        @Test
        @DisplayName("null 공개 상태로 변경 시 예외 발생")
        void updatePublicStatusWithNull() {
            Post post = createTestPost();

            assertThatThrownBy(() -> post.updatePublicStatus(null))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.INVALID_PUBLIC_STATUS);
        }

        @Test
        @DisplayName("삭제된 게시물의 공개 상태 변경 시 예외 발생")
        void updatePublicStatusOnDeletedPost() {
            Post post = createTestPost();
            post.delete();

            assertThatThrownBy(() -> post.updatePublicStatus(false))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.POST_ALREADY_DELETED);
        }
    }

    @Nested
    @DisplayName("게시물 내용 수정")
    class ContentUpdate {

        @Test
        @DisplayName("제목 수정 성공")
        void updateTitle() {
            Post post = createTestPost();
            String newTitle = "Updated Title";

            Post updated = post.updateTitle(newTitle);

            assertThat(updated.title()).isEqualTo(newTitle);
            assertThat(updated.updatedAt()).isAfter(post.createdAt());
            assertThat(updated).isSameAs(post);
        }

        @Test
        @DisplayName("내용 수정 성공")
        void updateContent() {
            Post post = createTestPost();
            String newContent = "Updated Content";

            Post updated = post.updateContent(newContent);

            assertThat(updated.content()).isEqualTo(newContent);
            assertThat(updated.updatedAt()).isAfter(post.createdAt());
            assertThat(updated).isSameAs(post);
        }

        @Test
        @DisplayName("null 제목으로 수정 시 변화 없음")
        void updateTitleWithNull() {
            Post post = createTestPost();
            var originalTitle = post.title();
            var originalUpdatedAt = post.updatedAt();

            Post updated = post.updateTitle(null);

            assertThat(updated.title()).isEqualTo(originalTitle);
            assertThat(updated.updatedAt()).isEqualTo(originalUpdatedAt);
            assertThat(updated).isSameAs(post);
        }

        @Test
        @DisplayName("같은 제목으로 수정 시 변화 없음")
        void updateTitleWithSameTitle() {
            Post post = createTestPost();
            var originalUpdatedAt = post.updatedAt();

            Post updated = post.updateTitle(post.title());

            assertThat(updated.updatedAt()).isEqualTo(originalUpdatedAt);
            assertThat(updated).isSameAs(post);
        }

        @Test
        @DisplayName("삭제된 게시물의 제목 수정 시 예외 발생")
        void updateTitleOnDeletedPost() {
            Post post = createTestPost();
            post.delete();

            assertThatThrownBy(() -> post.updateTitle("New Title"))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.POST_ALREADY_DELETED);
        }

        @Test
        @DisplayName("삭제된 게시물의 내용 수정 시 예외 발생")
        void updateContentOnDeletedPost() {
            Post post = createTestPost();
            post.delete();

            assertThatThrownBy(() -> post.updateContent("New Content"))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.POST_ALREADY_DELETED);
        }
    }

    @Nested
    @DisplayName("게시물 삭제")
    class PostDeletion {

        @Test
        @DisplayName("게시물 삭제 성공")
        void deletePost() {
            Post post = createTestPost();

            Post deleted = post.delete();

            assertThat(deleted.status()).isEqualTo(ContentStatus.DELETED);
            assertThat(deleted.updatedAt()).isAfter(post.createdAt());
            assertThat(deleted).isSameAs(post);
        }

        @Test
        @DisplayName("이미 삭제된 게시물 삭제 시 예외 발생")
        void deleteAlreadyDeletedPost() {
            Post post = createTestPost();
            post.delete();

            assertThatThrownBy(post::delete)
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.POST_ALREADY_DELETED);
        }
    }

    @Nested
    @DisplayName("조회수 증가")
    class ViewCountIncrease {

        @Test
        @DisplayName("조회수 증가 성공")
        void increaseViewCount() {
            Post post = createTestPost();
            var originalViewCount = post.viewCount();

            Post updated = post.increaseViewCount();

            assertThat(updated.viewCount()).isEqualTo(originalViewCount + 1);
            assertThat(updated).isSameAs(post);
        }

        @Test
        @DisplayName("삭제된 게시물의 조회수 증가 시 예외 발생")
        void increaseViewCountOnDeletedPost() {
            Post post = createTestPost();
            post.delete();

            assertThatThrownBy(post::increaseViewCount)
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.POST_ALREADY_DELETED);
        }
    }

    @Nested
    @DisplayName("뱃지 업데이트")
    class BadgeUpdate {

        @Test
        @DisplayName("뱃지 업데이트 성공")
        void updateBadge() {
            Post post = createTestPost();

            Post updated = post.updateBadge(Badge.HOT);

            assertThat(updated.badge()).isEqualTo(Badge.HOT);
            assertThat(updated).isSameAs(post);
        }

        @Test
        @DisplayName("같은 뱃지로 업데이트 시 변화 없음")
        void updateBadgeWithSameBadge() {
            Post post = createTestPost();
            post.updateBadge(Badge.WEEKLY_BEST);

            Post updated = post.updateBadge(Badge.WEEKLY_BEST);

            assertThat(updated).isSameAs(post);
        }

        @Test
        @DisplayName("null 뱃지에서 뱃지 업데이트")
        void updateBadgeFromNull() {
            Post post = createTestPost();

            Post updated = post.updateBadge(Badge.HOT);

            assertThat(updated.badge()).isEqualTo(Badge.HOT);
        }
    }

    private Post createTestPost() {
        return Post.of(
                "user123",
                "US",
                "California",
                "Test Title",
                "Test Content",
                Categories.QNA,
                true
        );
    }
}