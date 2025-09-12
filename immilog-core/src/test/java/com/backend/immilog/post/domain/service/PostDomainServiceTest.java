package com.backend.immilog.post.domain.service;

import com.backend.immilog.post.domain.model.post.*;
import com.backend.immilog.post.domain.repositories.PostDomainRepository;
import com.backend.immilog.post.exception.PostErrorCode;
import com.backend.immilog.post.exception.PostException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("PostDomainService")
class PostDomainServiceTest {

    private final PostDomainRepository postDomainRepository = mock(PostDomainRepository.class);
    private final PostValidator postValidator = mock(PostValidator.class);

    private PostDomainService postDomainService = new PostDomainService(postDomainRepository, postValidator);

    private Post testPost;

    @BeforeEach
    void setUp() {
        testPost = createTestPost();
    }

    @Nested
    @DisplayName("게시물 생성")
    class CreatePost {

        @Test
        @DisplayName("유효한 게시물 생성 성공")
        void createValidPost() {
            when(postDomainRepository.save(any(Post.class))).thenReturn(testPost);

            Post result = postDomainService.createPost(testPost);

            assertThat(result).isEqualTo(testPost);
            verify(postDomainRepository).save(testPost);
        }

        @Test
        @DisplayName("null ID를 가진 게시물 생성 시 예외 발생")
        void createPostWithNullId() {
            Post invalidPost = new Post(
                    null,
                    new PostUserInfo("user123"),
                    PostInfo.of("Title", "Content", "US", "CA"),
                    Categories.QNA,
                    PublicStatus.PUBLIC,
                    null,
                    CommentCount.zero(),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            assertThatThrownBy(() -> postDomainService.createPost(invalidPost))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.INVALID_POST_DATA);

            verify(postDomainRepository, never()).save(any());
        }

        @Test
        @DisplayName("빈 제목을 가진 게시물 생성 시 예외 발생")
        void createPostWithBlankTitle() {
            Post invalidPost = new Post(
                    PostId.generate(),
                    new PostUserInfo("user123"),
                    PostInfo.of("", "Content", "US", "CA"),
                    Categories.QNA,
                    PublicStatus.PUBLIC,
                    null,
                    CommentCount.zero(),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            assertThatThrownBy(() -> postDomainService.createPost(invalidPost))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.INVALID_POST_DATA);
        }

        @Test
        @DisplayName("빈 내용을 가진 게시물 생성 시 예외 발생")
        void createPostWithBlankContent() {
            Post invalidPost = new Post(
                    PostId.generate(),
                    new PostUserInfo("user123"),
                    PostInfo.of("Title", "", "US", "CA"),
                    Categories.QNA,
                    PublicStatus.PUBLIC,
                    null,
                    CommentCount.zero(),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            assertThatThrownBy(() -> postDomainService.createPost(invalidPost))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.INVALID_POST_DATA);
        }

        @Test
        @DisplayName("빈 사용자 ID를 가진 게시물 생성 시 예외 발생")
        void createPostWithBlankUserId() {
            Post invalidPost = new Post(
                    PostId.generate(),
                    new PostUserInfo(""),
                    PostInfo.of("Title", "Content", "US", "CA"),
                    Categories.QNA,
                    PublicStatus.PUBLIC,
                    null,
                    CommentCount.zero(),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            assertThatThrownBy(() -> postDomainService.createPost(invalidPost))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.INVALID_USER);
        }
    }

    @Nested
    @DisplayName("게시물 내용 수정")
    class UpdatePostContent {

        @Test
        @DisplayName("제목과 내용 수정 성공")
        void updatePostContentSuccess() {
            PostId postId = PostId.of("post123");
            String userId = "user123";
            String newTitle = "Updated Title";
            String newContent = "Updated Content";

            when(postDomainRepository.findById(postId.value())).thenReturn(Optional.of(testPost));
            when(postDomainRepository.save(any(Post.class))).thenReturn(testPost);

            Post result = postDomainService.updatePostContent(postId, userId, newTitle, newContent);

            assertThat(result).isNotNull();
            verify(postValidator).validatePostAccess(testPost, userId);
            verify(postValidator).validatePostUpdate(testPost, newTitle, newContent);
            verify(postDomainRepository).save(testPost);
        }

        @Test
        @DisplayName("제목만 수정")
        void updateTitleOnly() {
            PostId postId = PostId.of("post123");
            String userId = "user123";
            String newTitle = "Updated Title";

            when(postDomainRepository.findById(postId.value())).thenReturn(Optional.of(testPost));
            when(postDomainRepository.save(any(Post.class))).thenReturn(testPost);

            Post result = postDomainService.updatePostContent(postId, userId, newTitle, null);

            assertThat(result).isNotNull();
            verify(postValidator).validatePostAccess(testPost, userId);
            verify(postValidator).validatePostUpdate(testPost, newTitle, null);
        }

        @Test
        @DisplayName("내용만 수정")
        void updateContentOnly() {
            PostId postId = PostId.of("post123");
            String userId = "user123";
            String newContent = "Updated Content";

            when(postDomainRepository.findById(postId.value())).thenReturn(Optional.of(testPost));
            when(postDomainRepository.save(any(Post.class))).thenReturn(testPost);

            Post result = postDomainService.updatePostContent(postId, userId, null, newContent);

            assertThat(result).isNotNull();
            verify(postValidator).validatePostAccess(testPost, userId);
            verify(postValidator).validatePostUpdate(testPost, null, newContent);
        }

        @Test
        @DisplayName("존재하지 않는 게시물 수정 시 예외 발생")
        void updateNonExistentPost() {
            PostId postId = PostId.of("nonexistent");
            String userId = "user123";

            when(postDomainRepository.findById(postId.value())).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    postDomainService.updatePostContent(postId, userId, "Title", "Content"))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.POST_NOT_FOUND);

            verify(postDomainRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("게시물 가시성 수정")
    class UpdatePostVisibility {

        @Test
        @DisplayName("가시성 수정 성공")
        void updateVisibilitySuccess() {
            PostId postId = PostId.of("post123");
            String userId = "user123";
            Boolean isPublic = false;

            when(postDomainRepository.findById(postId.value())).thenReturn(Optional.of(testPost));
            when(postDomainRepository.save(any(Post.class))).thenReturn(testPost);

            Post result = postDomainService.updatePostVisibility(postId, userId, isPublic);

            assertThat(result).isNotNull();
            verify(postValidator).validatePostAccess(testPost, userId);
            verify(postValidator).validatePostPublicStatus(isPublic);
            verify(postDomainRepository).save(testPost);
        }

        @Test
        @DisplayName("존재하지 않는 게시물 가시성 수정 시 예외 발생")
        void updateVisibilityOfNonExistentPost() {
            PostId postId = PostId.of("nonexistent");
            String userId = "user123";

            when(postDomainRepository.findById(postId.value())).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    postDomainService.updatePostVisibility(postId, userId, false))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.POST_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("게시물 삭제")
    class DeletePost {

        @Test
        @DisplayName("게시물 삭제 성공")
        void deletePostSuccess() {
            PostId postId = PostId.of("post123");
            String userId = "user123";

            when(postDomainRepository.findById(postId.value())).thenReturn(Optional.of(testPost));

            postDomainService.deletePost(postId, userId);

            verify(postValidator).validatePostAccess(testPost, userId);
            verify(postDomainRepository).save(testPost);
        }

        @Test
        @DisplayName("존재하지 않는 게시물 삭제 시 예외 발생")
        void deleteNonExistentPost() {
            PostId postId = PostId.of("nonexistent");
            String userId = "user123";

            when(postDomainRepository.findById(postId.value())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> postDomainService.deletePost(postId, userId))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.POST_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("조회수 증가")
    class IncrementViewCount {

        @Test
        @DisplayName("조회수 증가 성공")
        void incrementViewCountSuccess() {
            PostId postId = PostId.of("post123");

            when(postDomainRepository.findById(postId.value())).thenReturn(Optional.of(testPost));
            when(postDomainRepository.save(any(Post.class))).thenReturn(testPost);

            Post result = postDomainService.incrementViewCount(postId);

            assertThat(result).isNotNull();
            verify(postDomainRepository).save(testPost);
        }

        @Test
        @DisplayName("존재하지 않는 게시물 조회수 증가 시 예외 발생")
        void incrementViewCountOfNonExistentPost() {
            PostId postId = PostId.of("nonexistent");

            when(postDomainRepository.findById(postId.value())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> postDomainService.incrementViewCount(postId))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.POST_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("댓글 수 관리")
    class CommentCountManagement {

        @Test
        @DisplayName("댓글 수 증가 성공")
        void incrementCommentCountSuccess() {
            PostId postId = PostId.of("post123");

            when(postDomainRepository.findById(postId.value())).thenReturn(Optional.of(testPost));
            when(postDomainRepository.save(any(Post.class))).thenReturn(testPost);

            Post result = postDomainService.incrementCommentCount(postId);

            assertThat(result).isNotNull();
            verify(postDomainRepository).save(testPost);
        }

        @Test
        @DisplayName("댓글 수 감소 성공")
        void decrementCommentCountSuccess() {
            PostId postId = PostId.of("post123");

            when(postDomainRepository.findById(postId.value())).thenReturn(Optional.of(testPost));
            when(postDomainRepository.save(any(Post.class))).thenReturn(testPost);

            Post result = postDomainService.decrementCommentCount(postId);

            assertThat(result).isNotNull();
            verify(postDomainRepository).save(testPost);
        }

        @Test
        @DisplayName("존재하지 않는 게시물 댓글 수 증가 시 예외 발생")
        void incrementCommentCountOfNonExistentPost() {
            PostId postId = PostId.of("nonexistent");

            when(postDomainRepository.findById(postId.value())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> postDomainService.incrementCommentCount(postId))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.POST_NOT_FOUND);
        }

        @Test
        @DisplayName("존재하지 않는 게시물 댓글 수 감소 시 예외 발생")
        void decrementCommentCountOfNonExistentPost() {
            PostId postId = PostId.of("nonexistent");

            when(postDomainRepository.findById(postId.value())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> postDomainService.decrementCommentCount(postId))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.POST_NOT_FOUND);
        }
    }

    private Post createTestPost() {
        return new Post(
                PostId.of("post123"),
                new PostUserInfo("user123"),
                PostInfo.of("Test Title", "Test Content", "US", "CA"),
                Categories.QNA,
                PublicStatus.PUBLIC,
                null,
                CommentCount.zero(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}