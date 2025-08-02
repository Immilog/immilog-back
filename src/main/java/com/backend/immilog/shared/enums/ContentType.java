package com.backend.immilog.shared.enums;

import com.backend.immilog.shared.exception.CommonErrorCode;
import com.backend.immilog.shared.exception.CustomException;

import java.util.Arrays;

public enum ContentType {
    POST("posts"),
    JOB_BOARD("jobboards"),
    COMMENT("comments");

    private final String postType;

    ContentType(String postType) {
        this.postType = postType;
    }

    public static ContentType convertToEnum(String postType) {
        return Arrays.stream(ContentType.values())
                .filter(type -> postType.compareToIgnoreCase(type.name()) == 0)
                .findFirst()
                .orElseThrow(() -> new CustomException(CommonErrorCode.INVALID_CONTENT_TYPE));
    }
}
