package com.backend.immilog.post.application.dto;

import com.backend.immilog.post.domain.model.post.Badge;
import com.backend.immilog.post.domain.model.post.Categories;
import com.backend.immilog.post.presentation.payload.PostInformation;
import com.backend.immilog.shared.enums.ContentStatus;

import java.util.List;

public record PostResult(
        String postId,
        String userId,
        String userProfileUrl,
        String userNickname,
        Long commentCount,
        Long viewCount,
        Long likeCount,
        List<String> tags,
        List<String> attachments,
        List<String> likeUsers,
        List<String> bookmarkUsers,
        String isPublic,
        String country,
        String region,
        Categories category,
        ContentStatus status,
        Badge badge,
        String createdAt,
        String updatedAt,
        String title,
        String content,
        String keyword
) {

    public PostInformation toInfraDTO() {
        return new PostInformation(
                this.postId,
                this.userId,
                this.userProfileUrl,
                this.userNickname,
                this.commentCount,
                this.viewCount,
                this.likeCount,
                this.tags,
                this.attachments,
                this.likeUsers,
                this.bookmarkUsers,
                this.isPublic,
                this.country,
                this.region,
                this.category,
                this.status,
                this.badge,
                this.createdAt,
                this.updatedAt,
                this.title,
                this.content,
                this.keyword
        );
    }
}
