package com.backend.immilog.company.domain.repository;

import com.backend.immilog.company.domain.model.Company;

import java.util.Optional;

public interface CompanyRepository {
    Optional<Company> getByCompanyManagerUserSeq(Long userSeq);

    Optional<Company> findByManagerUserSeq(Long userSeq);

    Optional<Company> findById(Long companyId);

    boolean existsByName(String name);

    Company save(Company company);

    void delete(Company company);
}
