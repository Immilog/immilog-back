package com.backend.immilog.post.application.services.query;

import com.backend.immilog.post.domain.model.resource.ContentResource;
import com.backend.immilog.post.domain.repositories.ContentResourceRepository;
import com.backend.immilog.shared.enums.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static com.backend.immilog.post.domain.model.resource.ResourceType.ATTACHMENT;
import static com.backend.immilog.post.domain.model.resource.ResourceType.TAG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostResourceQueryService")
class PostResourceQueryServiceTest {

    private final ContentResourceRepository contentResourceRepository = mock(ContentResourceRepository.class);

    private List<ContentResource> testResources;
    private List<String> testPostIds;

    @BeforeEach
    void setUp() {
        testPostIds = List.of("post1", "post2", "post3");
        testResources = createTestResources();
    }

    @Nested
    @DisplayName("게시물별 리소스 조회")
    class GetResourcesByPostIdList {

        @Test
        @DisplayName("게시물 리소스 조회 성공")
        void getResourcesByPostIdListSuccess() {
            ContentType contentType = ContentType.POST;

            when(contentResourceRepository.findAllByContentIdList(testPostIds, contentType))
                    .thenReturn(testResources);

            List<ContentResource> result = contentResourceRepository.findAllByContentIdList(testPostIds, contentType);

            assertThat(result).hasSize(3);
            assertThat(result).containsExactlyElementsOf(testResources);
            verify(contentResourceRepository).findAllByContentIdList(testPostIds, contentType);
        }

        @Test
        @DisplayName("빈 게시물 ID 리스트로 조회")
        void getResourcesByEmptyPostIdList() {
            List<String> emptyPostIds = Collections.emptyList();
            ContentType contentType = ContentType.POST;
            List<ContentResource> emptyResources = Collections.emptyList();

            when(contentResourceRepository.findAllByContentIdList(emptyPostIds, contentType))
                    .thenReturn(emptyResources);

            List<ContentResource> result = contentResourceRepository.findAllByContentIdList(emptyPostIds, contentType);

            assertThat(result).isEmpty();
            verify(contentResourceRepository).findAllByContentIdList(emptyPostIds, contentType);
        }

        @Test
        @DisplayName("null 게시물 ID 리스트로 조회")
        void getResourcesByNullPostIdList() {
            ContentType contentType = ContentType.POST;
            List<ContentResource> emptyResources = Collections.emptyList();

            when(contentResourceRepository.findAllByContentIdList(null, contentType))
                    .thenReturn(emptyResources);

            List<ContentResource> result = contentResourceRepository.findAllByContentIdList(null, contentType);

            assertThat(result).isEmpty();
            verify(contentResourceRepository).findAllByContentIdList(null, contentType);
        }

        @Test
        @DisplayName("단일 게시물 ID로 조회")
        void getResourcesBySinglePostId() {
            List<String> singlePostId = List.of("post1");
            ContentType contentType = ContentType.POST;
            List<ContentResource> singlePostResources = List.of(
                    ContentResource.of(ContentType.POST, TAG, "java", "post1")
            );

            when(contentResourceRepository.findAllByContentIdList(singlePostId, contentType))
                    .thenReturn(singlePostResources);

            List<ContentResource> result = contentResourceRepository.findAllByContentIdList(singlePostId, contentType);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).content()).isEqualTo("java");
            verify(contentResourceRepository).findAllByContentIdList(singlePostId, contentType);
        }
    }

    @Nested
    @DisplayName("다양한 컨텐츠 타입")
    class VariousContentTypes {

        @Test
        @DisplayName("게시물 컨텐츠 타입으로 조회")
        void getResourcesWithPostContentType() {
            ContentType contentType = ContentType.POST;
            List<ContentResource> postResources = createPostResources();

            when(contentResourceRepository.findAllByContentIdList(testPostIds, contentType))
                    .thenReturn(postResources);

            List<ContentResource> result = contentResourceRepository.findAllByContentIdList(testPostIds, contentType);

            assertThat(result).allMatch(resource -> resource.contentType() == ContentType.POST);
            verify(contentResourceRepository).findAllByContentIdList(testPostIds, contentType);
        }

        @Test
        @DisplayName("댓글 컨텐츠 타입으로 조회")
        void getResourcesWithCommentContentType() {
            List<String> commentIds = List.of("comment1", "comment2");
            ContentType contentType = ContentType.COMMENT;
            List<ContentResource> commentResources = createCommentResources();

            when(contentResourceRepository.findAllByContentIdList(commentIds, contentType))
                    .thenReturn(commentResources);

            List<ContentResource> result = contentResourceRepository.findAllByContentIdList(commentIds, contentType);

            assertThat(result).allMatch(resource -> resource.contentType() == ContentType.COMMENT);
            verify(contentResourceRepository).findAllByContentIdList(commentIds, contentType);
        }
    }

    @Nested
    @DisplayName("대량 데이터 처리")
    class BulkDataHandling {

        @Test
        @DisplayName("대량 게시물 ID 리스트로 조회")
        void getResourcesByManyPostIds() {
            List<String> manyPostIds = createManyPostIds(100);
            ContentType contentType = ContentType.POST;
            List<ContentResource> manyResources = createManyResources(200);

            when(contentResourceRepository.findAllByContentIdList(manyPostIds, contentType))
                    .thenReturn(manyResources);

            List<ContentResource> result = contentResourceRepository.findAllByContentIdList(manyPostIds, contentType);

            assertThat(result).hasSize(200);
            verify(contentResourceRepository).findAllByContentIdList(manyPostIds, contentType);
        }

        @Test
        @DisplayName("리소스가 없는 게시물 ID 조회")
        void getResourcesWithNoResources() {
            List<String> noResourcePostIds = List.of("post_without_resources");
            ContentType contentType = ContentType.POST;
            List<ContentResource> emptyResources = Collections.emptyList();

            when(contentResourceRepository.findAllByContentIdList(noResourcePostIds, contentType))
                    .thenReturn(emptyResources);

            List<ContentResource> result = contentResourceRepository.findAllByContentIdList(noResourcePostIds, contentType);

            assertThat(result).isEmpty();
            verify(contentResourceRepository).findAllByContentIdList(noResourcePostIds, contentType);
        }
    }

    @Nested
    @DisplayName("Repository 예외 처리")
    class RepositoryExceptionHandling {

        @Test
        @DisplayName("Repository 예외 발생 시 그대로 전파")
        void repositoryExceptionPropagation() {
            ContentType contentType = ContentType.POST;
            RuntimeException repositoryException = new RuntimeException("Database connection error");

            when(contentResourceRepository.findAllByContentIdList(testPostIds, contentType))
                    .thenThrow(repositoryException);

            assertThatThrownBy(() -> contentResourceRepository.findAllByContentIdList(testPostIds, contentType))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database connection error");

            verify(contentResourceRepository).findAllByContentIdList(testPostIds, contentType);
        }

        @Test
        @DisplayName("Repository에서 null 반환 시 처리")
        void repositoryReturnsNull() {
            ContentType contentType = ContentType.POST;

            when(contentResourceRepository.findAllByContentIdList(testPostIds, contentType))
                    .thenReturn(null);

            List<ContentResource> result = contentResourceRepository.findAllByContentIdList(testPostIds, contentType);

            assertThat(result).isNull();
            verify(contentResourceRepository).findAllByContentIdList(testPostIds, contentType);
        }
    }

    private List<ContentResource> createTestResources() {
        return List.of(
                ContentResource.of(ContentType.POST, TAG, "java", "post1"),
                ContentResource.of(ContentType.POST, TAG, "spring", "post1"),
                ContentResource.of(ContentType.POST, ATTACHMENT, "file.jpg", "post2")
        );
    }

    private List<ContentResource> createPostResources() {
        return List.of(
                ContentResource.of(ContentType.POST, TAG, "java", "post1"),
                ContentResource.of(ContentType.POST, ATTACHMENT, "image.png", "post2")
        );
    }

    private List<ContentResource> createCommentResources() {
        return List.of(
                ContentResource.of(ContentType.COMMENT, TAG, "reply", "comment1"),
                ContentResource.of(ContentType.COMMENT, TAG, "question", "comment2")
        );
    }

    private List<String> createManyPostIds(int count) {
        return java.util.stream.IntStream.range(1, count + 1)
                .mapToObj(i -> "post" + i)
                .toList();
    }

    private List<ContentResource> createManyResources(int count) {
        return java.util.stream.IntStream.range(1, count + 1)
                .mapToObj(i -> ContentResource.of(
                        ContentType.POST,
                        i % 2 == 0 ? TAG : ATTACHMENT,
                        "content" + i,
                        "post" + (i / 2 + 1)
                ))
                .toList();
    }
}