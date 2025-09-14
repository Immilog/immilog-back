package com.backend.immilog.post.application.usecase;

import com.backend.immilog.post.domain.repositories.ContentResourceRepository;
import com.backend.immilog.post.domain.service.PostDomainService;
import com.backend.immilog.post.domain.model.post.PostId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public interface DeletePostUseCase {
    void deletePost(
            String userId,
            String postId
    );

    @Slf4j
    @Service
    @RequiredArgsConstructor
    class DeleterPost implements DeletePostUseCase {
        private final PostDomainService postDomainService;
        private final ContentResourceRepository contentResourceRepository;

        @Transactional
        public void deletePost(
                String userId,
                String postId
        ) {
            postDomainService.deletePost(PostId.of(postId), userId);
            contentResourceRepository.deleteAllByContentId(postId);
        }
    }
}

