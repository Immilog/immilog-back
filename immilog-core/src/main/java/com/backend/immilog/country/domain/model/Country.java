package com.backend.immilog.country.domain.model;

import java.time.LocalDateTime;

public class Country {
    private final String id;
    private final String nameKo;
    private final String nameEn;
    private final String continent;
    private final CountryStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Country(
            String id,
            String nameKo,
            String nameEn,
            String continent,
            CountryStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.nameKo = nameKo;
        this.nameEn = nameEn;
        this.continent = continent;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Country of(
            String id,
            String nameKo,
            String nameEn,
            String continent
    ) {
        return new Country(
                id,
                nameKo,
                nameEn,
                continent,
                CountryStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public String id() {
        return id;
    }

    public String nameKo() {
        return nameKo;
    }

    public String nameEn() {
        return nameEn;
    }

    public String continent() {
        return continent;
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

    public boolean isActive() {
        return status == CountryStatus.ACTIVE;
    }
}