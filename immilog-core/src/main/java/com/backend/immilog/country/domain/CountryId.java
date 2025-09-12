package com.backend.immilog.country.domain;

import com.backend.immilog.country.exception.CountryErrorCode;
import com.backend.immilog.country.exception.CountryException;

public record CountryId(String value) {
    public CountryId {
        if (value == null || value.isBlank()) {
            throw new CountryException(CountryErrorCode.INVALID_COUNTRY_ID);
        }
        if (value.length() > 10) {
            throw new CountryException(CountryErrorCode.INVALID_COUNTRY_ID);
        }
    }

    public static CountryId of(String value) {
        return new CountryId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}