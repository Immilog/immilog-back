package com.backend.immilog.notice.domain.model;

import com.backend.immilog.notice.exception.NoticeErrorCode;
import com.backend.immilog.notice.exception.NoticeException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public record NoticeTargeting(List<String> targetCountryIds) {

    public static NoticeTargeting of(List<String> targetCountryIds) {
        if (targetCountryIds == null || targetCountryIds.isEmpty()) {
            throw new NoticeException(NoticeErrorCode.INVALID_NOTICE_TARGET_COUNTRIES);
        }

        var uniqueCountries = new HashSet<>(targetCountryIds);
        return new NoticeTargeting(new ArrayList<>(uniqueCountries));
    }

    // TODO : 모든 국가 ID 목록 받아서 인자로 넣기
    public static NoticeTargeting all() {
        return new NoticeTargeting(List.of());
    }

    public boolean isTargetedTo(String countryId) {
        return this.targetCountryIds().contains(countryId);
    }

    public boolean isGlobal() {
        return this.targetCountryIds.size() > 1;
    }

    public int getTargetCount() {
        return targetCountryIds.size();
    }
}