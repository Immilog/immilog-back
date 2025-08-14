package com.backend.immilog.post.domain.events;

import com.backend.immilog.shared.domain.event.CompensationEvent;

import java.time.LocalDateTime;

public abstract class PostCompensationEvent implements CompensationEvent {
    
    /**
     * 댓글 수 증가 실패 시 보상 이벤트
     */
    public static class CommentCountIncreaseCompensation extends PostCompensationEvent {
        private final String transactionId;
        private final String originalEventId;
        private final String postId;
        private final LocalDateTime occurredAt;

        public CommentCountIncreaseCompensation(String transactionId, String originalEventId, String postId) {
            this.transactionId = transactionId;
            this.originalEventId = originalEventId;
            this.postId = postId;
            this.occurredAt = LocalDateTime.now();
        }

        @Override
        public String getTransactionId() {
            return transactionId;
        }

        @Override
        public String getCompensationAction() {
            return "ROLLBACK_COMMENT_COUNT_INCREASE";
        }

        @Override
        public String getOriginalEventId() {
            return originalEventId;
        }

        @Override
        public LocalDateTime occurredAt() {
            return occurredAt;
        }

        public String getPostId() {
            return postId;
        }
    }

    /**
     * 게시글 삭제 실패 시 보상 이벤트
     */
    public static class PostDeletionCompensation extends PostCompensationEvent {
        private final String transactionId;
        private final String originalEventId;
        private final String postId;
        private final String userId;
        private final LocalDateTime occurredAt;

        public PostDeletionCompensation(String transactionId, String originalEventId, String postId, String userId) {
            this.transactionId = transactionId;
            this.originalEventId = originalEventId;
            this.postId = postId;
            this.userId = userId;
            this.occurredAt = LocalDateTime.now();
        }

        @Override
        public String getTransactionId() {
            return transactionId;
        }

        @Override
        public String getCompensationAction() {
            return "RESTORE_DELETED_POST";
        }

        @Override
        public String getOriginalEventId() {
            return originalEventId;
        }

        @Override
        public LocalDateTime occurredAt() {
            return occurredAt;
        }

        public String getPostId() {
            return postId;
        }

        public String getUserId() {
            return userId;
        }
    }
}