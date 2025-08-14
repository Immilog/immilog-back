package com.backend.immilog.notice.domain.service;

import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.notice.domain.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeFactory {

    public Notice createNotice(
            String authorUserId,
            String title,
            String content,
            NoticeType type,
            List<String> targetCountries
    ) {
        NoticeAuthor author = NoticeAuthor.of(authorUserId);
        NoticeTitle noticeTitle = NoticeTitle.of(title);
        NoticeContent noticeContent = NoticeContent.of(content);
        NoticeTargeting targeting = NoticeTargeting.of(targetCountries);

        return Notice.create(author, noticeTitle, noticeContent, type, targeting);
    }

    public Notice createGlobalNotice(
            String authorUserId,
            String title,
            String content,
            NoticeType type
    ) {
        NoticeAuthor author = NoticeAuthor.of(authorUserId);
        NoticeTitle noticeTitle = NoticeTitle.of(title);
        NoticeContent noticeContent = NoticeContent.of(content);
        NoticeTargeting targeting = NoticeTargeting.all();

        return Notice.create(author, noticeTitle, noticeContent, type, targeting);
    }

    public Notice updateNoticeContent(
            Notice existingNotice,
            String newTitle,
            String newContent,
            NoticeType newType
    ) {
        Notice updatedNotice = existingNotice;

        if (newTitle != null && !newTitle.trim().isEmpty()) {
            NoticeTitle title = NoticeTitle.of(newTitle);
            updatedNotice = updatedNotice.updateTitle(title);
        }

        if (newContent != null && !newContent.trim().isEmpty()) {
            NoticeContent content = NoticeContent.of(newContent);
            updatedNotice = updatedNotice.updateContent(content);
        }

        if (newType != null) {
            updatedNotice = updatedNotice.updateType(newType);
        }

        return updatedNotice;
    }
}