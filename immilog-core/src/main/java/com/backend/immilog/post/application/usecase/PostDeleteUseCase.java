package com.backend.immilog.post.application.usecase;

import com.backend.immilog.post.application.services.PostCommandService;
import com.backend.immilog.post.application.services.PostQueryService;
import com.backend.immilog.post.application.services.PostResourceCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public interface PostDeleteUseCase {
    void deletePost(
            String userId,
            String postId
    );

    @Slf4j
    @Service
    class PostDeleter implements PostDeleteUseCase {
        private final PostCommandService postCommandService;
        private final PostQueryService postQueryService;
        private final PostResourceCommandService postResourceCommandService;

        public PostDeleter(
                PostCommandService postCommandService,
                PostQueryService postQueryService,
                PostResourceCommandService postResourceCommandService
        ) {
            this.postCommandService = postCommandService;
            this.postQueryService = postQueryService;
            this.postResourceCommandService = postResourceCommandService;
        }

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

