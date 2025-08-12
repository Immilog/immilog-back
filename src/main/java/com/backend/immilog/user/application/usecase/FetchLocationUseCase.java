package com.backend.immilog.user.application.usecase;

import com.backend.immilog.user.application.result.LocationResult;
import com.backend.immilog.user.infrastructure.gateway.GeocodeGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public interface FetchLocationUseCase {
    CompletableFuture<LocationResult> getCountry(
            Double latitude,
            Double longitude
    );

    LocationResult joinCompletableFutureLocation(CompletableFuture<LocationResult> countryFuture);

    @Slf4j
    @Service
    class LocationFetcher implements FetchLocationUseCase {
        private final GeocodeGateway geocodeGateway;
        private final ObjectMapper objectMapper;

        public LocationFetcher(
                GeocodeGateway geocodeGateway,
                ObjectMapper objectMapper
        ) {
            this.geocodeGateway = geocodeGateway;
            this.objectMapper = objectMapper;
        }

        @Async
        @Override
        public CompletableFuture<LocationResult> getCountry(
                Double latitude,
                Double longitude
        ) {
            if (latitude <= 0.0 || longitude <= 0.0) {
                return CompletableFuture.completedFuture(null);
            }
            try {
                var response = CompletableFuture.supplyAsync(() -> geocodeGateway.fetchGeocode(latitude, longitude));
                var locationInfo = extractLocationInfo(response.join());
                return CompletableFuture.completedFuture(locationInfo);
            } catch (Exception e) {
                log.error("Geocoder API 호출 중 예외 발생", e);
            }
            return CompletableFuture.completedFuture(new LocationResult("기타", "기타 지역"));
        }

        private LocationResult extractLocationInfo(String jsonResponse) {
            try {
                var rootNode = objectMapper.readTree(jsonResponse);
                var resultsArray = rootNode.path("results");

                if (resultsArray.isArray() && !resultsArray.isEmpty()) {
                    // 첫 번째 결과에서 address_components 추출
                    var firstResult = resultsArray.get(0);
                    var addressComponents = firstResult.path("address_components");

                    String countryCode = null;
                    String city = null;

                    // address_components에서 국가 코드와 도시 정보 추출
                    for (var component : addressComponents) {
                        var types = component.path("types");

                        // 국가 코드 추출 (types에 "countryId"가 포함된 경우)
                        if (types.isArray()) {
                            for (var type : types) {
                                if ("countryId".equals(type.asText())) {
                                    countryCode = component.path("short_name").asText(); // "KR"
                                    break;
                                }
                            }
                        }

                        // 도시 정보 추출 (types에 "administrative_area_level_1"이 포함된 경우)
                        if (types.isArray()) {
                            for (var type : types) {
                                if ("administrative_area_level_1".equals(type.asText())) {
                                    city = component.path("long_name").asText(); // "서울특별시"
                                    break;
                                }
                            }
                        }
                    }

                    return new LocationResult(
                            countryCode != null ? countryCode : "기타",
                            city != null ? city : "기타 지역"
                    );
                }
            } catch (IOException e) {
                log.error("JSON 파싱 중 예외 발생", e);
            }
            return new LocationResult("기타", "기타 지역");
        }

        @Override
        public LocationResult joinCompletableFutureLocation(CompletableFuture<LocationResult> countryFuture) {
            try {
                return countryFuture.get(5, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.error("CompletableFuture 조인 중 예외 발생", e);
                return new LocationResult("기타", "기타 지역");
            }
        }
    }
}
