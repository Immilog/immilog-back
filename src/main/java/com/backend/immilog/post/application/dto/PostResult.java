package com.backend.immilog.post.application.dto;

import com.backend.immilog.post.domain.model.post.Categories;
import com.backend.immilog.post.domain.model.post.PostStatus;

import java.util.List;

public record PostResult(
        String id,
        String userId,
        String userProfileUrl,
        String userNickName,
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
        PostStatus status,
        String createdAt,
        String updatedAt,
        String title,
        String content,
        String keyword
) {
}
