package com.backend.immilog.post.domain.events;

import com.backend.immilog.shared.domain.event.DomainEvent;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class PostEvent implements DomainEvent {

    public static class InteractionDataRequested extends PostEvent {
        @Getter
        private String requestId;
        @Getter
        private final List<String> postIds;
        @Getter
        private String contentType;
        private final LocalDateTime occurredAt;

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
    }

    public static class BookmarkPostsRequested extends PostEvent {
        @Getter
        private String requestId;
        @Getter
        private String userId;
        @Getter
        private String contentType;
        private final LocalDateTime occurredAt;

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

    }

    public static class UserDataRequested extends PostEvent {
        @Getter
        private String requestId;
        @Getter
        private List<String> userIds;
        private final LocalDateTime occurredAt;

        public UserDataRequested() {
            this.userIds = new ArrayList<>();
            this.occurredAt = LocalDateTime.now();
        }

        public UserDataRequested(
                String requestId,
                List<String> userIds
        ) {
            this.requestId = requestId;
            this.userIds = userIds;
            this.occurredAt = LocalDateTime.now();
        }

        @Override
        public LocalDateTime occurredAt() {
            return occurredAt;
        }

    }

    public static class CommentDataRequested extends PostEvent {
        @Getter
        private String requestId;
        @Getter
        private List<String> postIds;
        private final LocalDateTime occurredAt;

        public CommentDataRequested() {
            this.postIds = new ArrayList<>();
            this.occurredAt = LocalDateTime.now();
        }

        public CommentDataRequested(
                String requestId,
                List<String> postIds
        ) {
            this.requestId = requestId;
            this.postIds = postIds;
            this.occurredAt = LocalDateTime.now();
        }

        @Override
        public LocalDateTime occurredAt() {
            return occurredAt;
        }

    }
}