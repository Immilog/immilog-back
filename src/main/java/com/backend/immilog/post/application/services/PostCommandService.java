package com.backend.immilog.post.application.services;

import com.backend.immilog.post.application.result.PostResult;
import com.backend.immilog.post.domain.model.post.Post;
import com.backend.immilog.post.domain.repositories.PopularPostRepository;
import com.backend.immilog.post.domain.repositories.PostDomainRepository;
import com.backend.immilog.post.domain.service.PostValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class PostCommandService {
    private final PostDomainRepository postDomainRepository;
    private final PopularPostRepository popularPostRepository;
    private final PostValidator postValidator;

    public PostCommandService(
            PostDomainRepository postDomainRepository,
            PopularPostRepository popularPostRepository,
            PostValidator postValidator
    ) {
        this.postDomainRepository = postDomainRepository;
        this.popularPostRepository = popularPostRepository;
        this.postValidator = postValidator;
    }

    @Transactional
    public Post save(Post post) {
        return postDomainRepository.save(post);
    }

    @Transactional
    public Post updatePost(
            String postId,
            String userId,
            String title,
            String content
    ) {
        var post = postDomainRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        postValidator.validatePostAccess(post, userId);
        postValidator.validatePostUpdate(post, title, content);

        post.updateTitle(title);
        post.updateContent(content);

        return postDomainRepository.save(post);
    }

    @Transactional
    public Post updatePostVisibility(
            String postId,
            String userId,
            Boolean isPublic
    ) {
        var post = postDomainRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        postValidator.validatePostAccess(post, userId);
        postValidator.validatePostPublicStatus(isPublic);

        post.updateIsPublic(isPublic);

        return postDomainRepository.save(post);
    }

    @Transactional
    public void deletePost(
            String postId,
            String userId
    ) {
        var post = postDomainRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        postValidator.validatePostAccess(post, userId);

        post.delete();
        postDomainRepository.save(post);
    }

    @Transactional
    public void saveMostViewedPosts(
            List<PostResult> posts,
            Integer expiration
    ) throws JsonProcessingException {
        if (posts.isEmpty()) {
            log.warn("[POPULAR POST AGGREGATION FAILED] Failed to save most viewed posts");
            return;
        }
        popularPostRepository.saveMostViewedPosts(posts, expiration);
    }

    @Transactional
    public void saveHotPosts(
            List<PostResult> popularPosts,
            int expiration
    ) throws JsonProcessingException {
        if (popularPosts.isEmpty()) {
            log.warn("[POPULAR POST AGGREGATION FAILED] Failed to save hot posts");
            return;
        }
        popularPostRepository.saveHotPosts(popularPosts, expiration);
    }

}
