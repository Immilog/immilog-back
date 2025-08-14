package com.backend.immilog.company.domain.model;

public record CompanyMetaData(
        Industry industry,
        String name,
        String email,
        String phone,
        String address,
        String homepage,
        String logo
) {
    public static CompanyMetaData of(
            Industry industry,
            String name,
            String email,
            String phone,
            String address,
            String homepage,
            String logo
    ) {
        validateParameters(industry, name, email, phone);
        return new CompanyMetaData(industry, name, email, phone, address, homepage, logo);
    }

    private static void validateParameters(
            Industry industry,
            String name,
            String email,
            String phone
    ) {
        if (industry == null) {
            throw new IllegalArgumentException("Industry cannot be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Company name cannot be null or empty");
        }
        if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (phone != null && !phone.matches("^[+]?[0-9\\s\\-()]+$")) {
            throw new IllegalArgumentException("Invalid phone format");
        }
    }

    public static CompanyMetaData createEmpty() {
        return new CompanyMetaData(null, null, null, null, null, null, null);
    }

    public CompanyMetaData withIndustry(Industry newIndustry) {
        return new CompanyMetaData(newIndustry, name, email, phone, address, homepage, logo);
    }

    public CompanyMetaData withName(String newName) {
        return new CompanyMetaData(industry, newName, email, phone, address, homepage, logo);
    }

    public CompanyMetaData withEmail(String newEmail) {
        return new CompanyMetaData(industry, name, newEmail, phone, address, homepage, logo);
    }

    public CompanyMetaData withPhone(String newPhone) {
        return new CompanyMetaData(industry, name, email, newPhone, address, homepage, logo);
    }

    public CompanyMetaData withAddress(String newAddress) {
        return new CompanyMetaData(industry, name, email, phone, newAddress, homepage, logo);
    }

    public CompanyMetaData withHomepage(String newHomepage) {
        return new CompanyMetaData(industry, name, email, phone, address, newHomepage, logo);
    }

    public CompanyMetaData withLogo(String newLogo) {
        return new CompanyMetaData(industry, name, email, phone, address, homepage, newLogo);
    }
}