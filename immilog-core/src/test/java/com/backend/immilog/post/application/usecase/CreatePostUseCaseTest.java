package com.backend.immilog.post.application.usecase;

import com.backend.immilog.post.application.dto.in.PostUploadCommand;
import com.backend.immilog.post.application.services.UserValidationService;
import com.backend.immilog.post.application.services.command.BulkCommandService;
import com.backend.immilog.post.domain.service.PostDomainService;
import com.backend.immilog.post.domain.model.post.*;
import com.backend.immilog.post.exception.PostErrorCode;
import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.shared.domain.service.UserDataProvider;
import com.backend.immilog.shared.domain.model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UploadPostUseCase")
class CreatePostUseCaseTest {

    @Mock
    private PostDomainService postDomainService;

    @Mock
    private UserValidationService userValidationService;

    @Mock
    private BulkCommandService bulkCommandService;

    @Mock
    private UserDataProvider userDataProvider;

    @InjectMocks
    private UploadPostUseCase.UploaderPost uploadPostUseCase;

    private PostUploadCommand testCommand;
    private Post savedPost;

    @BeforeEach
    void setUp() {
        testCommand = createTestCommand();
        savedPost = createTestPost();
    }

    @Nested
    @DisplayName("게시물 생성 - 성공 케이스")
    class SuccessfulCreation {

        @Test
        @DisplayName("태그와 첨부파일이 있는 게시물 생성 성공")
        void executeWithTagsAndAttachments() {
            PostUploadCommand commandWithResources = new PostUploadCommand(
                    "Test Title",
                    "Test Content", 
                    List.of("tag1", "tag2"),
                    List.of("file1.jpg", "file2.pdf"),
                    true,
                    Categories.QNA
            );

            when(userValidationService.validateUser("user123")).thenReturn(true);
            when(userDataProvider.getUserData("user123")).thenReturn(new UserData("user123", "testUser", "profile.jpg", "KR", "Seoul"));
            when(postDomainService.createPost(any(Post.class))).thenReturn(savedPost);

            assertThatNoException()
                    .isThrownBy(() -> uploadPostUseCase.uploadPost("user123", commandWithResources));

            verify(userValidationService).validateUser("user123");
            verify(postDomainService).createPost(any(Post.class));
            verify(bulkCommandService).saveAll(anyList(), anyString(), any());
        }

        @Test
        @DisplayName("태그만 있는 게시물 생성 성공")
        void executeWithTagsOnly() {
            PostUploadCommand commandWithTags = new PostUploadCommand(
                    "Test Title",
                    "Test Content",
                    List.of("tag1", "tag2"),
                    null,
                    true,
                    Categories.QNA
            );

            when(userValidationService.validateUser("user123")).thenReturn(true);
            when(userDataProvider.getUserData("user123")).thenReturn(new UserData("user123", "testUser", "profile.jpg", "KR", "Seoul"));
            when(postDomainService.createPost(any(Post.class))).thenReturn(savedPost);

            assertThatNoException()
                    .isThrownBy(() -> uploadPostUseCase.uploadPost("user123", commandWithTags));

            verify(bulkCommandService).saveAll(anyList(), anyString(), any());
        }

        @Test
        @DisplayName("첨부파일만 있는 게시물 생성 성공")
        void executeWithAttachmentsOnly() {
            PostUploadCommand commandWithAttachments = new PostUploadCommand(
                    "Test Title",
                    "Test Content",
                    null,
                    List.of("file1.jpg"),
                    true,
                    Categories.QNA
            );

            when(userValidationService.validateUser("user123")).thenReturn(true);
            when(userDataProvider.getUserData("user123")).thenReturn(new UserData("user123", "testUser", "profile.jpg", "KR", "Seoul"));
            when(postDomainService.createPost(any(Post.class))).thenReturn(savedPost);

            assertThatNoException()
                    .isThrownBy(() -> uploadPostUseCase.uploadPost("user123", commandWithAttachments));

            verify(bulkCommandService).saveAll(anyList(), anyString(), any());
        }

        @Test
        @DisplayName("리소스 없는 게시물 생성 성공")
        void executeWithoutResources() {
            when(userValidationService.validateUser("user123")).thenReturn(true);
            when(userDataProvider.getUserData("user123")).thenReturn(new UserData("user123", "testUser", "profile.jpg", "KR", "Seoul"));
            when(postDomainService.createPost(any(Post.class))).thenReturn(savedPost);

            assertThatNoException()
                    .isThrownBy(() -> uploadPostUseCase.uploadPost("user123", testCommand));

            verify(userValidationService).validateUser("user123");
            verify(postDomainService).createPost(any(Post.class));
            verify(bulkCommandService, never()).saveContentResources(anyList());
        }

        @Test
        @DisplayName("비공개 게시물 생성 성공")
        void executePrivatePost() {
            PostUploadCommand privateCommand = new PostUploadCommand(
                    "Private Title",
                    "Private Content",
                    null,
                    null,
                    false,
                    Categories.COMMUNICATION
            );

            when(userValidationService.validateUser("user123")).thenReturn(true);
            when(userDataProvider.getUserData("user123")).thenReturn(new UserData("user123", "testUser", "profile.jpg", "KR", "Seoul"));
            when(postDomainService.createPost(any(Post.class))).thenReturn(savedPost);

            assertThatNoException()
                    .isThrownBy(() -> uploadPostUseCase.uploadPost("user123", privateCommand));

            verify(postDomainService).createPost(any(Post.class));
        }
    }

    @Nested
    @DisplayName("게시물 생성 - 검증 실패")
    class ValidationFailure {

        @Test
        @DisplayName("유효하지 않은 사용자로 생성 시 예외 발생")
        void executeWithInvalidUser() {
            when(userValidationService.validateUser("invalidUser")).thenReturn(false);

            assertThatThrownBy(() -> uploadPostUseCase.uploadPost("invalidUser", testCommand))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.INVALID_USER);

            verify(postDomainService, never()).createPost(any());
            verify(bulkCommandService, never()).saveContentResources(anyList());
        }
    }

    @Nested
    @DisplayName("게시물 생성 - 리소스 처리")
    class ResourceHandling {

        @Test
        @DisplayName("빈 태그는 필터링됨")
        void executeWithEmptyTags() {
            PostUploadCommand commandWithEmptyTags = new PostUploadCommand(
                    "Test Title",
                    "Test Content",
                    Arrays.asList("tag1", "", "   ", "tag2", null),
                    null,
                    true,
                    Categories.QNA
            );

            when(userValidationService.validateUser("user123")).thenReturn(true);
            when(userDataProvider.getUserData("user123")).thenReturn(new UserData("user123", "testUser", "profile.jpg", "KR", "Seoul"));
            when(postDomainService.createPost(any(Post.class))).thenReturn(savedPost);

            assertThatNoException()
                    .isThrownBy(() -> uploadPostUseCase.uploadPost("user123", commandWithEmptyTags));

            verify(bulkCommandService).saveAll(anyList(), anyString(), any());
        }

        @Test
        @DisplayName("빈 첨부파일은 필터링됨")
        void executeWithEmptyAttachments() {
            PostUploadCommand commandWithEmptyAttachments = new PostUploadCommand(
                    "Test Title",
                    "Test Content",
                    null,
                    Arrays.asList("file1.jpg", "", "   ", "file2.pdf", null),
                    true,
                    Categories.QNA
            );

            when(userValidationService.validateUser("user123")).thenReturn(true);
            when(userDataProvider.getUserData("user123")).thenReturn(new UserData("user123", "testUser", "profile.jpg", "KR", "Seoul"));
            when(postDomainService.createPost(any(Post.class))).thenReturn(savedPost);

            assertThatNoException()
                    .isThrownBy(() -> uploadPostUseCase.uploadPost("user123", commandWithEmptyAttachments));

            verify(bulkCommandService).saveAll(anyList(), anyString(), any());
        }

        @Test
        @DisplayName("빈 리소스 리스트는 저장하지 않음")
        void executeWithEmptyResourceLists() {
            PostUploadCommand commandWithEmptyLists = new PostUploadCommand(
                    "Test Title",
                    "Test Content",
                    List.of("", "   "),
                    List.of("", "   "),
                    true,
                    Categories.QNA
            );

            when(userValidationService.validateUser("user123")).thenReturn(true);
            when(userDataProvider.getUserData("user123")).thenReturn(new UserData("user123", "testUser", "profile.jpg", "KR", "Seoul"));
            when(postDomainService.createPost(any(Post.class))).thenReturn(savedPost);

            assertThatNoException()
                    .isThrownBy(() -> uploadPostUseCase.uploadPost("user123", commandWithEmptyLists));

            verify(bulkCommandService, never()).saveContentResources(anyList());
        }
    }

    @Nested
    @DisplayName("다양한 카테고리 테스트")
    class CategoryTests {

        @Test
        @DisplayName("각 카테고리별 게시물 생성 성공")
        void executeWithDifferentCategories() {
            Categories[] categories = Categories.values();
            
            for (Categories category : categories) {
                if (category == Categories.ALL) continue;
                
                PostUploadCommand command = new PostUploadCommand(
                        "Title for " + category,
                        "Content for " + category,
                        null,
                        null,
                        true,
                        category
                );

                when(userValidationService.validateUser("user123")).thenReturn(true);
                when(userDataProvider.getUserData("user123")).thenReturn(new UserData("user123", "testUser", "profile.jpg", "KR", "Seoul"));
                when(postDomainService.createPost(any(Post.class))).thenReturn(savedPost);

                assertThatNoException()
                        .isThrownBy(() -> uploadPostUseCase.uploadPost("user123", command));
            }
        }
    }

    private PostUploadCommand createTestCommand() {
        return new PostUploadCommand(
                "Test Title",
                "Test Content",
                null,
                null,
                true,
                Categories.QNA
        );
    }

    private Post createTestPost() {
        return new Post(
                PostId.generate(),
                new PostUserInfo("user123"),
                PostInfo.of("Test Title", "Test Content", null, null),
                Categories.QNA,
                PublicStatus.PUBLIC,
                null,
                CommentCount.zero(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}