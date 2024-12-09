package com.backend.immilog.notice.application.services;

import com.backend.immilog.notice.application.result.NoticeResult;
import com.backend.immilog.notice.domain.model.enums.NoticeCountry;
import com.backend.immilog.notice.domain.repositories.NoticeRepository;
import com.backend.immilog.notice.exception.NoticeException;
import com.backend.immilog.user.application.services.query.UserQueryService;
import com.backend.immilog.user.domain.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.immilog.notice.exception.NoticeErrorCode.NOTICE_NOT_FOUND;
import static com.backend.immilog.user.exception.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class NoticeInquiryService {
    private final NoticeRepository noticeRepository;
    private final UserQueryService userQueryService;

    @Transactional(readOnly = true)
    public Page<NoticeResult> getNotices(
            Long userSeq,
            Integer page
    ) {
        if (userSeq == null) {
            return Page.empty();
        }
        Pageable pageable = PageRequest.of(page, 10);
        return noticeRepository.getNotices(userSeq, pageable);
    }

    @Transactional(readOnly = true)
    public NoticeResult getNoticeDetail(
            Long noticeSeq
    ) {
        return noticeRepository.findBySeq(noticeSeq)
                .map(NoticeResult::from)
                .orElseThrow(() -> new NoticeException(NOTICE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Boolean isUnreadNoticeExist(
            Long userSeq
    ) {
        User user = userQueryService.getUserById(userSeq)
                .orElseThrow(() -> new NoticeException(USER_NOT_FOUND));
        return noticeRepository.areUnreadNoticesExist(
                NoticeCountry.valueOf(user.getCountry().name()),
                user.getSeq()
        );
    }
}
