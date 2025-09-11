package com.backend.immilog.post.application.services.command;

import com.backend.immilog.post.domain.model.post.Post;
import com.backend.immilog.post.domain.model.post.PostId;
import com.backend.immilog.post.domain.service.PostDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PostCommandService {
    private final PostDomainService postDomainService;

    public PostCommandService(PostDomainService postDomainService) {
        this.postDomainService = postDomainService;
    }

    @Transactional
    public Post save(Post post) {
        return postDomainService.createPost(post);
    }

    @Transactional
    public Post updatePost(
            String postId,
            String userId,
            String title,
            String content
    ) {
        return postDomainService.updatePostContent(
                PostId.of(postId),
                userId,
                title,
                content
        );
    }

    @Transactional
    public Post updatePostVisibility(
            String postId,
            String userId,
            Boolean isPublic
    ) {
        return postDomainService.updatePostVisibility(
                PostId.of(postId),
                userId,
                isPublic
        );
    }

    @Transactional
    public void deletePost(
            String postId,
            String userId
    ) {
        postDomainService.deletePost(
                PostId.of(postId),
                userId
        );
    }

    @Transactional
    public Post incrementViewCount(String postId) {
        return postDomainService.incrementViewCount(PostId.of(postId));
    }

    @Transactional
    public Post incrementCommentCount(String postId) {
        return postDomainService.incrementCommentCount(PostId.of(postId));
    }

    @Transactional
    public Post decrementCommentCount(String postId) {
        return postDomainService.decrementCommentCount(PostId.of(postId));
    }

}
