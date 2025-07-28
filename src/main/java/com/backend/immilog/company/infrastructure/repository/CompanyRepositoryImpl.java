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
    public Optional<Company> getByCompanyManagerUserSeq(Long userSeq) {
        return companyJpaRepository
                .findByManager_CompanyManagerUserSeq(userSeq)
                .map(CompanyJpaEntity::toDomain);
    }

    @Override
    public Optional<Company> findByManagerUserSeq(Long userSeq) {
        return companyJpaRepository
                .findByManager_CompanyManagerUserSeq(userSeq)
                .map(CompanyJpaEntity::toDomain);
    }

    @Override
    public Optional<Company> findById(Long companyId) {
        return companyJpaRepository
                .findById(companyId)
                .map(CompanyJpaEntity::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return companyJpaRepository.existsByCompanyMetaData_CompanyName(name);
    }

    @Override
    public Company save(Company company) {
        CompanyJpaEntity savedEntity = companyJpaRepository.save(CompanyJpaEntity.from(company));
        return savedEntity.toDomain();
    }

    @Override
    public void delete(Company company) {
        companyJpaRepository.deleteById(company.seq());
    }
}
