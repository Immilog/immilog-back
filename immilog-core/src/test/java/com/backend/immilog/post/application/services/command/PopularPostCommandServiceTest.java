package com.backend.immilog.post.application.services.command;

import com.backend.immilog.post.application.dto.out.PostResult;
import com.backend.immilog.post.domain.repositories.PopularPostRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@DisplayName("PopularPostCommandService")
class PopularPostCommandServiceTest {

    private final PopularPostRepository popularPostRepository = mock(PopularPostRepository.class);
    private final PopularPostCommandService popularPostCommandService = new PopularPostCommandService(popularPostRepository);

    private List<PostResult> testPosts;
    private Integer expiration;

    @BeforeEach
    void setUp() {
        testPosts = createTestPosts();
        expiration = 3600;
    }

    @Nested
    @DisplayName("인기 게시물 저장")
    class SaveMostViewedPosts {

        @Test
        @DisplayName("인기 게시물 저장 성공")
        void saveMostViewedPostsSuccess() throws JsonProcessingException {
            popularPostCommandService.saveMostViewedPosts(testPosts, expiration);

            verify(popularPostRepository).saveMostViewedPosts(testPosts, expiration);
        }

        @Test
        @DisplayName("빈 리스트로 저장 시도 시 예외 발생")
        void saveMostViewedPostsWithEmptyList() throws JsonProcessingException {
            List<PostResult> emptyList = Collections.emptyList();

            assertThatThrownBy(() -> popularPostCommandService.saveMostViewedPosts(emptyList, expiration))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Posts list cannot be null or empty");

            verify(popularPostRepository, never()).saveMostViewedPosts(any(), anyInt());
        }

        @Test
        @DisplayName("null 리스트로 저장 시도 시 예외 발생")
        void saveMostViewedPostsWithNullList() throws JsonProcessingException {
            assertThatThrownBy(() -> popularPostCommandService.saveMostViewedPosts(null, expiration))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Posts list cannot be null or empty");

            verify(popularPostRepository, never()).saveMostViewedPosts(any(), anyInt());
        }

        @Test
        @DisplayName("null 요소가 포함된 리스트로 저장 시도 시 예외 발생")
        void saveMostViewedPostsWithNullElements() throws JsonProcessingException {
            List<PostResult> postsWithNull = Arrays.asList(
                    createTestPostResult("post1"),
                    null,
                    createTestPostResult("post2")
            );

            assertThatThrownBy(() -> popularPostCommandService.saveMostViewedPosts(postsWithNull, expiration))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Posts list cannot contain null elements");

            verify(popularPostRepository, never()).saveMostViewedPosts(any(), anyInt());
        }

        @Test
        @DisplayName("Repository에서 JsonProcessingException 발생 시 그대로 전파")
        void saveMostViewedPostsWithJsonProcessingException() throws JsonProcessingException {
            JsonProcessingException exception = mock(JsonProcessingException.class);
            doThrow(exception).when(popularPostRepository).saveMostViewedPosts(testPosts, expiration);

            assertThatThrownBy(() -> popularPostCommandService.saveMostViewedPosts(testPosts, expiration))
                    .isSameAs(exception);
        }

        @Test
        @DisplayName("다양한 만료 시간으로 저장 성공")
        void saveMostViewedPostsWithDifferentExpirations() throws JsonProcessingException {
            Integer[] expirations = {60, 300, 3600, 86400};

            for (Integer exp : expirations) {
                popularPostCommandService.saveMostViewedPosts(testPosts, exp);
                verify(popularPostRepository).saveMostViewedPosts(testPosts, exp);
            }
        }
    }

    @Nested
    @DisplayName("핫 게시물 저장")
    class SaveHotPosts {

        @Test
        @DisplayName("핫 게시물 저장 성공")
        void saveHotPostsSuccess() throws JsonProcessingException {
            popularPostCommandService.saveHotPosts(testPosts, expiration);

            verify(popularPostRepository).saveHotPosts(testPosts, expiration);
        }

        @Test
        @DisplayName("빈 리스트로 저장 시도 시 예외 발생")
        void saveHotPostsWithEmptyList() throws JsonProcessingException {
            List<PostResult> emptyList = Collections.emptyList();

            assertThatThrownBy(() -> popularPostCommandService.saveHotPosts(emptyList, expiration))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Posts list cannot be null or empty");

            verify(popularPostRepository, never()).saveHotPosts(any(), anyInt());
        }

        @Test
        @DisplayName("null 리스트로 저장 시도 시 예외 발생")
        void saveHotPostsWithNullList() throws JsonProcessingException {
            assertThatThrownBy(() -> popularPostCommandService.saveHotPosts(null, expiration))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Posts list cannot be null or empty");

            verify(popularPostRepository, never()).saveHotPosts(any(), anyInt());
        }

        @Test
        @DisplayName("null 요소가 포함된 리스트로 저장 시도 시 예외 발생")
        void saveHotPostsWithNullElements() throws JsonProcessingException {
            List<PostResult> postsWithNull = Arrays.asList(
                    createTestPostResult("post1"),
                    null,
                    createTestPostResult("post2")
            );

            assertThatThrownBy(() -> popularPostCommandService.saveHotPosts(postsWithNull, expiration))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Posts list cannot contain null elements");

            verify(popularPostRepository, never()).saveHotPosts(any(), anyInt());
        }

        @Test
        @DisplayName("Repository에서 예외 발생 시 그대로 전파")
        void saveHotPostsWithRepositoryException() throws JsonProcessingException {
            RuntimeException exception = new RuntimeException("Repository error");
            doThrow(exception).when(popularPostRepository).saveHotPosts(testPosts, expiration);

            assertThatThrownBy(() -> popularPostCommandService.saveHotPosts(testPosts, expiration))
                    .isSameAs(exception);
        }

        @Test
        @DisplayName("단일 게시물로 핫 게시물 저장")
        void saveHotPostsWithSinglePost() throws JsonProcessingException {
            List<PostResult> singlePost = List.of(createTestPostResult("post1"));

            popularPostCommandService.saveHotPosts(singlePost, expiration);

            verify(popularPostRepository).saveHotPosts(singlePost, expiration);
        }
    }

    @Nested
    @DisplayName("다량의 게시물 저장")
    class BulkSave {

        @Test
        @DisplayName("대량의 인기 게시물 저장")
        void saveManyMostViewedPosts() throws JsonProcessingException {
            List<PostResult> manyPosts = createManyTestPosts(100);

            popularPostCommandService.saveMostViewedPosts(manyPosts, expiration);

            verify(popularPostRepository).saveMostViewedPosts(manyPosts, expiration);
        }

        @Test
        @DisplayName("대량의 핫 게시물 저장")
        void saveManyHotPosts() throws JsonProcessingException {
            List<PostResult> manyPosts = createManyTestPosts(50);

            popularPostCommandService.saveHotPosts(manyPosts, expiration);

            verify(popularPostRepository).saveHotPosts(manyPosts, expiration);
        }
    }

    private List<PostResult> createTestPosts() {
        return List.of(
                createTestPostResult("post1"),
                createTestPostResult("post2"),
                createTestPostResult("post3")
        );
    }

    private List<PostResult> createManyTestPosts(int count) {
        return java.util.stream.IntStream.range(1, count + 1)
                .mapToObj(i -> createTestPostResult("post" + i))
                .toList();
    }

    private PostResult createTestPostResult(String postId) {
        return PostResult.builder()
                .postId(postId)
                .title("Test Title " + postId)
                .content("Test Content " + postId)
                .userId("user123")
                .viewCount(100L)
                .likeCount(10L)
                .commentCount(5L)
                .build();
    }
}