package com.backend.immilog.notice.application.services;

import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.domain.repository.NoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NoticeCommandService {
    private final NoticeRepository noticeRepository;

    public NoticeCommandService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    @Transactional
    public Notice save(Notice notice) {
        return noticeRepository.save(notice);
    }

    @Transactional
    public void delete(Notice notice) {
        noticeRepository.delete(notice);
    }

    @Transactional
    public void deleteById(Long noticeId) {
        noticeRepository.deleteById(noticeId);
    }
}
