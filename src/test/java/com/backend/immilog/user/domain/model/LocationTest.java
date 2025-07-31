package com.backend.immilog.user.domain.model;

import com.backend.immilog.shared.enums.Country;
import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Location 도메인 테스트")
class LocationTest {

    @Test
    @DisplayName("정상적인 국가와 지역으로 Location을 생성할 수 있다")
    void createLocationWithValidCountryAndRegion() {
        // given
        Country validCountry = Country.SOUTH_KOREA;
        String validRegion = "서울특별시";

        // when
        Location location = Location.of(validCountry, validRegion);

        // then
        assertThat(location.country()).isEqualTo(validCountry);
        assertThat(location.region()).isEqualTo(validRegion);
    }

    @Test
    @DisplayName("null 국가로 Location 생성 시 예외가 발생한다")
    void createLocationWithNullCountry() {
        // given
        Country nullCountry = null;
        String validRegion = "서울특별시";

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> Location.of(nullCountry, validRegion));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_REGION);
    }

    @Test
    @DisplayName("null 지역으로 Location 생성 시 예외가 발생한다")
    void createLocationWithNullRegion() {
        // given
        Country validCountry = Country.SOUTH_KOREA;
        String nullRegion = null;

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> Location.of(validCountry, nullRegion));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_REGION);
    }

    @Test
    @DisplayName("빈 지역으로 Location 생성 시 예외가 발생한다")
    void createLocationWithEmptyRegion() {
        // given
        Country validCountry = Country.SOUTH_KOREA;
        String emptyRegion = "";

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> Location.of(validCountry, emptyRegion));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_REGION);
    }

    @Test
    @DisplayName("공백 지역으로 Location 생성 시 예외가 발생한다")
    void createLocationWithBlankRegion() {
        // given
        Country validCountry = Country.SOUTH_KOREA;
        String blankRegion = "   ";

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> Location.of(validCountry, blankRegion));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_REGION);
    }

    @Test
    @DisplayName("100자를 초과하는 지역으로 Location 생성 시 예외가 발생한다")
    void createLocationWithTooLongRegion() {
        // given
        Country validCountry = Country.SOUTH_KOREA;
        String tooLongRegion = "a".repeat(101); // 101자

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> Location.of(validCountry, tooLongRegion));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_REGION);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 50, 100})
    @DisplayName("유효한 길이의 지역으로 Location을 생성할 수 있다")
    void createLocationWithValidRegionLength(int regionLength) {
        // given
        Country validCountry = Country.SOUTH_KOREA;
        String validRegion = "a".repeat(regionLength);

        // when
        Location location = Location.of(validCountry, validRegion);

        // then
        assertThat(location.country()).isEqualTo(validCountry);
        assertThat(location.region()).isEqualTo(validRegion);
        assertThat(location.region().length()).isEqualTo(regionLength);
    }

    @Test
    @DisplayName("모든 Country enum 값으로 Location을 생성할 수 있다")
    void createLocationWithAllCountryValues() {
        // given
        String validRegion = "테스트지역";

        // when & then
        for (Country country : Country.values()) {
            Location location = Location.of(country, validRegion);
            assertThat(location.country()).isEqualTo(country);
            assertThat(location.region()).isEqualTo(validRegion);
        }
    }

    @Test
    @DisplayName("다양한 실제 지역명으로 Location을 생성할 수 있다")
    void createLocationWithRealRegionNames() {
        // given
        Country korea = Country.SOUTH_KOREA;
        String[] realRegions = {
                "서울특별시", "부산광역시", "대구광역시", "인천광역시", "광주광역시",
                "대전광역시", "울산광역시", "세종특별자치시", "경기도", "강원도",
                "충청북도", "충청남도", "전라북도", "전라남도", "경상북도", "경상남도", "제주특별자치도"
        };

        // when & then
        for (String region : realRegions) {
            Location location = Location.of(korea, region);
            assertThat(location.country()).isEqualTo(korea);
            assertThat(location.region()).isEqualTo(region);
        }
    }

    @Test
    @DisplayName("Location record의 동등성이 정상 동작한다")
    void locationEquality() {
        // given
        Country country = Country.SOUTH_KOREA;
        String region = "서울특별시";

        Location location1 = Location.of(country, region);
        Location location2 = Location.of(country, region);
        Location location3 = Location.of(Country.JAPAN, region);
        Location location4 = Location.of(country, "부산광역시");

        // when & then
        assertThat(location1).isEqualTo(location2);
        assertThat(location1).isNotEqualTo(location3);
        assertThat(location1).isNotEqualTo(location4);
        assertThat(location1.hashCode()).isEqualTo(location2.hashCode());
    }

    @Test
    @DisplayName("Location record의 toString이 정상 동작한다")
    void locationToString() {
        // given
        Country country = Country.SOUTH_KOREA;
        String region = "서울특별시";
        Location location = Location.of(country, region);

        // when
        String toString = location.toString();

        // then
        assertThat(toString).contains("Location");
        assertThat(toString).contains(country.toString());
        assertThat(toString).contains(region);
    }

    @Test
    @DisplayName("다국가의 지역으로 Location을 생성할 수 있다")
    void createLocationWithInternationalRegions() {
        // given & when & then
        Location malaysia = Location.of(Country.MALAYSIA, "쿠알라룸푸르");
        Location singapore = Location.of(Country.SINGAPORE, "센트럴");
        Location japan = Location.of(Country.JAPAN, "도쿄");
        Location china = Location.of(Country.CHINA, "베이징");

        assertThat(malaysia.region()).isEqualTo("쿠알라룸푸르");
        assertThat(singapore.region()).isEqualTo("센트럴");
        assertThat(japan.region()).isEqualTo("도쿄");
        assertThat(china.region()).isEqualTo("베이징");
    }
}