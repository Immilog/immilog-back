package com.backend.immilog.shared.enums;

import com.backend.immilog.shared.exception.CommonErrorCode;
import com.backend.immilog.shared.exception.CustomException;

import java.util.Arrays;

public enum ContentType {
    POST("posts"),
    JOB_BOARD("jobboards"),
    COMMENT("comments");

    private final String contentType;

    ContentType(String contentType) {
        this.contentType = contentType;
    }

    public static ContentType convertToEnum(String contentTypeString) {
        return Arrays.stream(ContentType.values())
                .filter(type -> contentTypeString.compareToIgnoreCase(type.name()) == 0)
                .findFirst()
                .orElseThrow(() -> new CustomException(CommonErrorCode.INVALID_CONTENT_TYPE));
    }
}
