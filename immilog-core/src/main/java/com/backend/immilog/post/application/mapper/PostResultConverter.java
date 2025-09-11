package com.backend.immilog.post.application.mapper;

import com.backend.immilog.post.application.dto.out.PostResult;
import com.backend.immilog.post.domain.model.post.Post;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class PostResultConverter {

    public PostResult convertToPostResult(Post post) {
        return new PostResult(
                post.id().value(),
                post.postUserInfo().userId(),
                null,
                null,
                post.commentCountValue(),
                post.postInfo().viewCount(),
                0L,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                post.isPublicValue(),
                post.postInfo().countryId(),
                post.postInfo().region(),
                post.category(),
                post.postInfo().status(),
                post.badge(),
                post.createdAt() != null ? post.createdAt().toString() : null,
                post.updatedAt() != null ? post.updatedAt().toString() : null,
                post.postInfo().title(),
                post.postInfo().content(),
                null
        );
    }
}