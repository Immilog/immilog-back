package com.backend.immilog.country.infrastructure.repositories;

import com.backend.immilog.country.domain.model.Country;
import com.backend.immilog.country.domain.model.CountryStatus;
import com.backend.immilog.country.domain.repositories.CountryRepository;
import com.backend.immilog.country.infrastructure.jpa.CountryEntity;
import com.backend.immilog.country.infrastructure.jpa.CountryJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CountryRepositoryImpl implements CountryRepository {
    private final CountryJpaRepository countryJpaRepository;

    public CountryRepositoryImpl(CountryJpaRepository countryJpaRepository) {
        this.countryJpaRepository = countryJpaRepository;
    }

    @Override
    public List<Country> findAll() {
        return countryJpaRepository.findAll()
                .stream()
                .map(CountryEntity::toDomain)
                .toList();
    }

    @Override
    public List<Country> findAllActive() {
        return countryJpaRepository.findByStatus(CountryStatus.ACTIVE)
                .stream()
                .map(CountryEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Country> findById(String id) {
        return countryJpaRepository.findById(id)
                .map(CountryEntity::toDomain);
    }

    @Override
    public Optional<Country> findByIdAndActive(String id) {
        return countryJpaRepository.findByIdAndStatus(id, CountryStatus.ACTIVE)
                .map(CountryEntity::toDomain);
    }

    @Override
    public Country save(Country country) {
        CountryEntity entity = countryJpaRepository.save(CountryEntity.from(country));
        return entity.toDomain();
    }

    @Override
    public void deleteById(String id) {
        countryJpaRepository.deleteById(id);
    }
}