package com.backend.immilog.post.application.services;

import com.backend.immilog.post.application.config.PostConfiguration;
import com.backend.immilog.post.domain.events.PostEvent;
import com.backend.immilog.post.domain.service.PostDataService;
import com.backend.immilog.shared.domain.event.DomainEvents;
import com.backend.immilog.shared.domain.model.CommentData;
import com.backend.immilog.shared.domain.model.InteractionData;
import com.backend.immilog.shared.domain.model.UserData;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostEventService implements PostDataService {
    private final EventResultStorageService eventResultStorageService;
    private final PostConfiguration postConfiguration;

    @Override
    public List<UserData> getUserData(List<String> userIds) {
        return requestUserData(userIds);
    }

    @Override
    public List<InteractionData> getInteractionData(
            List<String> postIds,
            String contentType
    ) {
        return requestInteractionData(postIds, contentType);
    }

    @Override
    public List<CommentData> getCommentData(List<String> postIds) {
        return requestCommentData(postIds);
    }

    @Override
    public List<String> getBookmarkData(
            String userId,
            String contentType
    ) {
        return requestBookmarkData(userId, contentType);
    }

    @SuppressWarnings("unchecked")
    public List<UserData> requestUserData(List<String> userIds) {
        String requestId = eventResultStorageService.generateRequestId("user");
        log.info("Requesting user data for {} users with requestId: {}, userIds: {}", userIds.size(), requestId, userIds);

        eventResultStorageService.registerEventProcessing(requestId);
        DomainEvents.raise(new PostEvent.UserDataRequested(requestId, userIds));

        var userData = eventResultStorageService.waitForUserData(requestId, postConfiguration.getEventTimeout());
        log.info("Retrieved {} user data items via event for requestId: {}", userData.size(), requestId);

        return userData;
    }

    @SuppressWarnings("unchecked")
    public List<InteractionData> requestInteractionData(
            List<String> postIds,
            String contentType
    ) {
        String requestId = eventResultStorageService.generateRequestId("interaction");
        log.info("Requesting interaction data for {} posts with requestId: {}, postIds: {}", postIds.size(), requestId, postIds);

        eventResultStorageService.registerEventProcessing(requestId);
        DomainEvents.raise(new PostEvent.InteractionDataRequested(requestId, postIds, contentType));

        var interactionData = eventResultStorageService.waitForInteractionData(requestId, postConfiguration.getEventTimeout());
        log.info("Retrieved {} interaction data items via event for requestId: {}", interactionData.size(), requestId);

        return interactionData;
    }

    public List<String> requestBookmarkData(
            String userId,
            String contentType
    ) {
        String requestId = eventResultStorageService.generateRequestId("bookmark");
        log.debug("Requesting bookmark data for userId: {} with requestId: {}", userId, requestId);

        eventResultStorageService.registerEventProcessing(requestId);
        DomainEvents.raise(new PostEvent.BookmarkPostsRequested(requestId, userId, contentType));

        var postIdList = eventResultStorageService.waitForBookmarkData(requestId, postConfiguration.getEventTimeout());
        log.debug("Retrieved {} bookmarked post IDs via event for user: {}", postIdList.size(), userId);

        return postIdList;
    }

    @SuppressWarnings("unchecked")
    public List<CommentData> requestCommentData(List<String> postIds) {
        String requestId = eventResultStorageService.generateRequestId("comment");
        log.info("Requesting comment data for {} posts with requestId: {}", postIds.size(), requestId);

        eventResultStorageService.registerEventProcessing(requestId);
        DomainEvents.raise(new PostEvent.CommentDataRequested(requestId, postIds));

        var commentData = eventResultStorageService.waitForCommentData(requestId, postConfiguration.getEventTimeout());
        log.info("Retrieved {} comment data items via event for requestId: {}", commentData.size(), requestId);

        return commentData;
    }
}