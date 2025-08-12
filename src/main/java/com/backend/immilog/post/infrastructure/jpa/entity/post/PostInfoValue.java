package com.backend.immilog.post.infrastructure.jpa.entity.post;

import com.backend.immilog.post.domain.model.post.PostInfo;
import com.backend.immilog.shared.enums.ContentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class PostInfoValue {
    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "region")
    private String region;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ContentStatus status;

    @Column(name = "country_id")
    private String countryId;

    protected PostInfoValue() {}

    public PostInfoValue(
            String title,
            String content,
            Long viewCount,
            String region,
            ContentStatus status,
            String countryId
    ) {
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.region = region;
        this.status = status;
        this.countryId = countryId;
    }

    public static PostInfoValue of(
            String title,
            String content,
            Long viewCount,
            String countryId,
            String region,
            ContentStatus status
    ) {
        return new PostInfoValue(
                title,
                content,
                viewCount,
                region,
                status,
                countryId
        );
    }

    public PostInfo toDomain() {
        return new PostInfo(
                title,
                content,
                viewCount,
                region,
                status,
                countryId
        );
    }
}