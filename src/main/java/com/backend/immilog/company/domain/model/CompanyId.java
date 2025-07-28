package com.backend.immilog.company.domain.model;

public record CompanyId(Long value) {

    public static CompanyId of(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("CompanyId value must be positive");
        }
        return new CompanyId(value);
    }

    public static CompanyId generate() {
        return new CompanyId(null);
    }
}