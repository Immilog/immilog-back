package com.backend.immilog.company.infrastructure.repository;

import com.backend.immilog.company.domain.model.Company;
import com.backend.immilog.company.domain.repository.CompanyRepository;
import com.backend.immilog.company.infrastructure.jpa.CompanyJpaEntity;
import com.backend.immilog.company.infrastructure.jpa.CompanyJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CompanyRepositoryImpl implements CompanyRepository {
    private final CompanyJpaRepository companyJpaRepository;

    public CompanyRepositoryImpl(CompanyJpaRepository companyJpaRepository) {
        this.companyJpaRepository = companyJpaRepository;
    }

    @Override
    public Optional<Company> getByCompanyManagerUserId(String userId) {
        return companyJpaRepository
                .findByManager_CompanyManagerUserId(userId)
                .map(CompanyJpaEntity::toDomain);
    }

    @Override
    public Optional<Company> findByManagerUserId(String userId) {
        return companyJpaRepository
                .findByManager_CompanyManagerUserId(userId)
                .map(CompanyJpaEntity::toDomain);
    }

    @Override
    public Optional<Company> findById(String companyId) {
        return companyJpaRepository
                .findById(companyId)
                .map(CompanyJpaEntity::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return companyJpaRepository.existsByCompanyData_CompanyName(name);
    }

    @Override
    public Company save(Company company) {
        CompanyJpaEntity savedEntity = companyJpaRepository.save(CompanyJpaEntity.from(company));
        return savedEntity.toDomain();
    }

    @Override
    public void delete(Company company) {
        companyJpaRepository.deleteById(company.id());
    }
}
