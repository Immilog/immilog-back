package com.backend.immilog.post.domain.repositories;

import com.backend.immilog.post.domain.model.resource.PostResource;
import com.backend.immilog.post.domain.model.resource.ResourceType;
import com.backend.immilog.shared.enums.ContentType;

import java.util.List;

public interface PostResourceRepository {
    void deleteAllEntities(
            String postId,
            ContentType contentType,
            ResourceType resourceType,
            List<String> deleteAttachments
    );

    void deleteAllByPostId(String id);

    List<PostResource> findAllByPostId(String id);

    List<PostResource> findAllByPostIdList(
            List<String> postIdList,
            ContentType contentType
    );
}
