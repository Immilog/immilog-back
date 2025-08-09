package com.backend.immilog.comment.domain.model;

import com.backend.immilog.comment.domain.event.CommentCreatedEvent;
import com.backend.immilog.shared.domain.event.DomainEvents;
import com.backend.immilog.shared.enums.ContentStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Comment {
    private final String id;
    private final String userId;
    private final String content;
    private final CommentRelation commentRelation;
    private final int replyCount;
    private final ContentStatus status;
    private final List<String> likeUsers;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Comment(
            String id,
            String userId,
            String content,
            CommentRelation commentRelation,
            int replyCount,
            ContentStatus status,
            List<String> likeUsers,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.commentRelation = commentRelation;
        this.replyCount = replyCount;
        this.status = status;
        this.likeUsers = likeUsers;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Comment of(
            String userId,
            String postId,
            String content,
            ReferenceType referenceType
    ) {
        return new Comment(
                null,
                userId,
                content,
                CommentRelation.of(postId, null, referenceType),
                0,
                ContentStatus.NORMAL,
                new ArrayList<>(),
                LocalDateTime.now(),
                null
        );
    }

    public Comment delete() {
        return new Comment(
                this.id,
                this.userId,
                this.content,
                this.commentRelation,
                this.replyCount,
                ContentStatus.DELETED,
                this.likeUsers,
                this.createdAt,
                LocalDateTime.now()
        );
    }

    public Comment addLikeUser(String userId) {
        if (!Objects.isNull(this.likeUsers)) {
            var newLikeUsers = this.likeUsers;
            newLikeUsers.add(userId);
            return new Comment(
                    this.id,
                    this.userId,
                    this.content,
                    this.commentRelation,
                    this.replyCount,
                    this.status,
                    newLikeUsers,
                    this.createdAt,
                    this.updatedAt
            );
        }
        return this;
    }

    public Comment increaseReplyCount() {
        return new Comment(
                this.id,
                this.userId,
                this.content,
                this.commentRelation,
                this.replyCount + 1,
                this.status,
                this.likeUsers,
                this.createdAt,
                this.updatedAt
        );
    }

    public String postId() {
        return this.commentRelation.postId();
    }

    public String parentId() {
        return this.commentRelation.parentId();
    }

    public ReferenceType referenceType() {
        return this.commentRelation.referenceType();
    }

    public String id() {return id;}

    public String userId() {return userId;}

    public String content() {return content;}

    public CommentRelation commentRelation() {return commentRelation;}

    public int replyCount() {return replyCount;}


    public ContentStatus status() {return status;}

    public List<String> likeUsers() {return likeUsers;}

    public LocalDateTime createdAt() {return createdAt;}

    public LocalDateTime updatedAt() {return updatedAt;}

    public Comment updateContent(String newContent) {
        return new Comment(
                this.id,
                this.userId,
                newContent,
                this.commentRelation,
                this.replyCount,
                this.status,
                this.likeUsers,
                this.createdAt,
                LocalDateTime.now()
        );
    }

    public void publishCreatedEvent() {
        if (this.id != null) {
            DomainEvents.raise(new CommentCreatedEvent(this.id, this.postId(), this.userId));
        }
    }

    public Comment withId(String id) {
        return new Comment(
                id,
                this.userId,
                this.content,
                this.commentRelation,
                this.replyCount,
                this.status,
                this.likeUsers,
                this.createdAt,
                this.updatedAt
        );
    }
}