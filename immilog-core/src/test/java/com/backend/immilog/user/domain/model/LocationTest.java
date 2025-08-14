package com.backend.immilog.user.domain.model;

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
    void createLocationWithValidStringAndRegion() {
        // given
        String validString = "KR";
        String validRegion = "서울특별시";

        // when
        Location location = Location.of(validString, validRegion);

        // then
        assertThat(location.countryId()).isEqualTo(validString);
        assertThat(location.region()).isEqualTo(validRegion);
    }

    @Test
    @DisplayName("null 국가로 Location 생성 시 예외가 발생한다")
    void createLocationWithNullString() {
        // given
        String nullString = null;
        String validRegion = "서울특별시";

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> Location.of(nullString, validRegion));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_REGION);
    }

    @Test
    @DisplayName("null 지역으로 Location 생성 시 예외가 발생한다")
    void createLocationWithNullRegion() {
        // given
        String validString = "KR";
        String nullRegion = null;

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> Location.of(validString, nullRegion));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_REGION);
    }

    @Test
    @DisplayName("빈 지역으로 Location 생성 시 예외가 발생한다")
    void createLocationWithEmptyRegion() {
        // given
        String validString = "KR";
        String emptyRegion = "";

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> Location.of(validString, emptyRegion));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_REGION);
    }

    @Test
    @DisplayName("공백 지역으로 Location 생성 시 예외가 발생한다")
    void createLocationWithBlankRegion() {
        // given
        String validString = "KR";
        String blankRegion = "   ";

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> Location.of(validString, blankRegion));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_REGION);
    }

    @Test
    @DisplayName("100자를 초과하는 지역으로 Location 생성 시 예외가 발생한다")
    void createLocationWithTooLongRegion() {
        // given
        String validString = "KR";
        String tooLongRegion = "a".repeat(101); // 101자

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> Location.of(validString, tooLongRegion));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_REGION);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 50, 100})
    @DisplayName("유효한 길이의 지역으로 Location을 생성할 수 있다")
    void createLocationWithValidRegionLength(int regionLength) {
        // given
        String validString = "KR";
        String validRegion = "a".repeat(regionLength);

        // when
        Location location = Location.of(validString, validRegion);

        // then
        assertThat(location.countryId()).isEqualTo(validString);
        assertThat(location.region()).isEqualTo(validRegion);
        assertThat(location.region().length()).isEqualTo(regionLength);
    }

    @Test
    @DisplayName("모든 String enum 값으로 Location을 생성할 수 있다")
    void createLocationWithAllStringValues() {
        // given
        String validRegion = "테스트지역";

        // when & then
        String[] countries = {"KR", "JP", "MY", "BN", "US", "CA", "AU", "NZ", "GB", "SG"};
        for (String country : countries) {
            Location location = Location.of(country, validRegion);
            assertThat(location.countryId()).isEqualTo(country);
            assertThat(location.region()).isEqualTo(validRegion);
        }
    }

    @Test
    @DisplayName("다양한 실제 지역명으로 Location을 생성할 수 있다")
    void createLocationWithRealRegionNames() {
        // given
        String korea = "KR";
        String[] realRegions = {
                "서울특별시", "부산광역시", "대구광역시", "인천광역시", "광주광역시",
                "대전광역시", "울산광역시", "세종특별자치시", "경기도", "강원도",
                "충청북도", "충청남도", "전라북도", "전라남도", "경상북도", "경상남도", "제주특별자치도"
        };

        // when & then
        for (String region : realRegions) {
            Location location = Location.of(korea, region);
            assertThat(location.countryId()).isEqualTo(korea);
            assertThat(location.region()).isEqualTo(region);
        }
    }

    @Test
    @DisplayName("Location record의 동등성이 정상 동작한다")
    void locationEquality() {
        // given
        String country = "KR";
        String region = "서울특별시";

        Location location1 = Location.of(country, region);
        Location location2 = Location.of(country, region);
        Location location3 = Location.of("JP", region);
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
        String country = "KR";
        String region = "서울특별시";
        Location location = Location.of(country, region);

        // when
        String toString = location.toString();

        // then
        assertThat(toString).contains("Location");
        assertThat(toString).contains(country);
        assertThat(toString).contains(region);
    }

    @Test
    @DisplayName("다국가의 지역으로 Location을 생성할 수 있다")
    void createLocationWithInternationalRegions() {
        // given & when & then
        Location malaysia = Location.of("MY", "쿠알라룸푸르");
        Location singapore = Location.of("SG", "센트럴");
        Location japan = Location.of("JP", "도쿄");
        Location china = Location.of("CN", "베이징");

        assertThat(malaysia.region()).isEqualTo("쿠알라룸푸르");
        assertThat(singapore.region()).isEqualTo("센트럴");
        assertThat(japan.region()).isEqualTo("도쿄");
        assertThat(china.region()).isEqualTo("베이징");
    }
}