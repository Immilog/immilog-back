package com.backend.immilog.user.presentation.controller;

import com.backend.immilog.country.application.CountryQueryService;
import com.backend.immilog.user.application.usecase.FetchLocationUseCase;
import com.backend.immilog.user.presentation.payload.UserLocationPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@Tag(name = "Location API", description = "위치 관련 API")
@RequestMapping("/api/v1/locations")
@RestController
@RequiredArgsConstructor
public class LocationController {
    private final FetchLocationUseCase locationFetcher;
    private final CountryQueryService countryQueryService;

    @GetMapping
    @Operation(summary = "위치 정보", description = "위치 정보를 가져옵니다.")
    public ResponseEntity<UserLocationPayload.UserLocationResponse> getLocation(
            @Parameter(description = "위도") @RequestParam("latitude") Double latitude,
            @Parameter(description = "경도") @RequestParam("longitude") Double longitude
    ) {
        var locationResult = locationFetcher.getCountry(latitude, longitude).join();
        var countries = countryQueryService.getActiveCountries();
        var country = countries.stream()
                .filter(c -> c.id().value().equals(locationResult.country()))
                .findFirst();
        var countryId = country.map(c -> c.id().value()).orElse("ETC");
        var body = new UserLocationPayload.UserLocationResponse(countryId, locationResult.city());
        return ResponseEntity.status(OK).body(body);
    }
}