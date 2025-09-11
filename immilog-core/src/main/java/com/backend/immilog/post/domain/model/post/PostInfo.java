package com.backend.immilog.post.domain.model.post;

import com.backend.immilog.shared.enums.ContentStatus;

public record PostInfo(
        String title,
        String content,
        Long viewCount,
        String region,
        ContentStatus status,
        String countryId
) {
    public static PostInfo of(
            String title,
            String content,
            String countryId,
            String region
    ) {
        return new PostInfo(
                title,
                content,
                0L,
                region,
                ContentStatus.NORMAL,
                countryId
        );
    }

    public PostInfo withTitle(String newTitle) {
        return new PostInfo(newTitle, content, viewCount, region, status, countryId);
    }

    public PostInfo withContent(String newContent) {
        return new PostInfo(title, newContent, viewCount, region, status, countryId);
    }

    public PostInfo withViewCount(Long newViewCount) {
        return new PostInfo(title, content, newViewCount, region, status, countryId);
    }

    public PostInfo withStatus(ContentStatus newStatus) {
        return new PostInfo(title, content, viewCount, region, newStatus, countryId);
    }
}
