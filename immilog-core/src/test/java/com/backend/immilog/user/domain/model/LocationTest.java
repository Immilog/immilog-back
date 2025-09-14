package com.backend.immilog.user.domain.model;

import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocationTest {

    @Nested
    @DisplayName("Location 생성 테스트")
    class LocationCreationTest {

        @Test
        @DisplayName("유효한 값들로 Location을 생성할 수 있다")
        void createLocationWithValidValues() {
            String countryId = "KR";
            String region = "Seoul";

            Location location = new Location(countryId, region);

            assertThat(location.countryId()).isEqualTo(countryId);
            assertThat(location.region()).isEqualTo(region);
        }

        @Test
        @DisplayName("of 팩토리 메서드로 Location을 생성할 수 있다")
        void createLocationWithFactoryMethod() {
            String countryId = "JP";
            String region = "Tokyo";

            Location location = Location.of(countryId, region);

            assertThat(location.countryId()).isEqualTo(countryId);
            assertThat(location.region()).isEqualTo(region);
        }
    }

    @Nested
    @DisplayName("국가 ID 검증 테스트")
    class CountryIdValidationTest {

        @Test
        @DisplayName("null 국가 ID로 생성 시 예외가 발생한다")
        void createLocationWithNullCountryIdThrowsException() {
            assertThatThrownBy(() -> new Location(null, "Seoul"))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_REGION.getMessage());
        }

        @Test
        @DisplayName("빈 국가 ID로 생성 시 예외가 발생한다")
        void createLocationWithEmptyCountryIdThrowsException() {
            assertThatThrownBy(() -> new Location("", "Seoul"))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_REGION.getMessage());
        }

        @Test
        @DisplayName("공백 국가 ID로 생성 시 예외가 발생한다")
        void createLocationWithBlankCountryIdThrowsException() {
            assertThatThrownBy(() -> new Location("   ", "Seoul"))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_REGION.getMessage());
        }

        @Test
        @DisplayName("유효한 국가 코드들로 Location을 생성할 수 있다")
        void createLocationWithValidCountryCodes() {
            String[] validCountryCodes = {"KR", "JP", "US", "CN", "DE", "FR", "GB", "CA", "AU"};

            for (String countryCode : validCountryCodes) {
                Location location = Location.of(countryCode, "TestRegion");
                assertThat(location.countryId()).isEqualTo(countryCode);
            }
        }

        @Test
        @DisplayName("1자인 국가 ID로 Location을 생성할 수 있다")
        void createLocationWithSingleCharacterCountryId() {
            Location location = Location.of("K", "Seoul");

            assertThat(location.countryId()).isEqualTo("K");
            assertThat(location.countryId()).hasSize(1);
        }

        @Test
        @DisplayName("긴 국가 식별자로도 Location을 생성할 수 있다")
        void createLocationWithLongCountryIdentifier() {
            String longCountryId = "VERY_LONG_COUNTRY_IDENTIFIER_123";

            Location location = Location.of(longCountryId, "Seoul");

            assertThat(location.countryId()).isEqualTo(longCountryId);
        }

        @Test
        @DisplayName("특수문자가 포함된 국가 ID로도 Location을 생성할 수 있다")
        void createLocationWithSpecialCharacterCountryId() {
            String specialCountryId = "KR-123_@";

            Location location = Location.of(specialCountryId, "Seoul");

            assertThat(location.countryId()).isEqualTo(specialCountryId);
        }
    }

    @Nested
    @DisplayName("지역 검증 테스트")
    class RegionValidationTest {

        @Test
        @DisplayName("null 지역으로 생성 시 예외가 발생한다")
        void createLocationWithNullRegionThrowsException() {
            assertThatThrownBy(() -> new Location("KR", null))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_REGION.getMessage());
        }

        @Test
        @DisplayName("빈 지역으로 생성 시 예외가 발생한다")
        void createLocationWithEmptyRegionThrowsException() {
            assertThatThrownBy(() -> new Location("KR", ""))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_REGION.getMessage());
        }

        @Test
        @DisplayName("공백 지역으로 생성 시 예외가 발생한다")
        void createLocationWithBlankRegionThrowsException() {
            assertThatThrownBy(() -> new Location("KR", "   "))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_REGION.getMessage());
        }

        @Test
        @DisplayName("100자를 초과하는 지역으로 생성 시 예외가 발생한다")
        void createLocationWithTooLongRegionThrowsException() {
            String longRegion = "a".repeat(101);

            assertThatThrownBy(() -> new Location("KR", longRegion))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_REGION.getMessage());
        }

        @Test
        @DisplayName("정확히 100자인 지역으로 Location을 생성할 수 있다")
        void createLocationWithHundredCharacterRegion() {
            String hundredCharRegion = "a".repeat(100);

            Location location = Location.of("KR", hundredCharRegion);

            assertThat(location.region()).isEqualTo(hundredCharRegion);
            assertThat(location.region()).hasSize(100);
        }

        @Test
        @DisplayName("1자인 지역으로 Location을 생성할 수 있다")
        void createLocationWithSingleCharacterRegion() {
            Location location = Location.of("KR", "S");

            assertThat(location.region()).isEqualTo("S");
            assertThat(location.region()).hasSize(1);
        }

        @Test
        @DisplayName("다양한 형식의 지역명으로 Location을 생성할 수 있다")
        void createLocationWithVariousRegionFormats() {
            String[] validRegions = {
                    "Seoul",
                    "서울특별시",
                    "New York City",
                    "São Paulo",
                    "北京市",
                    "Москва",
                    "القاهرة",
                    "Region-123",
                    "Test_Region_456"
            };

            for (String region : validRegions) {
                Location location = Location.of("TEST", region);
                assertThat(location.region()).isEqualTo(region);
            }
        }

        @Test
        @DisplayName("특수문자가 포함된 지역명으로 Location을 생성할 수 있다")
        void createLocationWithSpecialCharacterRegion() {
            String specialRegion = "Seoul-City_123!@#$%^&*()";

            Location location = Location.of("KR", specialRegion);

            assertThat(location.region()).isEqualTo(specialRegion);
        }

        @Test
        @DisplayName("유니코드 문자가 포함된 지역명으로 Location을 생성할 수 있다")
        void createLocationWithUnicodeRegion() {
            String unicodeRegion = "서울特別市🏙️";

            Location location = Location.of("KR", unicodeRegion);

            assertThat(location.region()).isEqualTo(unicodeRegion);
        }
    }

    @Nested
    @DisplayName("Location 동등성 테스트")
    class LocationEqualityTest {

        @Test
        @DisplayName("같은 국가 ID와 지역을 가진 Location은 동등하다")
        void locationsWithSameValuesAreEqual() {
            String countryId = "KR";
            String region = "Seoul";

            Location location1 = Location.of(countryId, region);
            Location location2 = Location.of(countryId, region);

            assertThat(location1).isEqualTo(location2);
            assertThat(location1.hashCode()).isEqualTo(location2.hashCode());
        }

        @Test
        @DisplayName("다른 국가 ID를 가진 Location은 동등하지 않다")
        void locationsWithDifferentCountryIdsAreNotEqual() {
            Location location1 = Location.of("KR", "Seoul");
            Location location2 = Location.of("JP", "Seoul");

            assertThat(location1).isNotEqualTo(location2);
        }

        @Test
        @DisplayName("다른 지역을 가진 Location은 동등하지 않다")
        void locationsWithDifferentRegionsAreNotEqual() {
            Location location1 = Location.of("KR", "Seoul");
            Location location2 = Location.of("KR", "Busan");

            assertThat(location1).isNotEqualTo(location2);
        }

        @Test
        @DisplayName("대소문자가 다른 Location은 동등하지 않다")
        void locationsWithDifferentCasesAreNotEqual() {
            Location location1 = Location.of("kr", "seoul");
            Location location2 = Location.of("KR", "Seoul");

            assertThat(location1).isNotEqualTo(location2);
        }
    }

    @Nested
    @DisplayName("Location 특수 케이스 테스트")
    class LocationSpecialCasesTest {

        @Test
        @DisplayName("모든 필드가 최소값인 Location을 생성할 수 있다")
        void createLocationWithMinimalValues() {
            Location location = Location.of("K", "S");

            assertThat(location.countryId()).isEqualTo("K");
            assertThat(location.region()).isEqualTo("S");
        }

        @Test
        @DisplayName("모든 필드가 최대값인 Location을 생성할 수 있다")
        void createLocationWithMaximalValues() {
            String longCountryId = "VERY_LONG_COUNTRY_ID";
            String maxRegion = "a".repeat(100);

            Location location = Location.of(longCountryId, maxRegion);

            assertThat(location.countryId()).isEqualTo(longCountryId);
            assertThat(location.region()).isEqualTo(maxRegion);
            assertThat(location.region()).hasSize(100);
        }

        @Test
        @DisplayName("실제 지역 데이터로 Location을 생성할 수 있다")
        void createLocationWithRealWorldData() {
            String[][] realWorldData = {
                    {"KR", "서울특별시"},
                    {"JP", "東京都"},
                    {"US", "New York"},
                    {"CN", "北京市"},
                    {"DE", "Berlin"},
                    {"FR", "Paris"},
                    {"GB", "London"},
                    {"RU", "Москва"},
                    {"EG", "القاهرة"},
                    {"BR", "São Paulo"}
            };

            for (String[] data : realWorldData) {
                Location location = Location.of(data[0], data[1]);
                assertThat(location.countryId()).isEqualTo(data[0]);
                assertThat(location.region()).isEqualTo(data[1]);
            }
        }
    }

    @Nested
    @DisplayName("Location toString 테스트")
    class LocationToStringTest {

        @Test
        @DisplayName("toString 메서드가 올바르게 동작한다")
        void toStringWorksCorrectly() {
            Location location = Location.of("KR", "Seoul");

            String result = location.toString();

            assertThat(result).contains("KR");
            assertThat(result).contains("Seoul");
            assertThat(result).contains("Location");
        }

        @Test
        @DisplayName("특수문자가 포함된 Location의 toString이 올바르게 동작한다")
        void toStringWorksCorrectlyWithSpecialCharacters() {
            Location location = Location.of("KR-123", "Seoul_City!@#");

            String result = location.toString();

            assertThat(result).contains("KR-123");
            assertThat(result).contains("Seoul_City!@#");
        }
    }
}