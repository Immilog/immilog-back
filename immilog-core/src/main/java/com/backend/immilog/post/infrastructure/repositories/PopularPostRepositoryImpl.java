package com.backend.immilog.post.infrastructure.repositories;

import com.backend.immilog.post.application.dto.out.PostResult;
import com.backend.immilog.post.domain.model.post.Post;
import com.backend.immilog.post.domain.model.post.SortingMethods;
import com.backend.immilog.post.domain.repositories.PopularPostRepository;
import com.backend.immilog.post.infrastructure.jdbc.PostJdbcRepository;
import com.backend.immilog.shared.infrastructure.DataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class PopularPostRepositoryImpl implements PopularPostRepository {
    private final PostJdbcRepository postJdbcRepository;
    private final DataRepository redisDataRepository;
    private final ObjectMapper objectMapper;

    public PopularPostRepositoryImpl(
            PostJdbcRepository postJdbcRepository,
            DataRepository redisDataRepository,
            ObjectMapper objectMapper
    ) {
        this.postJdbcRepository = postJdbcRepository;
        this.redisDataRepository = redisDataRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void saveMostViewedPosts(
            List<PostResult> posts,
            Integer expiration
    ) throws JsonProcessingException {
        var json = objectMapper.writeValueAsString(posts);
        var MOST_VIEWED_POSTS_KEY = "most_viewed_posts";
        redisDataRepository.save(MOST_VIEWED_POSTS_KEY, json, expiration);
    }

    @Override
    public void saveHotPosts(
            List<PostResult> popularPosts,
            int expiration
    ) throws JsonProcessingException {
        var json = objectMapper.writeValueAsString(popularPosts);
        var HOT_POSTS_KEY = "hot_posts";
        redisDataRepository.save(HOT_POSTS_KEY, json, expiration);
    }

    @Override
    public List<PostResult> getMostViewedPosts(
            LocalDateTime from,
            LocalDateTime to
    ) {
        var posts = postJdbcRepository.getPopularPosts(from, to, SortingMethods.VIEW_COUNT);
        return posts.stream()
                .filter(post -> post.viewCount() != null && post.viewCount() >= 50) // 최소 조회수 50회 이상
                .map(this::convertToPostResult)
                .limit(5) // 최대 5개까지
                .toList();
    }

    @Override
    public List<PostResult> getHotPosts(
            LocalDateTime from,
            LocalDateTime to
    ) {
        var posts = postJdbcRepository.getPopularPosts(from, to, SortingMethods.COMMENT_COUNT);
        return posts.stream()
                .filter(post -> post.commentCount() != null && post.commentCountValue() >= 5) // 최소 댓글 5개 이상
                .map(this::convertToPostResult)
                .limit(5) // 최대 5개까지
                .toList();
    }

    private PostResult convertToPostResult(Post post) {
        return new PostResult(
                post.id().value(),
                post.userId(),
                null, // 유저 프로필 이미지는 이벤트로 조회하여 나중에 설정됨
                null, // 유저 닉네임은 이벤트로 조회하여 나중에 설정됨
                post.commentCountValue(),
                post.viewCount(),
                0L,
                null,
                null,
                null,
                null,
                post.isPublicValue(),
                post.countryId(),
                post.region(),
                post.category(),
                post.status(),
                post.badge(),
                post.createdAt().toString(),
                post.updatedAt().toString(),
                post.title(),
                post.content(),
                null
        );
    }
}
