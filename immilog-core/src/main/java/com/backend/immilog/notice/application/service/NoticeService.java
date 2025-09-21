package com.backend.immilog.notice.application.service;

import com.backend.immilog.notice.application.dto.NoticeModelResult;
import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.domain.model.NoticeId;
import com.backend.immilog.notice.domain.repository.NoticeRepository;
import com.backend.immilog.notice.domain.service.NoticeAuthorizationService;
import com.backend.immilog.notice.domain.service.NoticeFactory;
import com.backend.immilog.notice.domain.service.NoticeValidationService;
import com.backend.immilog.notice.exception.NoticeErrorCode;
import com.backend.immilog.notice.exception.NoticeException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeAuthorizationService authorizationService;
    private final NoticeValidationService validationService;
    private final NoticeFactory noticeFactory;

    public NoticeService(
            NoticeRepository noticeRepository,
            NoticeAuthorizationService authorizationService,
            NoticeValidationService validationService,
            NoticeFactory noticeFactory
    ) {
        this.noticeRepository = noticeRepository;
        this.authorizationService = authorizationService;
        this.validationService = validationService;
        this.noticeFactory = noticeFactory;
    }

    public NoticeId createNotice(
            String token,
            String title,
            String content,
            NoticeType type,
            List<String> targetCountries
    ) {
        var author = authorizationService.validateAndGetAuthor(token);
        validationService.validateNoticeCreation(title, content, type, targetCountries);

        var notice = noticeFactory.createNotice(
                author.userId(),
                title,
                content,
                type,
                targetCountries
        );

        var savedNotice = noticeRepository.save(notice);
        return savedNotice.getId();
    }

    public void updateNotice(
            String token,
            NoticeId noticeId,
            String title,
            String content,
            NoticeType type
    ) {
        var notice = getById(noticeId);
        authorizationService.validateNoticeModificationAccess(notice, token);
        validationService.validateNoticeUpdate(notice, title, content);

        var updatedNotice = noticeFactory.updateNoticeContent(notice, title, content, type);
        noticeRepository.save(updatedNotice);
    }

    public void deleteNotice(
            String token,
            NoticeId noticeId
    ) {
        var notice = getById(noticeId);
        authorizationService.validateNoticeModificationAccess(notice, token);

        var deletedNotice = notice.delete();
        noticeRepository.save(deletedNotice);
    }

    public void markAsRead(
            NoticeId noticeId,
            String userId,
            String userCountryId
    ) {
        var notice = getById(noticeId);
        authorizationService.validateNoticeReadAccess(notice, userId, userCountryId);

        var readNotice = notice.markAsRead(userId);
        noticeRepository.save(readNotice);
    }

    @Transactional(readOnly = true)
    public Page<NoticeModelResult> getNotices(String userId, Pageable pageable) {
        return noticeRepository.getNotices(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Notice getById(NoticeId noticeId) {
        return noticeRepository.findById(noticeId.value())
                .orElseThrow(() -> new NoticeException(NoticeErrorCode.NOTICE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Notice getNoticeById(NoticeId noticeId) {
        return getById(noticeId);
    }

    @Transactional(readOnly = true)
    public Notice getNoticeById(String noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NoticeException(NoticeErrorCode.NOTICE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Notice> getNoticesForCountry(String countryId) {
        return noticeRepository.findActiveNoticesForCountryId(countryId);
    }

    @Transactional(readOnly = true)
    public List<Notice> getAllActiveNotices() {
        return noticeRepository.findAllActiveNotices();
    }

    @Transactional(readOnly = true)
    public List<Notice> getNoticesByType(NoticeType type) {
        return noticeRepository.findByType(type);
    }

    @Transactional(readOnly = true)
    public List<Notice> getNoticesByAuthor(String authorUserId) {
        return noticeRepository.findByAuthorUserId(authorUserId);
    }

    @Transactional(readOnly = true)
    public boolean isNoticeReadBy(NoticeId noticeId, String userId) {
        var notice = getById(noticeId);
        return notice.isReadBy(userId);
    }

    @Transactional(readOnly = true)
    public boolean existsById(NoticeId noticeId) {
        return noticeRepository.existsById(noticeId.value());
    }

    @Transactional(readOnly = true)
    public Boolean areUnreadNoticesExist(String countryId, String id) {
        return noticeRepository.areUnreadNoticesExist(countryId, id);
    }

    public void delete(Notice notice) {
        noticeRepository.delete(notice);
    }

    public void deleteById(String noticeId) {
        noticeRepository.deleteById(noticeId);
    }
}