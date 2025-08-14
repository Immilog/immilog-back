package com.backend.immilog.company.domain.repository;

import com.backend.immilog.company.domain.model.Company;

import java.util.Optional;

public interface CompanyRepository {
    Optional<Company> getByCompanyManagerUserId(String userId);

    Optional<Company> findByManagerUserId(String userId);

    Optional<Company> findById(String companyId);

    boolean existsByName(String name);

    Company save(Company company);

    void delete(Company company);
}
