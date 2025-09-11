package com.backend.immilog.post.application.services.command;

import com.backend.immilog.post.domain.model.resource.ResourceType;
import com.backend.immilog.post.domain.repositories.ContentResourceRepository;
import com.backend.immilog.shared.aop.annotation.PerformanceMonitor;
import com.backend.immilog.shared.enums.ContentType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostResourceCommandService {
    private final ContentResourceRepository contentResourceRepository;

    public PostResourceCommandService(ContentResourceRepository contentResourceRepository) {
        this.contentResourceRepository = contentResourceRepository;
    }

    @PerformanceMonitor
    @Transactional
    public void deleteAllEntities(
            String postId,
            ContentType contentType,
            ResourceType resourceType,
            List<String> deleteResources
    ) {
        contentResourceRepository.deleteAllEntities(
                postId,
                contentType,
                resourceType,
                deleteResources
        );
    }

    @Transactional
    public void deleteAllByPostId(String id) {
        contentResourceRepository.deleteAllByContentId(id);
    }
}
