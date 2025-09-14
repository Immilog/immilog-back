package com.backend.immilog.post.domain.service;

import com.backend.immilog.shared.domain.model.CommentData;
import com.backend.immilog.shared.domain.model.InteractionData;
import com.backend.immilog.shared.domain.model.UserData;

import java.util.List;

public interface PostDataService {
    List<UserData> getUserData(List<String> userIds);

    List<InteractionData> getInteractionData(
            List<String> postIds,
            String contentType
    );

    List<CommentData> getCommentData(List<String> postIds);

    List<String> getBookmarkData(
            String userId,
            String contentType
    );
}