package com.backend.immilog.comment.domain.model;

import com.backend.immilog.post.domain.model.post.PostStatus;

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
    private final Integer likeCount;
    private final PostStatus status;
    private final List<String> likeUsers;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Comment(
            String id,
            String userId,
            String content,
            CommentRelation commentRelation,
            int replyCount,
            Integer likeCount,
            PostStatus status,
            List<String> likeUsers,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.commentRelation = commentRelation;
        this.replyCount = replyCount;
        this.likeCount = likeCount;
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
                0,
                PostStatus.NORMAL,
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
                this.likeCount,
                PostStatus.DELETED,
                this.likeUsers,
                this.createdAt,
                LocalDateTime.now()
        );
    }

    public Comment addLikeUser(String userId) {
        if (!Objects.isNull(this.likeUsers)) {
            var newLikeUsers = this.likeUsers;
            newLikeUsers.add(userId);
            var newLikeCount = this.likeCount + 1;
            return new Comment(
                    this.id,
                    this.userId,
                    this.content,
                    this.commentRelation,
                    this.replyCount,
                    newLikeCount,
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
                this.likeCount,
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

    public Integer likeCount() {return likeCount;}

    public PostStatus status() {return status;}

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
                this.likeCount,
                this.status,
                this.likeUsers,
                this.createdAt,
                LocalDateTime.now()
        );
    }
}