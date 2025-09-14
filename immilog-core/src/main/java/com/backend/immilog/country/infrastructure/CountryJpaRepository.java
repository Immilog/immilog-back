package com.backend.immilog.country.infrastructure;

import com.backend.immilog.country.domain.CountryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CountryJpaRepository extends JpaRepository<CountryEntity, String> {
    List<CountryEntity> findByStatus(CountryStatus status);
    
    Optional<CountryEntity> findByIdAndStatus(String id, CountryStatus status);
}