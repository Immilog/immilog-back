package com.backend.immilog.post.application.services.query;

import com.backend.immilog.post.application.dto.out.PostResult;
import com.backend.immilog.post.application.mapper.PostResultAssembler;
import com.backend.immilog.post.application.mapper.PostResultConverter;
import com.backend.immilog.post.application.services.PostCommentDataService;
import com.backend.immilog.post.domain.model.post.*;
import com.backend.immilog.post.domain.repositories.ContentResourceRepository;
import com.backend.immilog.post.domain.repositories.PostDomainRepository;
import com.backend.immilog.post.domain.service.PostScoreCalculator;
import com.backend.immilog.post.exception.PostErrorCode;
import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.shared.enums.ContentType;
import com.backend.immilog.shared.infrastructure.DataRepository;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostQueryService")
class PostQueryServiceTest {

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private PostDomainRepository postDomainRepository;
    @Mock
    private DataRepository redisDataRepository;
    @Mock
    private ContentResourceRepository contentResourceRepository;
    @Mock
    private PostResultAssembler postResultAssembler;
    @Mock
    private EventResultStorageService eventResultStorageService;
    @Mock
    private PostCommentDataService postCommentDataService;
    @Mock
    private PostResultConverter postResultConverter;
    @Mock
    private PostScoreCalculator postScoreCalculator;

    @InjectMocks
    private PostQueryService postQueryService;

    private Post testPost;
    private PostResult testPostResult;
    private Pageable testPageable;

    @BeforeEach
    void setUp() {
        testPost = createTestPost();
        testPostResult = createTestPostResult();
        testPageable = PageRequest.of(0, 10);
    }

    @Nested
    @DisplayName("게시물 단건 조회")
    class GetSinglePost {

        @Test
        @DisplayName("게시물 ID로 조회 성공")
        void getPostByIdSuccess() {
            when(postDomainRepository.findById("post123")).thenReturn(Optional.of(testPost));

            Post result = postQueryService.getPostById("post123");

            assertThat(result).isEqualTo(testPost);
            verify(postDomainRepository).findById("post123");
        }

        @Test
        @DisplayName("존재하지 않는 게시물 조회 시 예외 발생")
        void getPostByIdNotFound() {
            when(postDomainRepository.findById("nonexistent")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> postQueryService.getPostById("nonexistent"))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.POST_NOT_FOUND);
        }

        @Test
        @DisplayName("게시물 Optional 조회 성공")
        void getPostByIdOptionalSuccess() {
            when(postDomainRepository.findById("post123")).thenReturn(Optional.of(testPost));

            Optional<Post> result = postQueryService.getPostByIdOptional("post123");

            assertThat(result).isPresent().contains(testPost);
        }

        @Test
        @DisplayName("게시물 Optional 조회 실패")
        void getPostByIdOptionalNotFound() {
            when(postDomainRepository.findById("nonexistent")).thenReturn(Optional.empty());

            Optional<Post> result = postQueryService.getPostByIdOptional("nonexistent");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("게시물 목록 조회")
    class GetPostsList {

        @Test
        @DisplayName("일반 게시물 목록 조회 성공")
        void getPostsSuccess() {
            Page<Post> postsPage = new PageImpl<>(List.of(testPost), testPageable, 1);
            Page<PostResult> postResultsPage = new PageImpl<>(List.of(testPostResult), testPageable, 1);

            when(postDomainRepository.findPosts("KR", SortingMethods.CREATED_DATE, "Y", Categories.QNA, testPageable))
                    .thenReturn(postsPage);
            when(postResultConverter.convertToPostResult(testPost)).thenReturn(testPostResult);
            mockAssemblePostResult(postResultsPage);

            Page<PostResult> result = postQueryService.getPosts("KR", SortingMethods.CREATED_DATE, "Y", Categories.QNA, testPageable);

            assertThat(result.getContent()).hasSize(1);
            verify(postDomainRepository).findPosts("KR", SortingMethods.CREATED_DATE, "Y", Categories.QNA, testPageable);
        }

        @Test
        @DisplayName("키워드 검색 성공")
        void getPostsByKeywordSuccess() {
            Page<Post> postsPage = new PageImpl<>(List.of(testPost), testPageable, 1);
            Page<PostResult> postResultsPage = new PageImpl<>(List.of(testPostResult), testPageable, 1);

            when(postDomainRepository.findPostsByKeyword("java", testPageable)).thenReturn(postsPage);
            when(postResultConverter.convertToPostResult(testPost)).thenReturn(testPostResult);
            when(postResultAssembler.assembleKeywords(testPostResult, "java")).thenReturn(testPostResult);
            mockAssemblePostResult(postResultsPage);

            Page<PostResult> result = postQueryService.getPostsByKeyword("java", testPageable);

            assertThat(result.getContent()).hasSize(1);
            verify(postResultAssembler).assembleKeywords(testPostResult, "java");
        }

        @Test
        @DisplayName("사용자별 게시물 조회 성공")
        void getPostsByUserIdSuccess() {
            Page<Post> postsPage = new PageImpl<>(List.of(testPost), testPageable, 1);
            Page<PostResult> postResultsPage = new PageImpl<>(List.of(testPostResult), testPageable, 1);

            when(postDomainRepository.findPostsByUserId("user123", testPageable)).thenReturn(postsPage);
            when(postResultConverter.convertToPostResult(testPost)).thenReturn(testPostResult);
            mockAssemblePostResult(postResultsPage);

            Page<PostResult> result = postQueryService.getPostsByUserId("user123", testPageable);

            assertThat(result.getContent()).hasSize(1);
            verify(postDomainRepository).findPostsByUserId("user123", testPageable);
        }
    }

    @Nested
    @DisplayName("게시물 상세 조회")
    class GetPostDetail {

        @Test
        @DisplayName("게시물 상세 조회 성공")
        void getPostDetailSuccess() {
            Page<PostResult> postResultsPage = new PageImpl<>(List.of(testPostResult), testPageable, 1);

            when(postDomainRepository.findById("post123")).thenReturn(Optional.of(testPost));
            when(postResultConverter.convertToPostResult(testPost)).thenReturn(testPostResult);
            mockAssemblePostResult(postResultsPage);

            PostResult result = postQueryService.getPostDetail("post123");

            assertThat(result).isEqualTo(testPostResult);
        }

        @Test
        @DisplayName("게시물 상세 조회 실패")
        void getPostDetailNotFound() {
            when(postDomainRepository.findById("nonexistent")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> postQueryService.getPostDetail("nonexistent"))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.POST_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Redis 데이터 조회")
    class RedisQueries {

        @Test
        @DisplayName("Redis에서 게시물 조회 성공")
        void getPostsFromRedisSuccess() throws JsonProcessingException {
            List<PostResult> expectedResults = List.of(testPostResult);
            String jsonData = "[{\"postId\":\"post123\"}]";

            when(redisDataRepository.findByKey("popular_posts")).thenReturn(jsonData);
            when(objectMapper.readValue(eq(jsonData), any(TypeReference.class))).thenReturn(expectedResults);

            List<PostResult> result = postQueryService.getPostsFromRedis("popular_posts");

            assertThat(result).hasSize(1).contains(testPostResult);
        }

        @Test
        @DisplayName("Redis 키가 존재하지 않을 때")
        void getPostsFromRedisKeyNotFound() {
            when(redisDataRepository.findByKey("nonexistent_key")).thenReturn(null);

            List<PostResult> result = postQueryService.getPostsFromRedis("nonexistent_key");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Redis JSON 파싱 실패")
        void getPostsFromRedisJsonParsingFailure() throws JsonProcessingException {
            String invalidJson = "invalid json";
            when(redisDataRepository.findByKey("invalid_key")).thenReturn(invalidJson);
            when(objectMapper.readValue(eq(invalidJson), any(TypeReference.class)))
                    .thenThrow(new JsonProcessingException("Invalid JSON") {});

            List<PostResult> result = postQueryService.getPostsFromRedis("invalid_key");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("게시물 ID 리스트 조회")
    class GetPostsByIdList {

        @Test
        @DisplayName("게시물 ID 리스트로 조회 성공")
        void getPostsByPostIdListSuccess() {
            List<String> postIds = List.of("post1", "post2");
            List<Post> posts = List.of(testPost);
            Page<PostResult> postResultsPage = new PageImpl<>(List.of(testPostResult));

            when(postDomainRepository.findPostsByIdList(postIds)).thenReturn(posts);
            when(postResultConverter.convertToPostResult(testPost)).thenReturn(testPostResult);
            mockAssemblePostResult(postResultsPage);

            List<PostResult> result = postQueryService.getPostsByPostIdList(postIds);

            assertThat(result).hasSize(1);
            verify(postDomainRepository).findPostsByIdList(postIds);
        }

        @Test
        @DisplayName("빈 ID 리스트로 조회")
        void getPostsByEmptyIdList() {
            List<String> emptyIds = Collections.emptyList();
            List<Post> posts = Collections.emptyList();

            when(postDomainRepository.findPostsByIdList(emptyIds)).thenReturn(posts);

            List<PostResult> result = postQueryService.getPostsByPostIdList(emptyIds);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("배지별 게시물 조회")
    class GetPostsByBadge {

        @Test
        @DisplayName("배지로 게시물 조회 성공")
        void findByBadgeSuccess() {
            Badge badge = Badge.HOT;
            List<Post> posts = List.of(testPost);

            when(postDomainRepository.findByBadge(badge)).thenReturn(posts);

            List<Post> result = postQueryService.findByBadge(badge);

            assertThat(result).hasSize(1).contains(testPost);
            verify(postDomainRepository).findByBadge(badge);
        }

        @Test
        @DisplayName("null 배지로 조회 시 빈 리스트 반환")
        void findByNullBadge() {
            List<Post> result = postQueryService.findByBadge(null);

            assertThat(result).isEmpty();
            verify(postDomainRepository, never()).findByBadge(any());
        }

        @Test
        @DisplayName("배지에 해당하는 게시물이 없는 경우")
        void findByBadgeNoResults() {
            Badge badge = Badge.HOT;
            when(postDomainRepository.findByBadge(badge)).thenReturn(Collections.emptyList());

            List<Post> result = postQueryService.findByBadge(badge);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("주간 베스트 게시물")
    class WeeklyBestPosts {

        @Test
        @DisplayName("주간 베스트 게시물 조회 성공")
        void getWeeklyBestPostsSuccess() {
            LocalDateTime from = LocalDateTime.now().minusDays(7);
            LocalDateTime to = LocalDateTime.now();
            List<Post> posts = List.of(testPost);
            Page<PostResult> postResultsPage = new PageImpl<>(List.of(testPostResult));

            when(postDomainRepository.findPostsInPeriod(from, to)).thenReturn(posts);
            when(postResultConverter.convertToPostResult(testPost)).thenReturn(testPostResult);
            when(postScoreCalculator.calculate(testPostResult)).thenReturn(100.0);
            mockAssemblePostResult(postResultsPage);

            List<PostResult> result = postQueryService.getWeeklyBestPosts(from, to);

            assertThat(result).isNotNull();
            verify(postDomainRepository).findPostsInPeriod(from, to);
            verify(postScoreCalculator).calculate(testPostResult);
        }

        @Test
        @DisplayName("조건에 맞지 않는 게시물 제외")
        void getWeeklyBestPostsFilterConditions() {
            LocalDateTime from = LocalDateTime.now().minusDays(7);
            LocalDateTime to = LocalDateTime.now();
            Post lowViewPost = createLowEngagementPost();
            List<Post> posts = List.of(lowViewPost);

            when(postDomainRepository.findPostsInPeriod(from, to)).thenReturn(posts);

            List<PostResult> result = postQueryService.getWeeklyBestPosts(from, to);

            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("북마크 게시물 조회")
    class BookmarkedPosts {

        @Test
        @DisplayName("북마크된 게시물 조회 성공")
        void getBookmarkedPostsSuccess() {
            String userId = "user123";
            ContentType contentType = ContentType.POST;
            List<String> bookmarkedPostIds = List.of("post123");
            List<Post> posts = List.of(testPost);
            Page<PostResult> postResultsPage = new PageImpl<>(List.of(testPostResult));

            when(eventResultStorageService.generateRequestId("bookmark")).thenReturn("bookmark-req-123");
            when(eventResultStorageService.waitForBookmarkData(eq("bookmark-req-123"), any()))
                    .thenReturn(bookmarkedPostIds);
            when(postDomainRepository.findPostsByIdList(bookmarkedPostIds)).thenReturn(posts);
            when(postResultConverter.convertToPostResult(testPost)).thenReturn(testPostResult);
            mockAssemblePostResult(postResultsPage);

            List<PostResult> result = postQueryService.getBookmarkedPosts(userId, contentType);

            assertThat(result).hasSize(1);
            verify(eventResultStorageService).registerEventProcessing("bookmark-req-123");
        }

        @Test
        @DisplayName("북마크된 게시물이 없는 경우")
        void getBookmarkedPostsEmpty() {
            String userId = "user123";
            ContentType contentType = ContentType.POST;
            List<String> emptyBookmarks = Collections.emptyList();

            when(eventResultStorageService.generateRequestId("bookmark")).thenReturn("bookmark-req-123");
            when(eventResultStorageService.waitForBookmarkData(eq("bookmark-req-123"), any()))
                    .thenReturn(emptyBookmarks);

            List<PostResult> result = postQueryService.getBookmarkedPosts(userId, contentType);

            assertThat(result).isEmpty();
        }
    }

    private void mockAssemblePostResult(Page<PostResult> expectedResult) {
        when(eventResultStorageService.generateRequestId("user")).thenReturn("user-req-123");
        when(eventResultStorageService.generateRequestId("interaction")).thenReturn("interaction-req-123");
        when(eventResultStorageService.waitForUserData(eq("user-req-123"), any())).thenReturn(Collections.emptyList());
        when(eventResultStorageService.waitForInteractionData(eq("interaction-req-123"), any())).thenReturn(Collections.emptyList());
        when(postCommentDataService.getCommentData(anyList())).thenReturn(Collections.emptyList());
        when(contentResourceRepository.findAllByContentIdList(anyList(), any())).thenReturn(Collections.emptyList());
        when(postResultAssembler.assembleUserData(any(PostResult.class), isNull())).thenAnswer(invocation -> invocation.getArgument(0));
        when(postResultAssembler.assembleInteractionData(any(PostResult.class), anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(postResultAssembler.assembleResources(any(PostResult.class), anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(postResultAssembler.assembleLikeCount(any(PostResult.class), anyLong())).thenAnswer(invocation -> invocation.getArgument(0));
        when(postResultAssembler.assembleCommentCount(any(PostResult.class), anyLong())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private Post createTestPost() {
        return new Post(
                PostId.of("post123"),
                new PostUserInfo("user123"),
                PostInfo.of("Test Title", "Test Content", "KR", "Seoul"),
                Categories.QNA,
                PublicStatus.PUBLIC,
                null,
                CommentCount.of(5L),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private Post createLowEngagementPost() {
        Post post = new Post(
                PostId.of("post456"),
                new PostUserInfo("user123"),
                PostInfo.of("Low Engagement Post", "Content", "KR", "Seoul"),
                Categories.QNA,
                PublicStatus.PUBLIC,
                null,
                CommentCount.of(1L),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        for(int i = 0 ; i < 5 ; i++){
            post.increaseViewCount();
        }
        return post;
    }

    private PostResult createTestPostResult() {
        return PostResult.builder()
                .postId("post123")
                .title("Test Title")
                .content("Test Content")
                .userId("user123")
                .viewCount(100L)
                .likeCount(10L)
                .commentCount(5L)
                .build();
    }
}