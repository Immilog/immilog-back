package com.backend.immilog.notice.domain.service;

import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.domain.model.NoticeContent;
import com.backend.immilog.notice.domain.model.NoticeTitle;
import com.backend.immilog.notice.exception.NoticeErrorCode;
import com.backend.immilog.notice.exception.NoticeException;
import com.backend.immilog.shared.enums.Country;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeValidationService {

    public void validateNoticeCreation(
            String title,
            String content,
            NoticeType type,
            List<Country> targetCountries
    ) {
        validateTitle(title);
        validateContent(content);
        validateType(type);
        validateTargetCountries(targetCountries);
    }

    public void validateNoticeUpdate(
            Notice notice,
            String title,
            String content
    ) {
        if (notice == null) {
            throw new NoticeException(NoticeErrorCode.NOTICE_NOT_FOUND);
        }

        if (notice.isDeleted()) {
            throw new NoticeException(NoticeErrorCode.NOTICE_ALREADY_DELETED);
        }

        if (title != null) {
            validateTitle(title);
        }

        if (content != null) {
            validateContent(content);
        }
    }

    public void validateNoticeAccess(
            Notice notice,
            Long userSeq
    ) {
        if (notice == null) {
            throw new NoticeException(NoticeErrorCode.NOTICE_NOT_FOUND);
        }

        if (userSeq == null || userSeq <= 0) {
            throw new NoticeException(NoticeErrorCode.INVALID_USER_SEQ);
        }
    }

    public void validateAuthorPermission(
            Notice notice,
            Long userSeq
    ) {
        validateNoticeAccess(notice, userSeq);

        if (!notice.isAuthor(userSeq)) {
            throw new NoticeException(NoticeErrorCode.NOT_AN_ADMIN_USER);
        }
    }

    private void validateTitle(String title) {
        try {
            NoticeTitle.of(title);
        } catch (Exception e) {
            throw new NoticeException(NoticeErrorCode.INVALID_NOTICE_TITLE);
        }
    }

    private void validateContent(String content) {
        try {
            NoticeContent.of(content);
        } catch (Exception e) {
            throw new NoticeException(NoticeErrorCode.INVALID_NOTICE_CONTENT);
        }
    }

    private void validateType(NoticeType type) {
        if (type == null) {
            throw new NoticeException(NoticeErrorCode.INVALID_NOTICE_TYPE);
        }
    }

    private void validateTargetCountries(List<Country> targetCountries) {
        if (targetCountries == null || targetCountries.isEmpty()) {
            throw new NoticeException(NoticeErrorCode.INVALID_NOTICE_TARGET_COUNTRIES);
        }
    }
}