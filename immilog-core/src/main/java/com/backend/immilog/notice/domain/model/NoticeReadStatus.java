package com.backend.immilog.notice.domain.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record NoticeReadStatus(Set<String> readUserIds) {

    public static NoticeReadStatus empty() {
        return new NoticeReadStatus(new HashSet<>());
    }

    public static NoticeReadStatus of(List<String> readUserIds) {
        var uniqueUserIds = readUserIds != null ? new HashSet<String>(readUserIds) : new HashSet<String>();
        return new NoticeReadStatus(uniqueUserIds);
    }

    public NoticeReadStatus markAsRead(String userId) {
        if (userId == null || userId.isBlank()) {
            return this;
        }

        var newReadUsers = new HashSet<String>(readUserIds);
        newReadUsers.add(userId);
        return new NoticeReadStatus(newReadUsers);
    }

    public boolean isReadBy(String userId) {
        return userId != null && readUserIds.contains(userId);
    }

    public int getReadCount() {
        return readUserIds.size();
    }

    public List<String> getReadUsersList() {
        return new ArrayList<>(readUserIds);
    }
}