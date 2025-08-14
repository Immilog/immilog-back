package com.backend.immilog.post.domain.service;

import com.backend.immilog.post.domain.model.post.Post;
import com.backend.immilog.post.exception.PostErrorCode;
import com.backend.immilog.post.exception.PostException;
import org.springframework.stereotype.Service;

@Service
public class PostValidator {

    public void validatePostAccess(
            Post post,
            String userId
    ) {
        if (!post.userId().equals(userId)) {
            throw new PostException(PostErrorCode.NO_AUTHORITY);
        }
    }

    public void validatePostUpdate(
            Post post,
            String newTitle,
            String newContent
    ) {
        if (newTitle == null && newContent == null) {
            throw new PostException(PostErrorCode.INVALID_POST_DATA);
        }
    }

    public void validatePostPublicStatus(Boolean isPublic) {
        if (isPublic == null) {
            throw new PostException(PostErrorCode.INVALID_PUBLIC_STATUS);
        }
    }
}