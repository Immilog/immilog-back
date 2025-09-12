package com.backend.immilog.country.domain;

public enum CountryStatus {
    ACTIVE,
    INACTIVE;

    public boolean isActive() {
        return this == ACTIVE;
    }
}