package com.backend.immilog.comment.domain.model;

import com.backend.immilog.comment.domain.event.CommentCreatedEvent;
import com.backend.immilog.shared.domain.event.DomainEvents;
import com.backend.immilog.shared.enums.ContentStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.function.Consumer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Accessors(fluent = true)
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

    @Builder
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
            String parentId,
            ReferenceType referenceType
    ) {
        return Comment.builder()
                .userId(userId)
                .content(content)
                .commentRelation(CommentRelation.of(postId, parentId, referenceType))
                .replyCount(0)
                .status(ContentStatus.NORMAL)
                .likeUsers(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Comment copyWith(Consumer<CommentBuilder> customizer) {
        var builder = Comment.builder()
                .id(this.id)
                .userId(this.userId)
                .content(this.content)
                .commentRelation(this.commentRelation)
                .replyCount(this.replyCount)
                .status(this.status)
                .likeUsers(this.likeUsers)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt);
        customizer.accept(builder);
        return builder.build();
    }

    public Comment delete() {
        return copyWith(builder -> builder
                .status(ContentStatus.DELETED)
                .updatedAt(LocalDateTime.now()));
    }

    public Comment addLikeUser(String userId) {
        if (!Objects.isNull(this.likeUsers)) {
            var newLikeUsers = new ArrayList<>(this.likeUsers);
            newLikeUsers.add(userId);
            return copyWith(builder -> builder.likeUsers(newLikeUsers));
        }
        return this;
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

    public Comment updateContent(String newContent) {
        return copyWith(builder -> builder
                .content(newContent)
                .updatedAt(LocalDateTime.now()));
    }

    public void publishCreatedEvent() {
        if (this.id != null) {
            DomainEvents.raise(new CommentCreatedEvent(this.id, this.postId(), this.userId));
        }
    }

    public Comment withId(String id) {
        return copyWith(builder -> builder.id(id));
    }
}