package com.backend.immilog.user.infrastructure.jpa.repositories;

import com.backend.immilog.user.infrastructure.jpa.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyJpaRepository extends JpaRepository<CompanyEntity, Long> {
    Optional<CompanyEntity> findByCompanyManagerUserSeq(
            Long userSeq
    );
}

