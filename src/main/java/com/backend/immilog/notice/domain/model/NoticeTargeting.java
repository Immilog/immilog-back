package com.backend.immilog.notice.domain.model;

import com.backend.immilog.notice.exception.NoticeErrorCode;
import com.backend.immilog.notice.exception.NoticeException;
import com.backend.immilog.shared.enums.Country;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public record NoticeTargeting(List<Country> targetCountries) {

    public static NoticeTargeting of(List<Country> targetCountries) {
        if (targetCountries == null || targetCountries.isEmpty()) {
            throw new NoticeException(NoticeErrorCode.INVALID_NOTICE_TARGET_COUNTRIES);
        }

        var uniqueCountries = new HashSet<>(targetCountries);
        return new NoticeTargeting(new ArrayList<>(uniqueCountries));
    }

    public static NoticeTargeting all() {
        return new NoticeTargeting(List.of(Country.values()));
    }

    public boolean isTargetedTo(Country country) {
        return targetCountries.contains(country);
    }

    public boolean isGlobal() {
        return targetCountries.size() == Country.values().length;
    }

    public int getTargetCount() {
        return targetCountries.size();
    }
}