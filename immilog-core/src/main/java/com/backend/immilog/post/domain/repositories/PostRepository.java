package com.backend.immilog.post.domain.repositories;

import com.backend.immilog.post.domain.model.post.Badge;
import com.backend.immilog.post.domain.model.post.Categories;
import com.backend.immilog.post.domain.model.post.Post;
import com.backend.immilog.post.domain.model.post.SortingMethods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    Page<Post> getPosts(
            String countryId,
            SortingMethods sortingMethod,
            String isPublic,
            Categories category,
            Pageable pageable
    );

    Post getPostDetail(String postId);

    Page<Post> getPostsByKeyword(
            String keyword,
            Pageable pageable
    );

    Page<Post> getPostsByUserId(
            String userId,
            Pageable pageable
    );

    Post getById(String postId);

    Post save(Post postEntity);

    List<Post> getPostsByPostIdList(List<String> postIdList);
    
    Optional<Post> findById(String postId);
    
    List<Post> findByBadge(Badge badge);
}