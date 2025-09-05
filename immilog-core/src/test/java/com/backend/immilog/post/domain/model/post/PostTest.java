package com.backend.immilog.post.domain.model.post;

import com.backend.immilog.post.exception.PostErrorCode;
import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.shared.enums.ContentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Post 도메인 모델 테스트")
class PostTest {

    @Nested
    @DisplayName("Post 생성 테스트")
    class PostCreationTest {

        @Test
        @DisplayName("정상적인 Post 생성")
        void createPostSuccessfully() {
            // given
            String userId = "user123";
            String userCountryId = "KR";
            String userRegion = "Seoul";
            String title = "테스트 제목";
            String content = "테스트 내용";
            Categories category = Categories.LIFE;
            String isPublic = "Y";

            // when
            Post post = Post.of(userId, userCountryId, userRegion, title, content, category, isPublic);

            // then
            assertThat(post).isNotNull();
            assertThat(post.userId()).isEqualTo(userId);
            assertThat(post.countryId()).isEqualTo(userCountryId);
            assertThat(post.region()).isEqualTo(userRegion);
            assertThat(post.title()).isEqualTo(title);
            assertThat(post.content()).isEqualTo(content);
            assertThat(post.category()).isEqualTo(category);
            assertThat(post.isPublic()).isEqualTo(isPublic);
            assertThat(post.commentCount()).isEqualTo(0L);
            assertThat(post.viewCount()).isEqualTo(0L);
            assertThat(post.status()).isEqualTo(ContentStatus.ACTIVE);
            assertThat(post.badge()).isNull();
            assertThat(post.createdAt()).isNotNull();
            assertThat(post.updatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("댓글 수 관리 테스트")
    class CommentCountTest {

        @Test
        @DisplayName("댓글 수 증가 성공")
        void increaseCommentCountSuccessfully() {
            // given
            Post post = createTestPost();
            Long initialCount = post.commentCount();

            // when
            Post updatedPost = post.increaseCommentCount();

            // then
            assertThat(updatedPost.commentCount()).isEqualTo(initialCount + 1);
            assertThat(updatedPost).isSameAs(post); // 같은 객체 인스턴스 반환
        }

        @Test
        @DisplayName("댓글 수 감소 성공")
        void decreaseCommentCountSuccessfully() {
            // given
            Post post = createTestPost();
            post.increaseCommentCount();
            post.increaseCommentCount();
            Long currentCount = post.commentCount();

            // when
            Post updatedPost = post.decreaseCommentCount();

            // then
            assertThat(updatedPost.commentCount()).isEqualTo(currentCount - 1);
            assertThat(updatedPost).isSameAs(post);
        }

        @Test
        @DisplayName("댓글 수가 0일 때 감소 시도해도 0으로 유지")
        void decreaseCommentCountWhenZero() {
            // given
            Post post = createTestPost();
            assertThat(post.commentCount()).isEqualTo(0L);

            // when
            Post updatedPost = post.decreaseCommentCount();

            // then
            assertThat(updatedPost.commentCount()).isEqualTo(0L);
        }

        @Test
        @DisplayName("삭제된 게시물의 댓글 수 증가 시 예외 발생")
        void increaseCommentCountOnDeletedPostThrowsException() {
            // given
            Post post = createTestPost();
            post.delete();

            // when & then
            assertThatThrownBy(() -> post.increaseCommentCount())
                .isInstanceOf(PostException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.POST_ALREADY_DELETED);
        }

        @Test
        @DisplayName("삭제된 게시물의 댓글 수 감소 시 예외 발생")
        void decreaseCommentCountOnDeletedPostThrowsException() {
            // given
            Post post = createTestPost();
            post.delete();

            // when & then
            assertThatThrownBy(() -> post.decreaseCommentCount())
                .isInstanceOf(PostException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.POST_ALREADY_DELETED);
        }
    }

    @Nested
    @DisplayName("공개 상태 업데이트 테스트")
    class PublicStatusUpdateTest {

        @Test
        @DisplayName("공개에서 비공개로 변경 성공")
        void updatePublicStatusFromPublicToPrivate() {
            // given
            Post post = createTestPost();
            assertThat(post.isPublic()).isEqualTo("Y");
            LocalDateTime beforeUpdate = post.updatedAt();

            // when
            Post updatedPost = post.updateIsPublic(false);

            // then
            assertThat(updatedPost.isPublic()).isEqualTo("N");
            assertThat(updatedPost.updatedAt()).isAfter(beforeUpdate);
            assertThat(updatedPost).isSameAs(post);
        }

        @Test
        @DisplayName("비공개에서 공개로 변경 성공")
        void updatePublicStatusFromPrivateToPublic() {
            // given
            Post post = createTestPost();
            post.updateIsPublic(false);
            LocalDateTime beforeUpdate = post.updatedAt();

            // when
            Post updatedPost = post.updateIsPublic(true);

            // then
            assertThat(updatedPost.isPublic()).isEqualTo("Y");
            assertThat(updatedPost.updatedAt()).isAfter(beforeUpdate);
        }

        @Test
        @DisplayName("동일한 공개 상태로 업데이트 시 변경 없음")
        void updatePublicStatusWithSameValue() {
            // given
            Post post = createTestPost();
            LocalDateTime beforeUpdate = post.updatedAt();

            // when
            Post updatedPost = post.updateIsPublic(true);

            // then
            assertThat(updatedPost.isPublic()).isEqualTo("Y");
            assertThat(updatedPost.updatedAt()).isEqualTo(beforeUpdate); // 시간 변경 없음
        }

        @Test
        @DisplayName("null 값으로 공개 상태 업데이트 시 예외 발생")
        void updatePublicStatusWithNullThrowsException() {
            // given
            Post post = createTestPost();

            // when & then
            assertThatThrownBy(() -> post.updateIsPublic(null))
                .isInstanceOf(PostException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.INVALID_PUBLIC_STATUS);
        }

        @Test
        @DisplayName("삭제된 게시물의 공개 상태 업데이트 시 예외 발생")
        void updatePublicStatusOnDeletedPostThrowsException() {
            // given
            Post post = createTestPost();
            post.delete();

            // when & then
            assertThatThrownBy(() -> post.updateIsPublic(false))
                .isInstanceOf(PostException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.POST_ALREADY_DELETED);
        }
    }

    @Nested
    @DisplayName("내용 업데이트 테스트")
    class ContentUpdateTest {

        @Test
        @DisplayName("내용 업데이트 성공")
        void updateContentSuccessfully() {
            // given
            Post post = createTestPost();
            String originalContent = post.content();
            String newContent = "새로운 내용";
            LocalDateTime beforeUpdate = post.updatedAt();

            // when
            Post updatedPost = post.updateContent(newContent);

            // then
            assertThat(updatedPost.content()).isEqualTo(newContent);
            assertThat(updatedPost.content()).isNotEqualTo(originalContent);
            assertThat(updatedPost.updatedAt()).isAfter(beforeUpdate);
            assertThat(updatedPost).isSameAs(post);
        }

        @Test
        @DisplayName("동일한 내용으로 업데이트 시 변경 없음")
        void updateContentWithSameValue() {
            // given
            Post post = createTestPost();
            String originalContent = post.content();
            LocalDateTime beforeUpdate = post.updatedAt();

            // when
            Post updatedPost = post.updateContent(originalContent);

            // then
            assertThat(updatedPost.content()).isEqualTo(originalContent);
            assertThat(updatedPost.updatedAt()).isEqualTo(beforeUpdate);
        }

        @Test
        @DisplayName("null 내용으로 업데이트 시 변경 없음")
        void updateContentWithNull() {
            // given
            Post post = createTestPost();
            String originalContent = post.content();

            // when
            Post updatedPost = post.updateContent(null);

            // then
            assertThat(updatedPost.content()).isEqualTo(originalContent);
        }

        @Test
        @DisplayName("삭제된 게시물의 내용 업데이트 시 예외 발생")
        void updateContentOnDeletedPostThrowsException() {
            // given
            Post post = createTestPost();
            post.delete();

            // when & then
            assertThatThrownBy(() -> post.updateContent("새 내용"))
                .isInstanceOf(PostException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.POST_ALREADY_DELETED);
        }
    }

    @Nested
    @DisplayName("제목 업데이트 테스트")
    class TitleUpdateTest {

        @Test
        @DisplayName("제목 업데이트 성공")
        void updateTitleSuccessfully() {
            // given
            Post post = createTestPost();
            String originalTitle = post.title();
            String newTitle = "새로운 제목";
            LocalDateTime beforeUpdate = post.updatedAt();

            // when
            Post updatedPost = post.updateTitle(newTitle);

            // then
            assertThat(updatedPost.title()).isEqualTo(newTitle);
            assertThat(updatedPost.title()).isNotEqualTo(originalTitle);
            assertThat(updatedPost.updatedAt()).isAfter(beforeUpdate);
        }

        @Test
        @DisplayName("동일한 제목으로 업데이트 시 변경 없음")
        void updateTitleWithSameValue() {
            // given
            Post post = createTestPost();
            String originalTitle = post.title();
            LocalDateTime beforeUpdate = post.updatedAt();

            // when
            Post updatedPost = post.updateTitle(originalTitle);

            // then
            assertThat(updatedPost.title()).isEqualTo(originalTitle);
            assertThat(updatedPost.updatedAt()).isEqualTo(beforeUpdate);
        }

        @Test
        @DisplayName("삭제된 게시물의 제목 업데이트 시 예외 발생")
        void updateTitleOnDeletedPostThrowsException() {
            // given
            Post post = createTestPost();
            post.delete();

            // when & then
            assertThatThrownBy(() -> post.updateTitle("새 제목"))
                .isInstanceOf(PostException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.POST_ALREADY_DELETED);
        }
    }

    @Nested
    @DisplayName("게시물 삭제 테스트")
    class PostDeletionTest {

        @Test
        @DisplayName("게시물 삭제 성공")
        void deletePostSuccessfully() {
            // given
            Post post = createTestPost();
            assertThat(post.status()).isEqualTo(ContentStatus.ACTIVE);
            LocalDateTime beforeDelete = post.updatedAt();

            // when
            Post deletedPost = post.delete();

            // then
            assertThat(deletedPost.status()).isEqualTo(ContentStatus.DELETED);
            assertThat(deletedPost.updatedAt()).isAfter(beforeDelete);
            assertThat(deletedPost).isSameAs(post);
        }

        @Test
        @DisplayName("이미 삭제된 게시물 삭제 시 예외 발생")
        void deleteAlreadyDeletedPostThrowsException() {
            // given
            Post post = createTestPost();
            post.delete();

            // when & then
            assertThatThrownBy(() -> post.delete())
                .isInstanceOf(PostException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.POST_ALREADY_DELETED);
        }
    }

    @Nested
    @DisplayName("조회수 증가 테스트")
    class ViewCountTest {

        @Test
        @DisplayName("조회수 증가 성공")
        void increaseViewCountSuccessfully() {
            // given
            Post post = createTestPost();
            Long initialViewCount = post.viewCount();

            // when
            Post updatedPost = post.increaseViewCount();

            // then
            assertThat(updatedPost.viewCount()).isEqualTo(initialViewCount + 1);
            assertThat(updatedPost).isSameAs(post);
        }

        @Test
        @DisplayName("삭제된 게시물의 조회수 증가 시 예외 발생")
        void increaseViewCountOnDeletedPostThrowsException() {
            // given
            Post post = createTestPost();
            post.delete();

            // when & then
            assertThatThrownBy(() -> post.increaseViewCount())
                .isInstanceOf(PostException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.POST_ALREADY_DELETED);
        }
    }

    @Nested
    @DisplayName("뱃지 업데이트 테스트")
    class BadgeUpdateTest {

        @Test
        @DisplayName("뱃지 업데이트 성공")
        void updateBadgeSuccessfully() {
            // given
            Post post = createTestPost();
            Badge newBadge = new Badge("POPULAR", "인기글", "#FF5722");

            // when
            Post updatedPost = post.updateBadge(newBadge);

            // then
            assertThat(updatedPost.badge()).isEqualTo(newBadge);
            assertThat(updatedPost).isSameAs(post);
        }

        @Test
        @DisplayName("동일한 뱃지로 업데이트 시 변경 없음")
        void updateBadgeWithSameValue() {
            // given
            Badge badge = new Badge("HOT", "핫한글", "#E91E63");
            Post post = createTestPost();
            post.updateBadge(badge);

            // when
            Post updatedPost = post.updateBadge(badge);

            // then
            assertThat(updatedPost.badge()).isEqualTo(badge);
            assertThat(updatedPost).isSameAs(post);
        }
    }

    private Post createTestPost() {
        return Post.of(
            "user123",
            "KR",
            "Seoul",
            "테스트 제목",
            "테스트 내용",
            Categories.LIFE,
            "Y"
        );
    }
}