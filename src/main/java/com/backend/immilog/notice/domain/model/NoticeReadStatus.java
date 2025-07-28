package com.backend.immilog.notice.domain.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record NoticeReadStatus(Set<Long> readUserSeqs) {

    public static NoticeReadStatus empty() {
        return new NoticeReadStatus(new HashSet<>());
    }

    public static NoticeReadStatus of(List<Long> readUserSeqs) {
        Set<Long> uniqueUserSeqs = readUserSeqs != null ? new HashSet<>(readUserSeqs) : new HashSet<>();
        return new NoticeReadStatus(uniqueUserSeqs);
    }

    public NoticeReadStatus markAsRead(Long userSeq) {
        if (userSeq == null || userSeq <= 0) {
            return this;
        }

        Set<Long> newReadUsers = new HashSet<>(readUserSeqs);
        newReadUsers.add(userSeq);
        return new NoticeReadStatus(newReadUsers);
    }

    public boolean isReadBy(Long userSeq) {
        return userSeq != null && readUserSeqs.contains(userSeq);
    }

    public int getReadCount() {
        return readUserSeqs.size();
    }

    public List<Long> getReadUsersList() {
        return new ArrayList<>(readUserSeqs);
    }
}