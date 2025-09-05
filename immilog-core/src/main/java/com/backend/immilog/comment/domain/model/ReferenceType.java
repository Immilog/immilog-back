package com.backend.immilog.comment.domain.model;

import com.backend.immilog.comment.exception.CommentErrorCode;
import com.backend.immilog.comment.exception.CommentException;

import java.util.Arrays;

public enum ReferenceType {
    COMMENT,
    POST;

    public static ReferenceType getByString(String referenceType) {
        return Arrays.stream(ReferenceType.values())
                .filter(type -> type.name().compareToIgnoreCase(referenceType) == 0)
                .findFirst()
                .orElseThrow(() -> new CommentException(CommentErrorCode.INVALID_REFERENCE_TYPE));
    }
}