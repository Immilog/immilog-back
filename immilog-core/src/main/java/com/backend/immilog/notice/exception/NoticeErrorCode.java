package com.backend.immilog.notice.exception;

import com.backend.immilog.shared.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
public enum NoticeErrorCode implements ErrorCode {
    NOT_AN_ADMIN_USER(BAD_REQUEST, "관리자 권한이 없는 사용자입니다."),
    NOTICE_NOT_FOUND(NOT_FOUND, "존재하지 않는 공지사항입니다."),
    SQL_ERROR(BAD_REQUEST, "SQL 에러가 발생했습니다."),
    INVALID_NOTICE_TITLE(BAD_REQUEST, "공지사항 제목이 유효하지 않습니다."),
    NOTICE_TITLE_TOO_LONG(BAD_REQUEST, "공지사항 제목이 너무 깁니다."),
    INVALID_NOTICE_CONTENT(BAD_REQUEST, "공지사항 내용이 유효하지 않습니다."),
    NOTICE_CONTENT_TOO_LONG(BAD_REQUEST, "공지사항 내용이 너무 깁니다."),
    INVALID_NOTICE_AUTHOR(BAD_REQUEST, "공지사항 작성자가 유효하지 않습니다."),
    INVALID_NOTICE_TYPE(BAD_REQUEST, "공지사항 타입이 유효하지 않습니다."),
    INVALID_NOTICE_TARGET_COUNTRIES(BAD_REQUEST, "공지사항 대상 국가가 유효하지 않습니다."),
    INVALID_USER_SEQ(BAD_REQUEST, "사용자 ID가 유효하지 않습니다."),
    NOTICE_ALREADY_DELETED(BAD_REQUEST, "이미 삭제된 공지사항입니다.");

    private final HttpStatus status;
    private final String message;

    NoticeErrorCode(
            HttpStatus status,
            String message
    ) {
        this.status = status;
        this.message = message;
    }

}
