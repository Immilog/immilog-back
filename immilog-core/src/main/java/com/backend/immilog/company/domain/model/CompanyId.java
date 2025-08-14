package com.backend.immilog.company.domain.model;

public record CompanyId(String value) {

    public static CompanyId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CompanyId value must be not null or empty");
        }
        return new CompanyId(value);
    }

    public static CompanyId generate() {
        return new CompanyId(null);
    }
}