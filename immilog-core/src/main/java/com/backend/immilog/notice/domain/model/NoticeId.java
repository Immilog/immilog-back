package com.backend.immilog.notice.domain.model;

public record NoticeId(String value) {

    public static NoticeId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("NoticeId value must be not null or empty");
        }
        return new NoticeId(value);
    }

    public static NoticeId generate() {
        return new NoticeId(null);
    }
}