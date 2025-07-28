package com.backend.immilog.notice.domain.model;

public record NoticeId(Long value) {

    public static NoticeId of(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("NoticeId value must be positive");
        }
        return new NoticeId(value);
    }

    public static NoticeId generate() {
        return new NoticeId(null);
    }
}