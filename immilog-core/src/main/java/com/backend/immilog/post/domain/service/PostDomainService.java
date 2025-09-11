package com.backend.immilog.post.domain.service;

import com.backend.immilog.post.domain.model.post.Post;
import com.backend.immilog.post.domain.model.post.PostId;
import com.backend.immilog.post.domain.repositories.PostDomainRepository;
import com.backend.immilog.post.exception.PostErrorCode;
import com.backend.immilog.post.exception.PostException;
import org.springframework.stereotype.Service;

@Service
public class PostDomainService {
    
    private final PostDomainRepository postDomainRepository;
    private final PostValidator postValidator;
    
    public PostDomainService(
            PostDomainRepository postDomainRepository,
            PostValidator postValidator
    ) {
        this.postDomainRepository = postDomainRepository;
        this.postValidator = postValidator;
    }
    
    public Post createPost(Post post) {
        validatePostForCreation(post);
        return postDomainRepository.save(post);
    }
    
    public Post updatePostContent(
            PostId postId,
            String userId,
            String title,
            String content
    ) {
        var post = getPostById(postId);
        postValidator.validatePostAccess(post, userId);
        postValidator.validatePostUpdate(post, title, content);
        
        if (title != null) {
            post.updateTitle(title);
        }
        if (content != null) {
            post.updateContent(content);
        }
        
        return postDomainRepository.save(post);
    }
    
    public Post updatePostVisibility(
            PostId postId,
            String userId,
            Boolean isPublic
    ) {
        var post = getPostById(postId);
        postValidator.validatePostAccess(post, userId);
        postValidator.validatePostPublicStatus(isPublic);
        
        post.updatePublicStatus(isPublic);
        return postDomainRepository.save(post);
    }
    
    public void deletePost(
            PostId postId,
            String userId
    ) {
        var post = getPostById(postId);
        postValidator.validatePostAccess(post, userId);
        
        post.delete();
        postDomainRepository.save(post);
    }
    
    public Post incrementViewCount(PostId postId) {
        var post = getPostById(postId);
        post.increaseViewCount();
        return postDomainRepository.save(post);
    }
    
    public Post incrementCommentCount(PostId postId) {
        var post = getPostById(postId);
        post.increaseCommentCount();
        return postDomainRepository.save(post);
    }
    
    public Post decrementCommentCount(PostId postId) {
        var post = getPostById(postId);
        post.decreaseCommentCount();
        return postDomainRepository.save(post);
    }
    
    private Post getPostById(PostId postId) {
        return postDomainRepository.findById(postId.value())
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));
    }
    
    private void validatePostForCreation(Post post) {
        if (post.id() == null) {
            throw new PostException(PostErrorCode.INVALID_POST_DATA);
        }
        if (post.title() == null || post.title().isBlank()) {
            throw new PostException(PostErrorCode.INVALID_POST_DATA);
        }
        if (post.content() == null || post.content().isBlank()) {
            throw new PostException(PostErrorCode.INVALID_POST_DATA);
        }
        if (post.userId() == null || post.userId().isBlank()) {
            throw new PostException(PostErrorCode.INVALID_USER);
        }
    }
}