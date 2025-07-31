package com.backend.immilog.jobboard.domain.model;

import java.math.BigDecimal;

public record Salary(BigDecimal amount, String currency) {
    public Salary {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Salary amount cannot be null or negative");
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency cannot be null or empty");
        }
    }

    public static Salary of(BigDecimal amount, String currency) {
        return new Salary(amount, currency.toUpperCase());
    }

    public static Salary krw(BigDecimal amount) {
        return new Salary(amount, "KRW");
    }

    public boolean isGreaterThan(Salary other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot compare salaries with different currencies");
        }
        return this.amount.compareTo(other.amount) > 0;
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}