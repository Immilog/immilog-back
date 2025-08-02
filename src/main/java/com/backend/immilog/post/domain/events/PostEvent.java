package com.backend.immilog.post.domain.events;

import com.backend.immilog.shared.domain.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.List;

public abstract class PostEvent implements DomainEvent {

    public static class InteractionDataRequested extends PostEvent {
        private final List<String> postIds;
        private final String contentType;
        private final LocalDateTime occurredAt;

        public InteractionDataRequested(
                List<String> postIds,
                String contentType
        ) {
            this.postIds = postIds;
            this.contentType = contentType;
            this.occurredAt = LocalDateTime.now();
        }

        @Override
        public LocalDateTime occurredAt() {
            return occurredAt;
        }

        public List<String> getPostIds() {
            return postIds;
        }

        public String getContentType() {
            return contentType;
        }
    }

    public static class BookmarkPostsRequested extends PostEvent {
        private final String userId;
        private final String contentType;
        private final LocalDateTime occurredAt;

        public BookmarkPostsRequested(
                String userId,
                String contentType
        ) {
            this.userId = userId;
            this.contentType = contentType;
            this.occurredAt = LocalDateTime.now();
        }

        @Override
        public LocalDateTime occurredAt() {
            return occurredAt;
        }

        public String getUserId() {
            return userId;
        }

        public String getContentType() {
            return contentType;
        }
    }
}