package com.backend.immilog.country.domain;

import com.backend.immilog.country.exception.CountryErrorCode;
import com.backend.immilog.country.exception.CountryException;

public record CountryInfo(
        String nameKo,
        String nameEn,
        String continent
) {
    public CountryInfo {
        validateNameKo(nameKo);
        validateNameEn(nameEn);
        validateContinent(continent);
    }

    public static CountryInfo of(String nameKo, String nameEn, String continent) {
        return new CountryInfo(nameKo, nameEn, continent);
    }

    private void validateNameKo(String nameKo) {
        if (nameKo == null || nameKo.trim().isEmpty()) {
            throw new CountryException(CountryErrorCode.INVALID_COUNTRY_NAME);
        }
        if (nameKo.length() > 50) {
            throw new CountryException(CountryErrorCode.INVALID_COUNTRY_NAME);
        }
    }

    private void validateNameEn(String nameEn) {
        if (nameEn == null || nameEn.trim().isEmpty()) {
            throw new CountryException(CountryErrorCode.INVALID_COUNTRY_NAME);
        }
        if (nameEn.length() > 100) {
            throw new CountryException(CountryErrorCode.INVALID_COUNTRY_NAME);
        }
    }

    private void validateContinent(String continent) {
        if (continent == null || continent.trim().isEmpty()) {
            throw new CountryException(CountryErrorCode.INVALID_CONTINENT);
        }
        if (continent.length() > 30) {
            throw new CountryException(CountryErrorCode.INVALID_CONTINENT);
        }
    }

    public boolean isAsianCountry() {
        return "Asia".equalsIgnoreCase(continent);
    }

    public boolean isEuropeanCountry() {
        return "Europe".equalsIgnoreCase(continent);
    }
}