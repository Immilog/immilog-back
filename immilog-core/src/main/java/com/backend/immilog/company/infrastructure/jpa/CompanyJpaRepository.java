package com.backend.immilog.company.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyJpaRepository extends JpaRepository<CompanyJpaEntity, String> {
    Optional<CompanyJpaEntity> findByManager_CompanyManagerUserId(String userId);

    boolean existsByCompanyData_CompanyName(String name);
}

