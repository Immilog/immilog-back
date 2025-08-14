package com.backend.immilog.image.exception

import com.backend.immilog.shared.exception.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND

enum class ImageErrorCode(
    private val status: HttpStatus,
    private val message: String
) : ErrorCode {
    IMAGE_NOT_FOUND(NOT_FOUND, "이미지를 찾을 수 없습니다."),
    INVALID_IMAGE_PATH(BAD_REQUEST, "이미지 경로가 올바르지 않습니다."),
    INVALID_IMAGE_PATH_FORMAT(BAD_REQUEST, "이미지 경로 형식이 올바르지 않습니다."),
    INVALID_IMAGE_TYPE(BAD_REQUEST, "이미지 타입이 올바르지 않습니다."),
    INVALID_IMAGE_FILE(BAD_REQUEST, "이미지 파일이 올바르지 않습니다."),
    IMAGE_FILE_TOO_LARGE(BAD_REQUEST, "이미지 파일 크기가 너무 큽니다."),
    UNSUPPORTED_IMAGE_FORMAT(BAD_REQUEST, "지원하지 않는 이미지 형식입니다."),
    NO_FILES_PROVIDED(BAD_REQUEST, "업로드할 파일이 없습니다."),
    IMAGE_ALREADY_DELETED(BAD_REQUEST, "이미 삭제된 이미지입니다."),
    IMAGE_SAVE_FAILED(BAD_REQUEST, "이미지 저장에 실패했습니다.");

    override fun getStatus(): HttpStatus = status
    override fun getMessage(): String = message

}
