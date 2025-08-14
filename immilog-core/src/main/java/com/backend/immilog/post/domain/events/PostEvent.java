package com.backend.immilog.post.domain.events;

import com.backend.immilog.shared.domain.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class PostEvent implements DomainEvent {

    public static class InteractionDataRequested extends PostEvent {
        private String requestId;
        private List<String> postIds;
        private String contentType;
        private LocalDateTime occurredAt;

        public InteractionDataRequested() {
            this.postIds = new ArrayList<>();
            this.occurredAt = LocalDateTime.now();
        }

        public InteractionDataRequested(
                String requestId,
                List<String> postIds,
                String contentType
        ) {
            this.requestId = requestId;
            this.postIds = postIds;
            this.contentType = contentType;
            this.occurredAt = LocalDateTime.now();
        }

        @Override
        public LocalDateTime occurredAt() {
            return occurredAt;
        }

        public String getRequestId() {
            return requestId;
        }

        public List<String> getPostIds() {
            return postIds;
        }

        public String getContentType() {
            return contentType;
        }
    }

    public static class BookmarkPostsRequested extends PostEvent {
        private String requestId;
        private String userId;
        private String contentType;
        private LocalDateTime occurredAt;

        public BookmarkPostsRequested() {
            this.occurredAt = LocalDateTime.now();
        }

        public BookmarkPostsRequested(
                String requestId,
                String userId,
                String contentType
        ) {
            this.requestId = requestId;
            this.userId = userId;
            this.contentType = contentType;
            this.occurredAt = LocalDateTime.now();
        }

        @Override
        public LocalDateTime occurredAt() {
            return occurredAt;
        }

        public String getRequestId() {
            return requestId;
        }

        public String getUserId() {
            return userId;
        }

        public String getContentType() {
            return contentType;
        }
    }
}