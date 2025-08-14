package com.backend.immilog.country.presentation.controller;

import com.backend.immilog.country.application.services.CountryQueryService;
import com.backend.immilog.country.presentation.payload.CountryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
public class CountryController {
    private final CountryQueryService countryQueryService;

    public CountryController(CountryQueryService countryQueryService) {
        this.countryQueryService = countryQueryService;
    }

    @GetMapping
    public ResponseEntity<List<CountryResponse>> getActiveCountries() {
        var countries = countryQueryService.getActiveCountries();
        return ResponseEntity.ok(CountryResponse.fromList(countries));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CountryResponse>> getAllCountries() {
        var countries = countryQueryService.getAllCountries();
        return ResponseEntity.ok(CountryResponse.fromList(countries));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CountryResponse> getCountry(@PathVariable String id) {
        var country = countryQueryService.getActiveCountryById(id);
        return ResponseEntity.ok(CountryResponse.from(country.get()));
    }
}