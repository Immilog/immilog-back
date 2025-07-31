package com.backend.immilog.post.domain.repositories;

import com.backend.immilog.post.domain.model.post.PostType;
import com.backend.immilog.post.domain.model.resource.PostResource;
import com.backend.immilog.post.domain.model.resource.ResourceType;

import java.util.List;

public interface PostResourceRepository {
    void deleteAllEntities(
            String postId,
            PostType postType,
            ResourceType resourceType,
            List<String> deleteAttachments
    );

    void deleteAllByPostId(String id);

    List<PostResource> findAllByPostId(String id);

    List<PostResource> findAllByPostIdList(
            List<String> postIdList,
            PostType postType
    );
}
