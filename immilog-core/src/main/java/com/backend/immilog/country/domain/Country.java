package com.backend.immilog.country.domain;

import com.backend.immilog.country.exception.CountryErrorCode;
import com.backend.immilog.country.exception.CountryException;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Country {
    private final CountryId id;
    private final CountryInfo info;
    private CountryStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    private Country(
            CountryId id,
            CountryInfo info,
            CountryStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.info = info;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Country create(
            CountryId id,
            CountryInfo info
    ) {
        validateCreationParameters(id, info);
        LocalDateTime now = LocalDateTime.now();
        return new Country(id, info, CountryStatus.ACTIVE, now, now);
    }

    public static Country restore(
            CountryId id,
            CountryInfo info,
            CountryStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Country(id, info, status, createdAt, updatedAt);
    }

    public static Country of(
            String id,
            String nameKo,
            String nameEn,
            String continent
    ) {
        return create(
                CountryId.of(id),
                CountryInfo.of(nameKo, nameEn, continent)
        );
    }

    public Country activate() {
        if (isActive()) {
            return this;
        }
        this.status = CountryStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public Country deactivate() {
        if (!isActive()) {
            return this;
        }

        this.status = CountryStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public boolean isActive() {
        return status.isActive();
    }

    public boolean canBeDeactivated() {
        return isActive();
    }

    public boolean canBeActivated() {
        return !isActive();
    }

    public boolean isAsianCountry() {
        return info.isAsianCountry();
    }

    public boolean isEuropeanCountry() {
        return info.isEuropeanCountry();
    }

    public CountryId id() {
        return id;
    }

    public String nameKo() {
        return info.nameKo();
    }

    public String nameEn() {
        return info.nameEn();
    }

    public String continent() {
        return info.continent();
    }

    public CountryInfo info() {
        return info;
    }

    public CountryStatus status() {
        return status;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    public LocalDateTime updatedAt() {
        return updatedAt;
    }

    private static void validateCreationParameters(
            CountryId id,
            CountryInfo info
    ) {
        if (id == null) {
            throw new CountryException(CountryErrorCode.INVALID_COUNTRY_ID);
        }
        if (info == null) {
            throw new CountryException(CountryErrorCode.INVALID_COUNTRY_NAME);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return Objects.equals(id, country.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", info=" + info +
                ", status=" + status +
                '}';
    }
}