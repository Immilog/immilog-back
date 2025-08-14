package com.backend.immilog.notice.domain.model;

import com.backend.immilog.notice.domain.enums.NoticeStatus;
import com.backend.immilog.notice.domain.enums.NoticeType;

import java.time.LocalDateTime;

public record NoticeMetadata(
        NoticeType type,
        NoticeStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static NoticeMetadata of(
            NoticeType type,
            NoticeStatus status
    ) {
        var now = LocalDateTime.now();
        return new NoticeMetadata(type, status, now, now);
    }

    public static NoticeMetadata restore(
            NoticeType type,
            NoticeStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new NoticeMetadata(type, status, createdAt, updatedAt);
    }

    public NoticeMetadata updateType(NoticeType newType) {
        if (newType == null || newType.equals(this.type)) {
            return this;
        }
        return new NoticeMetadata(newType, this.status, this.createdAt, LocalDateTime.now());
    }

    public NoticeMetadata updateStatus(NoticeStatus newStatus) {
        if (newStatus == null || newStatus.equals(this.status)) {
            return this;
        }
        return new NoticeMetadata(this.type, newStatus, this.createdAt, LocalDateTime.now());
    }

    public NoticeMetadata touch() {
        return new NoticeMetadata(this.type, this.status, this.createdAt, LocalDateTime.now());
    }

    public boolean isActive() {
        return status == NoticeStatus.NORMAL;
    }

    public boolean isDeleted() {
        return status == NoticeStatus.DELETED;
    }
}