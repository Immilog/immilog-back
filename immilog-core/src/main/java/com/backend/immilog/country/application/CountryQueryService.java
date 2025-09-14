package com.backend.immilog.country.application;

import com.backend.immilog.country.domain.Country;
import com.backend.immilog.country.domain.CountryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CountryQueryService {
    private final CountryRepository countryRepository;

    public CountryQueryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    public List<Country> getActiveCountries() {
        return countryRepository.findAllActive();
    }

    public Optional<Country> getCountryById(String id) {
        return countryRepository.findById(id);
    }

    public Optional<Country> getActiveCountryById(String id) {
        return countryRepository.findByIdAndActive(id);
    }
}