package com.backend.immilog.post.application.usecase;

import com.backend.immilog.post.application.services.command.PostCommandService;
import com.backend.immilog.post.application.services.command.PostResourceCommandService;
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
        private final PostCommandService postCommandService;
        private final PostResourceCommandService postResourceCommandService;

        @Transactional
        public void deletePost(
                String userId,
                String postId
        ) {
            postCommandService.deletePost(postId, userId);
            postResourceCommandService.deleteAllByPostId(postId);
        }
    }
}

