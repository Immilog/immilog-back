package com.backend.immilog.post.application.services;

import com.backend.immilog.post.domain.model.post.PostType;
import com.backend.immilog.post.domain.model.resource.ResourceType;
import com.backend.immilog.post.domain.repositories.PostResourceRepository;
import com.backend.immilog.shared.aop.annotation.PerformanceMonitor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostResourceCommandService {
    private final PostResourceRepository postResourceRepository;

    public PostResourceCommandService(PostResourceRepository postResourceRepository) {
        this.postResourceRepository = postResourceRepository;
    }

    @PerformanceMonitor
    @Transactional
    public void deleteAllEntities(
            String postId,
            PostType postType,
            ResourceType resourceType,
            List<String> deleteResources
    ) {
        postResourceRepository.deleteAllEntities(
                postId,
                postType,
                resourceType,
                deleteResources
        );
    }

    @Transactional
    public void deleteAllByPostId(String id) {
        postResourceRepository.deleteAllByPostId(id);
    }
}
