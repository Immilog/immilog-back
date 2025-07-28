package com.backend.immilog.notice.application.service;

import com.backend.immilog.notice.application.services.NoticeCommandService;
import com.backend.immilog.notice.application.services.NoticeQueryService;
import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.domain.model.NoticeId;
import com.backend.immilog.notice.domain.service.NoticeAuthorizationService;
import com.backend.immilog.notice.domain.service.NoticeFactory;
import com.backend.immilog.notice.domain.service.NoticeValidationService;
import com.backend.immilog.user.domain.model.enums.Country;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NoticeService {

    private final NoticeQueryService noticeQueryService;
    private final NoticeCommandService noticeCommandService;
    private final NoticeAuthorizationService authorizationService;
    private final NoticeValidationService validationService;
    private final NoticeFactory noticeFactory;

    public NoticeService(
            NoticeQueryService noticeQueryService,
            NoticeCommandService noticeCommandService,
            NoticeAuthorizationService authorizationService,
            NoticeValidationService validationService,
            NoticeFactory noticeFactory
    ) {
        this.noticeQueryService = noticeQueryService;
        this.noticeCommandService = noticeCommandService;
        this.authorizationService = authorizationService;
        this.validationService = validationService;
        this.noticeFactory = noticeFactory;
    }

    public NoticeId createNotice(
            String token,
            String title,
            String content,
            NoticeType type,
            List<Country> targetCountries
    ) {
        var author = authorizationService.validateAndGetAuthor(token);
        validationService.validateNoticeCreation(title, content, type, targetCountries);

        var notice = noticeFactory.createNotice(
                author.userSeq(),
                title,
                content,
                type,
                targetCountries
        );

        var savedNotice = noticeCommandService.save(notice);
        return savedNotice.getId();
    }

    public void updateNotice(
            String token,
            NoticeId noticeId,
            String title,
            String content,
            NoticeType type
    ) {
        var notice = noticeQueryService.getById(noticeId);
        authorizationService.validateNoticeModificationAccess(notice, token);
        validationService.validateNoticeUpdate(notice, title, content);

        var updatedNotice = noticeFactory.updateNoticeContent(notice, title, content, type);
        noticeCommandService.save(updatedNotice);
    }

    public void deleteNotice(
            String token,
            NoticeId noticeId
    ) {
        var notice = noticeQueryService.getById(noticeId);
        authorizationService.validateNoticeModificationAccess(notice, token);

        var deletedNotice = notice.delete();
        noticeCommandService.save(deletedNotice);
    }

    public void markAsRead(
            NoticeId noticeId,
            Long userSeq,
            Country userCountry
    ) {
        var notice = noticeQueryService.getById(noticeId);
        authorizationService.validateNoticeReadAccess(notice, userSeq, userCountry);

        var readNotice = notice.markAsRead(userSeq);
        noticeCommandService.save(readNotice);
    }

    @Transactional(readOnly = true)
    public Notice getNoticeById(NoticeId noticeId) {
        return noticeQueryService.getById(noticeId);
    }

    @Transactional(readOnly = true)
    public List<Notice> getNoticesForCountry(Country country) {
        return noticeQueryService.getActiveNoticesForCountry(country);
    }

    @Transactional(readOnly = true)
    public List<Notice> getAllActiveNotices() {
        return noticeQueryService.getAllActiveNotices();
    }

    @Transactional(readOnly = true)
    public List<Notice> getNoticesByType(NoticeType type) {
        return noticeQueryService.getNoticesByType(type);
    }

    @Transactional(readOnly = true)
    public boolean isNoticeReadBy(
            NoticeId noticeId,
            Long userSeq
    ) {
        var notice = noticeQueryService.getById(noticeId);
        return notice.isReadBy(userSeq);
    }
}