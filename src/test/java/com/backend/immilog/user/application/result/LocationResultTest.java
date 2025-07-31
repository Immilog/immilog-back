package com.backend.immilog.user.application.result;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LocationResult 테스트")
class LocationResultTest {

    @Test
    @DisplayName("정상적인 값들로 LocationResult를 생성할 수 있다")
    void createLocationResult() {
        // given
        String country = "대한민국";
        String city = "서울특별시";

        // when
        LocationResult result = new LocationResult(country, city);

        // then
        assertThat(result.country()).isEqualTo(country);
        assertThat(result.city()).isEqualTo(city);
    }

    @Test
    @DisplayName("null 값들로도 LocationResult를 생성할 수 있다")
    void createLocationResultWithNullValues() {
        // given & when
        LocationResult result = new LocationResult(null, null);

        // then
        assertThat(result.country()).isNull();
        assertThat(result.city()).isNull();
    }

    @Test
    @DisplayName("빈 문자열로도 LocationResult를 생성할 수 있다")
    void createLocationResultWithEmptyStrings() {
        // given & when
        LocationResult result = new LocationResult("", "");

        // then
        assertThat(result.country()).isEmpty();
        assertThat(result.city()).isEmpty();
    }

    @Test
    @DisplayName("다양한 국가와 도시로 LocationResult를 생성할 수 있다")
    void createLocationResultWithVariousLocations() {
        // given & when
        LocationResult koreaResult = new LocationResult("대한민국", "서울특별시");
        LocationResult japanResult = new LocationResult("일본", "도쿄");
        LocationResult usaResult = new LocationResult("미국", "뉴욕");
        LocationResult chinaResult = new LocationResult("중국", "베이징");
        LocationResult malaysiaResult = new LocationResult("말레이시아", "쿠알라룸푸르");

        // then
        assertThat(koreaResult.country()).isEqualTo("대한민국");
        assertThat(koreaResult.city()).isEqualTo("서울특별시");

        assertThat(japanResult.country()).isEqualTo("일본");
        assertThat(japanResult.city()).isEqualTo("도쿄");

        assertThat(usaResult.country()).isEqualTo("미국");
        assertThat(usaResult.city()).isEqualTo("뉴욕");

        assertThat(chinaResult.country()).isEqualTo("중국");
        assertThat(chinaResult.city()).isEqualTo("베이징");

        assertThat(malaysiaResult.country()).isEqualTo("말레이시아");
        assertThat(malaysiaResult.city()).isEqualTo("쿠알라룸푸르");
    }

    @Test
    @DisplayName("영어로 된 국가와 도시로 LocationResult를 생성할 수 있다")
    void createLocationResultWithEnglishNames() {
        // given & when
        LocationResult result = new LocationResult("South Korea", "Seoul");

        // then
        assertThat(result.country()).isEqualTo("South Korea");
        assertThat(result.city()).isEqualTo("Seoul");
    }

    @Test
    @DisplayName("에러 상황을 나타내는 LocationResult를 생성할 수 있다")
    void createLocationResultForErrorSituation() {
        // given & when
        LocationResult errorResult = new LocationResult("Error", "Timeout");
        LocationResult unknownResult = new LocationResult("Unknown", "Unknown");

        // then
        assertThat(errorResult.country()).isEqualTo("Error");
        assertThat(errorResult.city()).isEqualTo("Timeout");

        assertThat(unknownResult.country()).isEqualTo("Unknown");
        assertThat(unknownResult.city()).isEqualTo("Unknown");
    }

    @Test
    @DisplayName("LocationResult record의 동등성이 정상 작동한다")
    void locationResultEquality() {
        // given
        LocationResult result1 = new LocationResult("대한민국", "서울특별시");
        LocationResult result2 = new LocationResult("대한민국", "서울특별시");
        LocationResult result3 = new LocationResult("일본", "서울특별시");
        LocationResult result4 = new LocationResult("대한민국", "부산광역시");

        // when & then
        assertThat(result1).isEqualTo(result2);
        assertThat(result1).isNotEqualTo(result3);
        assertThat(result1).isNotEqualTo(result4);
        assertThat(result1.hashCode()).isEqualTo(result2.hashCode());
    }

    @Test
    @DisplayName("LocationResult record의 toString이 정상 작동한다")
    void locationResultToString() {
        // given
        LocationResult result = new LocationResult("대한민국", "서울특별시");

        // when
        String toString = result.toString();

        // then
        assertThat(toString).contains("LocationResult");
        assertThat(toString).contains("대한민국");
        assertThat(toString).contains("서울특별시");
    }

    @Test
    @DisplayName("공백이 포함된 지역명으로 LocationResult를 생성할 수 있다")
    void createLocationResultWithSpaces() {
        // given & when
        LocationResult result = new LocationResult("United States", "New York City");

        // then
        assertThat(result.country()).isEqualTo("United States");
        assertThat(result.city()).isEqualTo("New York City");
    }

    @Test
    @DisplayName("특수문자가 포함된 지역명으로 LocationResult를 생성할 수 있다")
    void createLocationResultWithSpecialCharacters() {
        // given & when
        LocationResult result = new LocationResult("Côte d'Ivoire", "São Paulo");

        // then
        assertThat(result.country()).isEqualTo("Côte d'Ivoire");
        assertThat(result.city()).isEqualTo("São Paulo");
    }

    @Test
    @DisplayName("긴 지역명으로 LocationResult를 생성할 수 있다")
    void createLocationResultWithLongNames() {
        // given
        String longCountryName = "The United Kingdom of Great Britain and Northern Ireland";
        String longCityName = "Llanfairpwllgwyngyllgogerychwyrndrobwllllantysiliogogogoch";

        // when
        LocationResult result = new LocationResult(longCountryName, longCityName);

        // then
        assertThat(result.country()).isEqualTo(longCountryName);
        assertThat(result.city()).isEqualTo(longCityName);
    }
}