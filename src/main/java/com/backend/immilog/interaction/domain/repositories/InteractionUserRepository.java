package com.backend.immilog.interaction.domain.repositories;

import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.post.domain.model.post.PostType;

import java.util.List;
import java.util.Optional;

public interface InteractionUserRepository {
    List<InteractionUser> findByPostIdListAndPostType(
            List<String> postIdList,
            PostType postType
    );

    List<InteractionUser> findBookmarksByUserIdAndPostType(
            String userId,
            PostType postType
    );

    Optional<InteractionUser> findById(String id);

    InteractionUser save(InteractionUser interactionUser);

    void deleteById(String id);
}