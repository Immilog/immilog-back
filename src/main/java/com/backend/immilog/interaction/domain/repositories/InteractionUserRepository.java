package com.backend.immilog.interaction.domain.repositories;

import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.shared.enums.ContentType;

import java.util.List;
import java.util.Optional;

public interface InteractionUserRepository {
    List<InteractionUser> findByPostIdListAndContentType(
            List<String> postIdList,
            ContentType contentType
    );

    List<InteractionUser> findBookmarksByUserIdAndContentType(
            String userId,
            ContentType contentType
    );

    Optional<InteractionUser> findById(String id);

    InteractionUser save(InteractionUser interactionUser);

    void deleteById(String id);
}