package com.backend.immilog.country.domain;

import java.util.List;
import java.util.Optional;

public interface CountryRepository {
    List<Country> findAll();
    
    List<Country> findAllActive();
    
    Optional<Country> findById(String id);
    
    Optional<Country> findByIdAndActive(String id);
    
    Country save(Country country);
    
    void deleteById(String id);
}