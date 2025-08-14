package com.backend.immilog.jobboard.domain.model;

public record JobBoardCompany(
        String name,
        String location,
        Industry industry
) {
    public static JobBoardCompany of(
            String name,
            String location,
            Industry industry
    ) {
        return new JobBoardCompany(name, location, industry);
    }
}