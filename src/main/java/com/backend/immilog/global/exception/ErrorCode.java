package com.backend.immilog.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    //undefined
    UNDEFINED_EXCEPTION(BAD_REQUEST, "알 수 없는 오류가 발생하였습니다."),

    //user
    USER_NOT_FOUND(NOT_FOUND, "존재하지 않는 사용자입니다."),
    EXISTING_USER(BAD_REQUEST, "이미 존재하는 사용자입니다."),
    PASSWORD_NOT_MATCH(BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    USER_STATUS_NOT_ACTIVE(BAD_REQUEST, "사용자 상태가 활성화되어 있지 않습니다."),
    LOCATION_NOT_MATCH(BAD_REQUEST, "현재 위치가 입력하신 위치와 일치하지 않습니다."),

    //image
    IMAGE_UPLOAD_FAILED(BAD_REQUEST, "이미지 업로드에 실패하였습니다."),

    //post
    POST_NOT_FOUND(NOT_FOUND, "존재하지 않는 게시물입니다."),
    NO_AUTHORITY(BAD_REQUEST, "본인이 작성한 글만 수정하거나 삭제할 수 있습니다."),
    ALREADY_DELETED_POST(BAD_REQUEST, "이미 삭제된 게시물입니다."),

    COMMENT_NOT_FOUND(NOT_FOUND, "존재하지 않는 댓글입니다."),
    CHAT_ROOM_NOT_FOUND(NOT_FOUND, "존재하지 않는 채팅방입니다."),
    INVALID_USER(BAD_REQUEST, "해당 채팅방에 대한 권한이 없습니다."),
    EMAIL_SEND_FAILED(BAD_REQUEST, "이메일 발송에 실패하였습니다."),

    ALREADY_REPORTED(BAD_REQUEST, "이미 신고한 사용자입니다."),
    CANNOT_REPORT_MYSELF(BAD_REQUEST, "자기 자신은 신고할 수 없습니다."),

    NOT_AN_ADMIN_USER(BAD_REQUEST, "관리자 권한이 없는 사용자입니다."),

    NOTICE_NOT_FOUND(NOT_FOUND, "존재하지 않는 공지사항입니다."),
    NOT_ELIGIBLE_COUNTRY(BAD_REQUEST, "해당 국가에 대한 공지사항이 아닙니다."),
    NOTICE_ALREADY_DELETED(BAD_REQUEST, "이미 삭제된 공지사항입니다."),

    JOB_BOARD_NOT_FOUND(NOT_FOUND, "존재하지 않는 구인 게시판입니다."),
    COMPANY_NOT_FOUND(NOT_FOUND, "존재하지 않는 회사입니다."),

    FAILED_TO_SAVE_POST(BAD_REQUEST, "게시물을 저장하는데 실패하였습니다."),

    INVALID_REFERENCE_TYPE(BAD_REQUEST, "유효하지 않은 참조 타입입니다.");

    private final HttpStatus status;
    private final String message;

}
