package com.backend.immilog.jobboard.domain.model;

public enum WorkType {
    FULL_TIME("정규직"),
    PART_TIME("파트타임"),
    CONTRACT("계약직"),
    FREELANCE("프리랜서"),
    INTERNSHIP("인턴십");

    private final String displayName;

    WorkType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}