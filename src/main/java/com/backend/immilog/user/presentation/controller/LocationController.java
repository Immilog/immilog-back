package com.backend.immilog.user.presentation.controller;

import com.backend.immilog.global.enums.GlobalCountry;
import com.backend.immilog.user.application.services.LocationService;
import com.backend.immilog.user.presentation.response.LocationResponse;
import com.backend.immilog.user.presentation.response.UserApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@Tag(name = "Location API", description = "위치 관련 API")
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@RestController
public class LocationController {
    private final LocationService locationService;

    /**
     * 위치 정보
     */
    @GetMapping
    @Operation(summary = "위치 정보", description = "위치 정보를 가져옵니다.")
    public ResponseEntity<UserApiResponse> getLocation(
            @RequestParam Double latitude,
            @RequestParam Double longitude
    ) {
        Pair<String, String> country =
                locationService.getCountry(latitude, longitude).join();

        return ResponseEntity
                .status(OK)
                .body(UserApiResponse.of(
                                LocationResponse.from(
                                        GlobalCountry.getCountryByKoreanName(country.getFirst()),
                                        country.getSecond()
                                )
                        )
                );
    }
}