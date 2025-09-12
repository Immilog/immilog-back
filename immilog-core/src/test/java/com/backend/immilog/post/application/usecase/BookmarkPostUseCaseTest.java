package com.backend.immilog.post.application.usecase;

import com.backend.immilog.interaction.application.services.InteractionUserCommandService;
import com.backend.immilog.interaction.application.services.InteractionUserQueryService;
import com.backend.immilog.interaction.domain.model.InteractionStatus;
import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.post.domain.model.post.*;
import com.backend.immilog.post.domain.repositories.PostDomainRepository;
import com.backend.immilog.post.domain.service.PostValidator;
import com.backend.immilog.post.exception.PostErrorCode;
import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.shared.enums.ContentStatus;
import com.backend.immilog.shared.enums.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.backend.immilog.interaction.domain.model.InteractionType.BOOKMARK;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("BookmarkPostUseCase")
class BookmarkPostUseCaseTest {

    private final InteractionUserCommandService interactionUserCommandService = mock(InteractionUserCommandService.class);

    private final InteractionUserQueryService interactionUserQueryService = mock(InteractionUserQueryService.class);

    private final  PostDomainRepository postDomainRepository = mock(PostDomainRepository.class);

    private final PostValidator postValidator = mock(PostValidator.class);

    private final BookmarkPostUseCase bookmarkPostUseCase = new BookmarkPostUseCase.BookmarkPostUseCaseImpl(
            interactionUserCommandService,
            interactionUserQueryService,
            postDomainRepository,
            postValidator
    );

    private Post testPost;
    private InteractionUser existingBookmark;

    @BeforeEach
    void setUp() {
        testPost = createTestPost();
        existingBookmark = createTestBookmark("user123", "post123", InteractionStatus.ACTIVE);
    }

    @Nested
    @DisplayName("북마크 토글 - 입력 검증")
    class InputValidation {

        @Test
        @DisplayName("null 사용자 ID로 토글 시 예외 발생")
        void toggleBookmarkWithNullUserId() {
            assertThatThrownBy(() -> bookmarkPostUseCase.toggleBookmark(null, "post123"))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.INVALID_USER);
        }

        @Test
        @DisplayName("빈 사용자 ID로 토글 시 예외 발생")
        void toggleBookmarkWithBlankUserId() {
            assertThatThrownBy(() -> bookmarkPostUseCase.toggleBookmark("", "post123"))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.INVALID_USER);

            assertThatThrownBy(() -> bookmarkPostUseCase.toggleBookmark("   ", "post123"))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.INVALID_USER);
        }

        @Test
        @DisplayName("null 게시물 ID로 토글 시 예외 발생")
        void toggleBookmarkWithNullPostId() {
            assertThatThrownBy(() -> bookmarkPostUseCase.toggleBookmark("user123", null))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.INVALID_POST_DATA);
        }

        @Test
        @DisplayName("빈 게시물 ID로 토글 시 예외 발생")
        void toggleBookmarkWithBlankPostId() {
            assertThatThrownBy(() -> bookmarkPostUseCase.toggleBookmark("user123", ""))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.INVALID_POST_DATA);
        }
    }

    @Nested
    @DisplayName("북마크 토글 - 게시물 검증")
    class PostValidation {

        @Test
        @DisplayName("존재하지 않는 게시물 북마크 시 예외 발생")
        void toggleBookmarkOfNonExistentPost() {
            when(postDomainRepository.findById("nonexistent")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookmarkPostUseCase.toggleBookmark("user123", "nonexistent"))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.POST_NOT_FOUND);
        }

        @Test
        @DisplayName("삭제된 게시물 북마크 시 예외 발생")
        void toggleBookmarkOfDeletedPost() {
            Post deletedPost = createDeletedPost();
            when(postDomainRepository.findById("post123")).thenReturn(Optional.of(deletedPost));

            assertThatThrownBy(() -> bookmarkPostUseCase.toggleBookmark("user123", "post123"))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.POST_ALREADY_DELETED);
        }
    }

    @Nested
    @DisplayName("북마크 토글 - 새로운 북마크")
    class NewBookmark {

        @Test
        @DisplayName("새 북마크 생성 성공")
        void createNewBookmarkSuccess() {
            when(postDomainRepository.findById("post123")).thenReturn(Optional.of(testPost));
            when(interactionUserQueryService.findBookmarkInteraction("user123", "post123", ContentType.POST))
                    .thenReturn(Optional.empty());
            when(interactionUserCommandService.createInteraction(any(InteractionUser.class)))
                    .thenReturn(existingBookmark);

            boolean result = bookmarkPostUseCase.toggleBookmark("user123", "post123");

            assertThat(result).isTrue();
            verify(interactionUserCommandService).createInteraction(any(InteractionUser.class));
        }

        @Test
        @DisplayName("새 북마크 생성 실패 시 예외 발생")
        void createNewBookmarkFailure() {
            when(postDomainRepository.findById("post123")).thenReturn(Optional.of(testPost));
            when(interactionUserQueryService.findBookmarkInteraction("user123", "post123", ContentType.POST))
                    .thenReturn(Optional.empty());
            when(interactionUserCommandService.createInteraction(any(InteractionUser.class)))
                    .thenThrow(new RuntimeException("Database error"));

            assertThatThrownBy(() -> bookmarkPostUseCase.toggleBookmark("user123", "post123"))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.FAILED_TO_SAVE_POST);
        }
    }

    @Nested
    @DisplayName("북마크 토글 - 기존 북마크")
    class ExistingBookmark {

        @Test
        @DisplayName("활성 북마크를 비활성화")
        void deactivateActiveBookmark() {
            InteractionUser activeBookmark = createTestBookmark("user123", "post123", InteractionStatus.ACTIVE);
            when(postDomainRepository.findById("post123")).thenReturn(Optional.of(testPost));
            when(interactionUserQueryService.findBookmarkInteraction("user123", "post123", ContentType.POST))
                    .thenReturn(Optional.of(activeBookmark));
            when(interactionUserCommandService.deactivateInteraction(activeBookmark.id()))
                    .thenReturn(activeBookmark);

            boolean result = bookmarkPostUseCase.toggleBookmark("user123", "post123");

            assertThat(result).isFalse();
            verify(interactionUserCommandService).deactivateInteraction(activeBookmark.id());
            verify(interactionUserCommandService, never()).activateInteraction(any());
        }

        @Test
        @DisplayName("비활성 북마크를 활성화")
        void activateInactiveBookmark() {
            InteractionUser inactiveBookmark = createTestBookmark("user123", "post123", InteractionStatus.INACTIVE);
            when(postDomainRepository.findById("post123")).thenReturn(Optional.of(testPost));
            when(interactionUserQueryService.findBookmarkInteraction("user123", "post123", ContentType.POST))
                    .thenReturn(Optional.of(inactiveBookmark));
            when(interactionUserCommandService.activateInteraction(inactiveBookmark.id()))
                    .thenReturn(inactiveBookmark);

            boolean result = bookmarkPostUseCase.toggleBookmark("user123", "post123");

            assertThat(result).isTrue();
            verify(interactionUserCommandService).activateInteraction(inactiveBookmark.id());
            verify(interactionUserCommandService, never()).deactivateInteraction(any());
        }

        @Test
        @DisplayName("북마크 상태 토글 실패 시 예외 발생")
        void toggleBookmarkStatusFailure() {
            InteractionUser activeBookmark = createTestBookmark("user123", "post123", InteractionStatus.ACTIVE);
            when(postDomainRepository.findById("post123")).thenReturn(Optional.of(testPost));
            when(interactionUserQueryService.findBookmarkInteraction("user123", "post123", ContentType.POST))
                    .thenReturn(Optional.of(activeBookmark));
            when(interactionUserCommandService.deactivateInteraction(activeBookmark.id()))
                    .thenThrow(new RuntimeException("Database error"));

            assertThatThrownBy(() -> bookmarkPostUseCase.toggleBookmark("user123", "post123"))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.FAILED_TO_SAVE_POST);
        }
    }

    @Nested
    @DisplayName("북마크 조회 실패 처리")
    class BookmarkQueryFailure {

        @Test
        @DisplayName("북마크 조회 실패 시 새 북마크로 처리")
        void handleBookmarkQueryFailure() {
            when(postDomainRepository.findById("post123")).thenReturn(Optional.of(testPost));
            when(interactionUserQueryService.findBookmarkInteraction("user123", "post123", ContentType.POST))
                    .thenThrow(new RuntimeException("Query error"));
            when(interactionUserCommandService.createInteraction(any(InteractionUser.class)))
                    .thenReturn(existingBookmark);

            boolean result = bookmarkPostUseCase.toggleBookmark("user123", "post123");

            assertThat(result).isTrue();
            verify(interactionUserCommandService).createInteraction(any(InteractionUser.class));
        }
    }

    private Post createTestPost() {
        return new Post(
                PostId.of("post123"),
                new PostUserInfo("author123"),
                PostInfo.of("Test Title", "Test Content", "US", "CA"),
                Categories.QNA,
                PublicStatus.PUBLIC,
                null,
                CommentCount.zero(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private Post createDeletedPost() {
        Post post = createTestPost();
        post.delete();
        return post;
    }

    private InteractionUser createTestBookmark(String userId, String postId, InteractionStatus status) {
        return new InteractionUser(
                "bookmark123",
                userId,
                postId,
                ContentType.POST,
                BOOKMARK,
                status,
                LocalDateTime.now()
        );
    }
}