package com.backend.immilog.user.application.usecase;

import com.backend.immilog.user.application.result.LocationResult;
import com.backend.immilog.user.infrastructure.gateway.GeocodeGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@DisplayName("FetchLocationUseCase 테스트")
class FetchLocationUseCaseTest {

    private final GeocodeGateway geocodeGateway = mock(GeocodeGateway.class);
    private FetchLocationUseCase locationFetcher;

    @BeforeEach
    void setUp() {
        locationFetcher = new FetchLocationUseCase.LocationFetcher(geocodeGateway);
    }

    private String createValidGeocodeResponse() {
        return """
                {
                    "plus_code": {
                        "compound_code": "8Q98+5V Seoul, South Korea"
                    },
                    "results": []
                }
                """;
    }

    private String createGeocodeResponseWithCity(String compoundCode) {
        return String.format("""
                {
                    "plus_code": {
                        "compound_code": "%s"
                    },
                    "results": []
                }
                """, compoundCode);
    }

    @Test
    @DisplayName("정상적인 좌표로 위치 정보를 조회할 수 있다")
    void getCountrySuccessfully() throws Exception {
        // given
        double latitude = 37.5665;
        double longitude = 126.9780;
        String geocodeResponse = createValidGeocodeResponse();

        given(geocodeGateway.fetchGeocode(latitude, longitude)).willReturn(geocodeResponse);

        // when
        CompletableFuture<LocationResult> result = locationFetcher.getCountry(latitude, longitude);
        LocationResult locationResult = result.get();

        // then
        assertThat(locationResult).isNotNull();
        assertThat(locationResult.country()).isEqualTo("Seoul,");
        assertThat(locationResult.city()).isEqualTo("South");
        verify(geocodeGateway).fetchGeocode(latitude, longitude);
    }

    @Test
    @DisplayName("0 이하의 위도로 조회 시 null을 반환한다")
    void getCountryWithZeroOrNegativeLatitude() throws Exception {
        // given
        double latitude = 0.0;
        double longitude = 126.9780;

        // when
        CompletableFuture<LocationResult> result = locationFetcher.getCountry(latitude, longitude);
        LocationResult locationResult = result.get();

        // then
        assertThat(locationResult).isNull();
        verifyNoInteractions(geocodeGateway);
    }

    @Test
    @DisplayName("0 이하의 경도로 조회 시 null을 반환한다")
    void getCountryWithZeroOrNegativeLongitude() throws Exception {
        // given
        double latitude = 37.5665;
        double longitude = 0.0;

        // when
        CompletableFuture<LocationResult> result = locationFetcher.getCountry(latitude, longitude);
        LocationResult locationResult = result.get();

        // then
        assertThat(locationResult).isNull();
        verifyNoInteractions(geocodeGateway);
    }

    @Test
    @DisplayName("음수 좌표로 조회 시 null을 반환한다")
    void getCountryWithNegativeCoordinates() throws Exception {
        // given
        double latitude = -37.5665;
        double longitude = -126.9780;

        // when
        CompletableFuture<LocationResult> result = locationFetcher.getCountry(latitude, longitude);
        LocationResult locationResult = result.get();

        // then
        assertThat(locationResult).isNull();
        verifyNoInteractions(geocodeGateway);
    }

    @Test
    @DisplayName("geocode API 예외 발생 시 기본값을 반환한다")
    void getCountryWithGeocodeException() throws Exception {
        // given
        double latitude = 37.5665;
        double longitude = 126.9780;

        given(geocodeGateway.fetchGeocode(latitude, longitude)).willThrow(new RuntimeException("API Error"));

        // when
        CompletableFuture<LocationResult> result = locationFetcher.getCountry(latitude, longitude);
        LocationResult locationResult = result.get();

        // then
        assertThat(locationResult).isNotNull();
        assertThat(locationResult.country()).isEqualTo("기타 국가");
        assertThat(locationResult.city()).isEqualTo("기타 지역");
        verify(geocodeGateway).fetchGeocode(latitude, longitude);
    }

    @Test
    @DisplayName("잘못된 JSON 응답 시 기본값을 반환한다")
    void getCountryWithInvalidJsonResponse() throws Exception {
        // given
        double latitude = 37.5665;
        double longitude = 126.9780;
        String invalidJson = "invalid json response";

        given(geocodeGateway.fetchGeocode(latitude, longitude)).willReturn(invalidJson);

        // when
        CompletableFuture<LocationResult> result = locationFetcher.getCountry(latitude, longitude);
        LocationResult locationResult = result.get();

        // then
        assertThat(locationResult).isNotNull();
        assertThat(locationResult.country()).isEqualTo("기타 국가");
        assertThat(locationResult.city()).isEqualTo("기타 지역");
        verify(geocodeGateway).fetchGeocode(latitude, longitude);
    }

    @Test
    @DisplayName("plus_code가 없는 응답 시 기본값을 반환한다")
    void getCountryWithNoPlusCodeResponse() throws Exception {
        // given
        double latitude = 37.5665;
        double longitude = 126.9780;
        String responseWithoutPlusCode = """
                {
                    "results": []
                }
                """;

        given(geocodeGateway.fetchGeocode(latitude, longitude)).willReturn(responseWithoutPlusCode);

        // when
        CompletableFuture<LocationResult> result = locationFetcher.getCountry(latitude, longitude);
        LocationResult locationResult = result.get();

        // then
        assertThat(locationResult).isNotNull();
        assertThat(locationResult.country()).isEqualTo("기타 국가");
        assertThat(locationResult.city()).isEqualTo("기타 지역");
        verify(geocodeGateway).fetchGeocode(latitude, longitude);
    }

    @Test
    @DisplayName("compound_code가 부족한 응답 시 기본값을 반환한다")
    void getCountryWithInsufficientCompoundCode() throws Exception {
        // given
        double latitude = 37.5665;
        double longitude = 126.9780;
        String responseWithShortCode = createGeocodeResponseWithCity("8Q98+5V");

        given(geocodeGateway.fetchGeocode(latitude, longitude)).willReturn(responseWithShortCode);

        // when
        CompletableFuture<LocationResult> result = locationFetcher.getCountry(latitude, longitude);
        LocationResult locationResult = result.get();

        // then
        assertThat(locationResult).isNotNull();
        assertThat(locationResult.country()).isEqualTo("기타 국가");
        assertThat(locationResult.city()).isEqualTo("기타 지역");
        verify(geocodeGateway).fetchGeocode(latitude, longitude);
    }

    @Test
    @DisplayName("다양한 도시의 위치 정보를 조회할 수 있다")
    void getCountryForDifferentCities() throws Exception {
        // given
        double seoulLat = 37.5665, seoulLon = 126.9780;
        double tokyoLat = 35.6762, tokyoLon = 139.6503;
        double newyorkLat = 40.7128, newyorkLon = 74.0060;

        String seoulResponse = createGeocodeResponseWithCity("8Q98+5V Seoul, South Korea");
        String tokyoResponse = createGeocodeResponseWithCity("8Q7X+X2 Tokyo, Japan");
        String newyorkResponse = createGeocodeResponseWithCity("87G8+5X New York, United States");

        given(geocodeGateway.fetchGeocode(seoulLat, seoulLon)).willReturn(seoulResponse);
        given(geocodeGateway.fetchGeocode(tokyoLat, tokyoLon)).willReturn(tokyoResponse);
        given(geocodeGateway.fetchGeocode(newyorkLat, newyorkLon)).willReturn(newyorkResponse);

        // when
        CompletableFuture<LocationResult> seoulResult = locationFetcher.getCountry(seoulLat, seoulLon);
        CompletableFuture<LocationResult> tokyoResult = locationFetcher.getCountry(tokyoLat, tokyoLon);
        CompletableFuture<LocationResult> newyorkResult = locationFetcher.getCountry(newyorkLat, newyorkLon);

        // then
        LocationResult seoul = seoulResult.get();
        LocationResult tokyo = tokyoResult.get();
        LocationResult newyork = newyorkResult.get();

        assertThat(seoul.country()).isEqualTo("Seoul,");
        assertThat(seoul.city()).isEqualTo("South");

        assertThat(tokyo.country()).isEqualTo("Tokyo,");
        assertThat(tokyo.city()).isEqualTo("Japan");

        assertThat(newyork.country()).isEqualTo("New");
        assertThat(newyork.city()).isEqualTo("York,");
    }

    @Test
    @DisplayName("CompletableFuture를 정상적으로 조인할 수 있다")
    void joinCompletableFutureLocationSuccessfully() {
        // given
        LocationResult expectedResult = new LocationResult("Korea", "Seoul");
        CompletableFuture<LocationResult> future = CompletableFuture.completedFuture(expectedResult);

        // when
        LocationResult result = locationFetcher.joinCompletableFutureLocation(future);

        // then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("CompletableFuture 타임아웃 시 오류 결과를 반환한다")
    void joinCompletableFutureLocationWithTimeout() {
        // given
        CompletableFuture<LocationResult> timeoutFuture = new CompletableFuture<>();
        // 타임아웃을 강제하기 위해 완료하지 않음

        // when
        LocationResult result = locationFetcher.joinCompletableFutureLocation(timeoutFuture);

        // then
        assertThat(result).isNotNull();
        assertThat(result.country()).isEqualTo("Error");
        assertThat(result.city()).isEqualTo("Timeout");
    }

    @Test
    @DisplayName("CompletableFuture 예외 시 오류 결과를 반환한다")
    void joinCompletableFutureLocationWithException() {
        // given
        CompletableFuture<LocationResult> exceptionFuture = CompletableFuture.failedFuture(
                new RuntimeException("Something went wrong")
        );

        // when
        LocationResult result = locationFetcher.joinCompletableFutureLocation(exceptionFuture);

        // then
        assertThat(result).isNotNull();
        assertThat(result.country()).isEqualTo("Error");
        assertThat(result.city()).isEqualTo("Timeout");
    }

    @Test
    @DisplayName("극한값 좌표로 위치 정보를 조회할 수 있다")
    void getCountryWithExtremeCoordinates() throws Exception {
        // given
        double maxLat = 90.0, maxLon = 180.0;
        String response = createGeocodeResponseWithCity("XXXX+XX North Pole, Arctic");

        given(geocodeGateway.fetchGeocode(maxLat, maxLon)).willReturn(response);

        // when
        CompletableFuture<LocationResult> result = locationFetcher.getCountry(maxLat, maxLon);
        LocationResult locationResult = result.get();

        // then
        assertThat(locationResult).isNotNull();
        assertThat(locationResult.country()).isEqualTo("North");
        assertThat(locationResult.city()).isEqualTo("Pole,");
        verify(geocodeGateway).fetchGeocode(maxLat, maxLon);
    }

    @Test
    @DisplayName("소수점이 많은 정밀한 좌표로 조회할 수 있다")
    void getCountryWithPreciseCoordinates() throws Exception {
        // given
        double preciseLat = 37.56656789;
        double preciseLon = 126.97801234;
        String response = createValidGeocodeResponse();

        given(geocodeGateway.fetchGeocode(preciseLat, preciseLon)).willReturn(response);

        // when
        CompletableFuture<LocationResult> result = locationFetcher.getCountry(preciseLat, preciseLon);
        LocationResult locationResult = result.get();

        // then
        assertThat(locationResult).isNotNull();
        verify(geocodeGateway).fetchGeocode(preciseLat, preciseLon);
    }

    @Test
    @DisplayName("빈 compound_code 응답 시 기본값을 반환한다")
    void getCountryWithEmptyCompoundCode() throws Exception {
        // given
        double latitude = 37.5665;
        double longitude = 126.9780;
        String responseWithEmptyCode = createGeocodeResponseWithCity("");

        given(geocodeGateway.fetchGeocode(latitude, longitude)).willReturn(responseWithEmptyCode);

        // when
        CompletableFuture<LocationResult> result = locationFetcher.getCountry(latitude, longitude);
        LocationResult locationResult = result.get();

        // then
        assertThat(locationResult).isNotNull();
        assertThat(locationResult.country()).isEqualTo("기타 국가");
        assertThat(locationResult.city()).isEqualTo("기타 지역");
        verify(geocodeGateway).fetchGeocode(latitude, longitude);
    }

    @Test
    @DisplayName("null compound_code 응답 시 기본값을 반환한다")
    void getCountryWithNullCompoundCode() throws Exception {
        // given
        double latitude = 37.5665;
        double longitude = 126.9780;
        String responseWithNullCode = """
                {
                    "plus_code": {
                        "compound_code": null
                    },
                    "results": []
                }
                """;

        given(geocodeGateway.fetchGeocode(latitude, longitude)).willReturn(responseWithNullCode);

        // when
        CompletableFuture<LocationResult> result = locationFetcher.getCountry(latitude, longitude);
        LocationResult locationResult = result.get();

        // then
        assertThat(locationResult).isNotNull();
        assertThat(locationResult.country()).isEqualTo("기타 국가");
        assertThat(locationResult.city()).isEqualTo("기타 지역");
        verify(geocodeGateway).fetchGeocode(latitude, longitude);
    }

    @Test
    @DisplayName("여러 개의 공백으로 구분된 compound_code를 처리할 수 있다")
    void getCountryWithMultipleSpacesInCompoundCode() throws Exception {
        // given
        double latitude = 37.5665;
        double longitude = 126.9780;
        String responseWithSpaces = createGeocodeResponseWithCity("8Q98+5V  Seoul   South   Korea");

        given(geocodeGateway.fetchGeocode(latitude, longitude)).willReturn(responseWithSpaces);

        // when
        CompletableFuture<LocationResult> result = locationFetcher.getCountry(latitude, longitude);
        LocationResult locationResult = result.get();

        // then
        assertThat(locationResult).isNotNull();
        assertThat(locationResult.country()).isEqualTo("Seoul");
        assertThat(locationResult.city()).isEqualTo("South");
        verify(geocodeGateway).fetchGeocode(latitude, longitude);
    }

    @Test
    @DisplayName("동시에 여러 위치를 조회할 수 있다")
    void getConcurrentLocations() throws Exception {
        // given
        double[] latitudes = {37.5665, 35.6762, 40.7128};
        double[] longitudes = {126.9780, 139.6503, 74.0060};
        String[] responses = {
                createGeocodeResponseWithCity("8Q98+5V Seoul, South Korea"),
                createGeocodeResponseWithCity("8Q7X+X2 Tokyo, Japan"),
                createGeocodeResponseWithCity("87G8+5X New York, United States")
        };

        for (int i = 0; i < latitudes.length; i++) {
            given(geocodeGateway.fetchGeocode(latitudes[i], longitudes[i])).willReturn(responses[i]);
        }

        // when
        CompletableFuture<LocationResult>[] futures = new CompletableFuture[3];
        for (int i = 0; i < latitudes.length; i++) {
            futures[i] = locationFetcher.getCountry(latitudes[i], longitudes[i]);
        }

        // then
        for (var future : futures) {
            var result = future.get();
            assertThat(result).isNotNull();
        }

        for (int i = 0; i < latitudes.length; i++) {
            verify(geocodeGateway).fetchGeocode(latitudes[i], longitudes[i]);
        }
    }
}