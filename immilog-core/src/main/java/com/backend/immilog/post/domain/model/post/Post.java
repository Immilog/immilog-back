package com.backend.immilog.post.domain.model.post;

import com.backend.immilog.post.exception.PostErrorCode;
import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.shared.enums.ContentStatus;

import java.time.LocalDateTime;

public class Post {
    private final PostId id;
    private final PostUserInfo postUserInfo;
    private PostInfo postInfo;
    private final Categories category;
    private PublicStatus publicStatus;
    private Badge badge;
    private CommentCount commentCount;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Post(
            PostId id,
            PostUserInfo postUserInfo,
            PostInfo postInfo,
            Categories category,
            PublicStatus publicStatus,
            Badge badge,
            CommentCount commentCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.postUserInfo = postUserInfo;
        this.postInfo = postInfo;
        this.category = category;
        this.publicStatus = publicStatus;
        this.badge = badge;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Post of(
            String userId,
            String userCountryId,
            String userRegion,
            String title,
            String content,
            Categories category,
            Boolean isPublic
    ) {
        final var postInfo = PostInfo.of(title, content, userCountryId, userRegion);
        final var postUserInfo = new PostUserInfo(userId);
        return new Post(
                PostId.generate(),
                postUserInfo,
                postInfo,
                category,
                PublicStatus.fromBoolean(isPublic),
                null,
                CommentCount.zero(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public Post increaseCommentCount() {
        if (this.postInfo.status() == ContentStatus.DELETED) {
            throw new PostException(PostErrorCode.POST_ALREADY_DELETED);
        }
        this.commentCount = this.commentCount.increment();
        return this;
    }

    public Post decreaseCommentCount() {
        if (this.postInfo.status() == ContentStatus.DELETED) {
            throw new PostException(PostErrorCode.POST_ALREADY_DELETED);
        }
        this.commentCount = this.commentCount.decrement();
        return this;
    }

    public Post updatePublicStatus(Boolean isPublic) {
        if (isPublic == null) {
            throw new PostException(PostErrorCode.INVALID_PUBLIC_STATUS);
        }
        if (this.status().equals(ContentStatus.DELETED)) {
            throw new PostException(PostErrorCode.POST_ALREADY_DELETED);
        }
        var newStatus = PublicStatus.fromBoolean(isPublic);
        if (this.publicStatus.equals(newStatus)) {
            return this;
        }
        this.publicStatus = newStatus;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public Post updateContent(String newContent) {
        if (newContent == null || this.postInfo.content().equals(newContent)) {
            return this;
        }
        if (this.postInfo.status() == ContentStatus.DELETED) {
            throw new PostException(PostErrorCode.POST_ALREADY_DELETED);
        }
        this.postInfo = this.postInfo.withContent(newContent);
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public Post updateTitle(String title) {
        if (title == null || this.postInfo.title().equals(title)) {
            return this;
        }
        if (this.postInfo.status() == ContentStatus.DELETED) {
            throw new PostException(PostErrorCode.POST_ALREADY_DELETED);
        }
        this.postInfo = this.postInfo.withTitle(title);
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public Post delete() {
        if (this.postInfo.status() == ContentStatus.DELETED) {
            throw new PostException(PostErrorCode.POST_ALREADY_DELETED);
        }
        this.postInfo = this.postInfo.withStatus(ContentStatus.DELETED);
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public String userId() {return this.postUserInfo.userId();}

    public Post increaseViewCount() {
        if (this.postInfo.status() == ContentStatus.DELETED) {
            throw new PostException(PostErrorCode.POST_ALREADY_DELETED);
        }
        this.postInfo = this.postInfo.withViewCount(this.postInfo.viewCount() + 1);
        return this;
    }

    public Post updateBadge(Badge badge) {
        if (this.badge != null && this.badge.equals(badge)) {
            return this;
        }
        this.badge = badge;
        return this;
    }

    public String title() {return this.postInfo.title();}

    public String content() {return this.postInfo.content();}

    public String countryId() {return this.postInfo.countryId();}

    public String region() {return this.postInfo.region();}

    public Long viewCount() {return this.postInfo.viewCount();}


    public ContentStatus status() {return this.postInfo.status();}

    public PostId id() {return id;}

    public PostUserInfo postUserInfo() {return postUserInfo;}

    public PostInfo postInfo() {return postInfo;}

    public Categories category() {return category;}

    public PublicStatus publicStatus() {return publicStatus;}

    public boolean isPublic() {return publicStatus.isPublic();}

    public String isPublicValue() {return publicStatus.getValue();}

    public Badge badge() {return badge;}

    public CommentCount commentCount() {return commentCount;}

    public Long commentCountValue() {return commentCount.value();}

    public LocalDateTime createdAt() {return createdAt;}

    public LocalDateTime updatedAt() {return updatedAt;}
}
