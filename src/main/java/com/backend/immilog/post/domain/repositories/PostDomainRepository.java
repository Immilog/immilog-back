package com.backend.immilog.post.domain.repositories;

import com.backend.immilog.post.domain.model.post.Categories;
import com.backend.immilog.post.domain.model.post.Post;
import com.backend.immilog.post.domain.model.post.SortingMethods;
import com.backend.immilog.shared.enums.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostDomainRepository {

    Optional<Post> findById(String id);

    Post save(Post post);

    Page<Post> findPosts(
            Country country,
            SortingMethods sortingMethod,
            String isPublic,
            Categories category,
            Pageable pageable
    );

    Page<Post> findPostsByKeyword(
            String keyword,
            Pageable pageable
    );

    Page<Post> findPostsByUserId(
            String userId,
            Pageable pageable
    );

    List<Post> findPostsByIdList(List<String> postIdList);
}