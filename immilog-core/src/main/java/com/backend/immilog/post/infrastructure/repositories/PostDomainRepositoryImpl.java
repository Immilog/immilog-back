package com.backend.immilog.post.infrastructure.repositories;

import com.backend.immilog.post.domain.model.post.Badge;
import com.backend.immilog.post.domain.model.post.Categories;
import com.backend.immilog.post.domain.model.post.Post;
import com.backend.immilog.post.domain.model.post.SortingMethods;
import com.backend.immilog.post.domain.repositories.PostDomainRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class PostDomainRepositoryImpl implements PostDomainRepository {

    private final PostRepositoryImpl postRepositoryImpl;

    public PostDomainRepositoryImpl(PostRepositoryImpl postRepositoryImpl) {
        this.postRepositoryImpl = postRepositoryImpl;
    }

    @Override
    public Optional<Post> findById(String id) {
        try {
            var post = postRepositoryImpl.getPostDetail(id);
            return Optional.of(post);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Post save(Post post) {
        return postRepositoryImpl.save(post);
    }

    @Override
    public Page<Post> findPosts(
            String countryId,
            SortingMethods sortingMethod,
            String isPublic,
            Categories category,
            Pageable pageable
    ) {
        return postRepositoryImpl.getPosts(
                countryId,
                sortingMethod,
                isPublic,
                category,
                pageable
        );
    }

    @Override
    public Page<Post> findPostsByKeyword(
            String keyword,
            Pageable pageable
    ) {
        return postRepositoryImpl.getPostsByKeyword(keyword, pageable);
    }

    @Override
    public Page<Post> findPostsByUserId(
            String userId,
            Pageable pageable
    ) {
        return postRepositoryImpl.getPostsByUserId(userId, pageable);
    }

    @Override
    public List<Post> findPostsByIdList(List<String> postIdList) {
        return postRepositoryImpl.getPostsByPostIdList(postIdList);
    }

    @Override
    public List<Post> findByBadge(Badge badge) {
        return postRepositoryImpl.findByBadge(badge);
    }

    @Override
    public List<Post> findPostsInPeriod(LocalDateTime from, LocalDateTime to) {
        return postRepositoryImpl.findPostsInPeriod(from, to);
    }
}