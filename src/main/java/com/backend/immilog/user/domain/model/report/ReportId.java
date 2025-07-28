package com.backend.immilog.user.domain.model.report;

import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;

public record ReportId(Long value) {
    public ReportId {
        if (value == null || value <= 0) {
            throw new UserException(UserErrorCode.ENTITY_TO_DOMAIN_ERROR);
        }
    }

    public static ReportId of(Long value) {
        return new ReportId(value);
    }

    public boolean equals(ReportId other) {
        return other != null && this.value.equals(other.value);
    }
}