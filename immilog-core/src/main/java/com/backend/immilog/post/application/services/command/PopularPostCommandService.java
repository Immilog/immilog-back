package com.backend.immilog.post.application.services.command;

import com.backend.immilog.post.application.dto.out.PostResult;
import com.backend.immilog.post.domain.repositories.PopularPostRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class PopularPostCommandService {
    
    private final PopularPostRepository popularPostRepository;
    
    public PopularPostCommandService(PopularPostRepository popularPostRepository) {
        this.popularPostRepository = popularPostRepository;
    }
    
    @Transactional
    public void saveMostViewedPosts(
            List<PostResult> posts,
            Integer expiration
    ) throws JsonProcessingException {
        validatePostsForSaving(posts, "most viewed");
        popularPostRepository.saveMostViewedPosts(posts, expiration);
        log.info("Successfully saved {} most viewed posts", posts.size());
    }
    
    @Transactional
    public void saveHotPosts(
            List<PostResult> posts,
            Integer expiration
    ) throws JsonProcessingException {
        validatePostsForSaving(posts, "hot");
        popularPostRepository.saveHotPosts(posts, expiration);
        log.info("Successfully saved {} hot posts", posts.size());
    }
    
    private void validatePostsForSaving(List<PostResult> posts, String type) {
        if (posts == null || posts.isEmpty()) {
            log.warn("[POPULAR POST AGGREGATION FAILED] Failed to save {} posts - empty list", type);
            throw new IllegalArgumentException("Posts list cannot be null or empty");
        }
        
        if (posts.stream().anyMatch(post -> post == null)) {
            log.warn("[POPULAR POST AGGREGATION FAILED] Failed to save {} posts - contains null posts", type);
            throw new IllegalArgumentException("Posts list cannot contain null elements");
        }
    }
}