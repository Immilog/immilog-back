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
}
