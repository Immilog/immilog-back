package com.backend.immilog.country.infrastructure.jpa;

import com.backend.immilog.country.domain.model.CountryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CountryJpaRepository extends JpaRepository<CountryEntity, String> {
    List<CountryEntity> findByStatus(CountryStatus status);
    
    Optional<CountryEntity> findByIdAndStatus(String id, CountryStatus status);
}