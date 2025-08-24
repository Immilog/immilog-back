package com.backend.immilog.post.domain.model.post;

import com.backend.immilog.post.exception.PostErrorCode;
import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.shared.enums.ContentStatus;

import java.time.LocalDateTime;

public class Post {
    private final String id;
    private final PostUserInfo postUserInfo;
    private PostInfo postInfo;
    private final Categories category;
    private String isPublic;
    private Badge badge;
    private Long commentCount;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Post(
            String id,
            PostUserInfo postUserInfo,
            PostInfo postInfo,
            Categories category,
            String isPublic,
            Badge badge,
            Long commentCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.postUserInfo = postUserInfo;
        this.postInfo = postInfo;
        this.category = category;
        this.isPublic = isPublic;
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
            String isPublic
    ) {
        final var postInfo = PostInfo.of(title, content, userCountryId, userRegion);
        final var postUserInfo = new PostUserInfo(userId);
        return new Post(
                null,
                postUserInfo,
                postInfo,
                category,
                isPublic,
                null,
                0L,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public Post increaseCommentCount() {
        if (this.postInfo.status() == ContentStatus.DELETED) {
            throw new PostException(PostErrorCode.POST_ALREADY_DELETED);
        }
        this.commentCount++;
        return this;
    }

    public Post decreaseCommentCount() {
        if (this.postInfo.status() == ContentStatus.DELETED) {
            throw new PostException(PostErrorCode.POST_ALREADY_DELETED);
        }
        if (this.commentCount > 0) {
            this.commentCount--;
        }
        return this;
    }

    public Post updateIsPublic(Boolean isPublic) {
        if (isPublic == null) {
            throw new PostException(PostErrorCode.INVALID_PUBLIC_STATUS);
        }
        if (this.status().equals(ContentStatus.DELETED)) {
            throw new PostException(PostErrorCode.POST_ALREADY_DELETED);
        }
        var value = isPublic ? "Y" : "N";
        if (this.isPublic.equals(value)) {
            return this;
        }
        this.isPublic = value;
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
        this.postInfo = new PostInfo(
                this.postInfo.title(),
                newContent,
                this.postInfo.viewCount(),
                this.postInfo.region(),
                this.postInfo.status(),
                this.postInfo.countryId()
        );
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
        this.postInfo = new PostInfo(
                title,
                this.postInfo.content(),
                this.postInfo.viewCount(),
                this.postInfo.region(),
                this.postInfo.status(),
                this.postInfo.countryId()
        );
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public Post delete() {
        if (this.postInfo.status() == ContentStatus.DELETED) {
            throw new PostException(PostErrorCode.POST_ALREADY_DELETED);
        }
        this.postInfo = new PostInfo(
                this.postInfo.title(),
                this.postInfo.content(),
                this.postInfo.viewCount(),
                this.postInfo.region(),
                ContentStatus.DELETED,
                this.postInfo.countryId()
        );
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public String userId() {return this.postUserInfo.userId();}

    public Post increaseViewCount() {
        if (this.postInfo.status() == ContentStatus.DELETED) {
            throw new PostException(PostErrorCode.POST_ALREADY_DELETED);
        }
        this.postInfo = new PostInfo(
                this.postInfo.title(),
                this.postInfo.content(),
                this.postInfo.viewCount() + 1,
                this.postInfo.region(),
                this.postInfo.status(),
                this.postInfo.countryId()
        );
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

    public String id() {return id;}

    public PostUserInfo postUserInfo() {return postUserInfo;}

    public PostInfo postInfo() {return postInfo;}

    public Categories category() {return category;}

    public String isPublic() {return isPublic;}

    public Badge badge() {return badge;}

    public Long commentCount() {return commentCount;}

    public LocalDateTime createdAt() {return createdAt;}

    public LocalDateTime updatedAt() {return updatedAt;}
}
