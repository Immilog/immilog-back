package com.backend.immilog.country.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CountryTest {

    @Nested
    @DisplayName("Country 생성 테스트")
    class CountryCreationTest {

        @Test
        @DisplayName("of 팩토리 메서드로 Country를 생성할 수 있다")
        void createCountryWithFactoryMethod() {
            Country country = Country.of("KR", "대한민국", "South Korea", "Asia");

            assertThat(country).isNotNull();
            assertThat(country.id().value()).isEqualTo("KR");
            assertThat(country.nameKo()).isEqualTo("대한민국");
            assertThat(country.nameEn()).isEqualTo("South Korea");
            assertThat(country.continent()).isEqualTo("Asia");
            assertThat(country.status()).isEqualTo(CountryStatus.ACTIVE);
            assertThat(country.createdAt()).isNotNull();
            assertThat(country.updatedAt()).isNotNull();
        }

        @Test
        @DisplayName("빌더로 Country를 생성할 수 있다")
        void createCountryWithBuilder() {
            LocalDateTime now = LocalDateTime.now();

            var country = Country.builder()
                    .id(new CountryId("JP"))
                    .info(new CountryInfo("일본", "Japan", "Asia"))
                    .status(CountryStatus.INACTIVE)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            assertThat(country.id().value()).isEqualTo("JP");
            assertThat(country.nameKo()).isEqualTo("일본");
            assertThat(country.nameEn()).isEqualTo("Japan");
            assertThat(country.continent()).isEqualTo("Asia");
            assertThat(country.status()).isEqualTo(CountryStatus.INACTIVE);
            assertThat(country.createdAt()).isEqualTo(now);
            assertThat(country.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("Country 비즈니스 로직 테스트")
    class CountryBusinessLogicTest {

        @Test
        @DisplayName("ACTIVE 상태의 Country는 활성화 상태이다")
        void activeCountryIsActive() {
            Country country = Country.of("KR", "대한민국", "South Korea", "Asia");

            assertThat(country.isActive()).isTrue();
        }

        @Test
        @DisplayName("INACTIVE 상태의 Country는 비활성화 상태이다")
        void inactiveCountryIsNotActive() {
            var now = LocalDateTime.now();
            var country = Country.builder()
                    .id(new CountryId("JP"))
                    .info(new CountryInfo("일본", "Japan", "Asia"))
                    .status(CountryStatus.INACTIVE)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            assertThat(country.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("Country 동등성 테스트")
    class CountryEqualityTest {

        @Test
        @DisplayName("같은 ID를 가진 Country는 동등하다")
        void countriesWithSameIdAreEqual() {
            Country country1 = Country.of("KR", "대한민국", "South Korea", "Asia");
            Country country2 = Country.of("KR", "한국", "Korea", "Asia");

            assertThat(country1).isEqualTo(country2);
            assertThat(country1.hashCode()).isEqualTo(country2.hashCode());
        }

        @Test
        @DisplayName("다른 ID를 가진 Country는 동등하지 않다")
        void countriesWithDifferentIdAreNotEqual() {
            Country country1 = Country.of("KR", "대한민국", "South Korea", "Asia");
            Country country2 = Country.of("JP", "일본", "Japan", "Asia");

            assertThat(country1).isNotEqualTo(country2);
        }

        @Test
        @DisplayName("null과 비교하면 동등하지 않다")
        void countryIsNotEqualToNull() {
            Country country = Country.of("KR", "대한민국", "South Korea", "Asia");

            assertThat(country).isNotEqualTo(null);
        }

        @Test
        @DisplayName("다른 타입 객체와 비교하면 동등하지 않다")
        void countryIsNotEqualToDifferentType() {
            Country country = Country.of("KR", "대한민국", "South Korea", "Asia");

            assertThat(country).isNotEqualTo("not a country");
        }
    }

    @Nested
    @DisplayName("Country 속성 검증 테스트")
    class CountryPropertyValidationTest {

        @Test
        @DisplayName("모든 필수 속성이 올바르게 설정된다")
        void allRequiredPropertiesAreSet() {
            LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 0, 0);
            LocalDateTime updatedAt = LocalDateTime.of(2023, 1, 2, 0, 0);

            Country country = Country.builder()
                    .id(new CountryId("US"))
                    .info(new CountryInfo("미국", "United States", "North America"))
                    .status(CountryStatus.ACTIVE)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();

            assertThat(country.id().value()).isEqualTo("US");
            assertThat(country.nameKo()).isEqualTo("미국");
            assertThat(country.nameEn()).isEqualTo("United States");
            assertThat(country.continent()).isEqualTo("North America");
            assertThat(country.status()).isEqualTo(CountryStatus.ACTIVE);
            assertThat(country.createdAt()).isEqualTo(createdAt);
            assertThat(country.updatedAt()).isEqualTo(updatedAt);
        }

        @Test
        @DisplayName("toString 메서드가 올바르게 동작한다")
        void toStringMethodWorksCorrectly() {
            Country country = Country.of("KR", "대한민국", "South Korea", "Asia");

            String result = country.toString();

            assertThat(result).contains("KR");
            assertThat(result).contains("대한민국");
            assertThat(result).contains("South Korea");
            assertThat(result).contains("Asia");
            assertThat(result).contains("ACTIVE");
        }
    }

    @Nested
    @DisplayName("Country 팩토리 메서드 검증 테스트")
    class CountryFactoryMethodTest {

        @Test
        @DisplayName("of 메서드는 항상 ACTIVE 상태로 Country를 생성한다")
        void ofMethodAlwaysCreatesActiveCountry() {
            Country country = Country.of("CN", "중국", "China", "Asia");

            assertThat(country.status()).isEqualTo(CountryStatus.ACTIVE);
            assertThat(country.isActive()).isTrue();
        }

        @Test
        @DisplayName("of 메서드는 현재 시간으로 createdAt과 updatedAt을 설정한다")
        void ofMethodSetsCurrentTimeForTimestamps() {
            LocalDateTime before = LocalDateTime.now();
            Country country = Country.of("FR", "프랑스", "France", "Europe");
            LocalDateTime after = LocalDateTime.now();

            assertThat(country.createdAt()).isBetween(before, after);
            assertThat(country.updatedAt()).isBetween(before, after);
        }

        @Test
        @DisplayName("of 메서드로 생성된 Country의 createdAt과 updatedAt은 같다")
        void ofMethodSetsEqualTimestamps() {
            Country country = Country.of("DE", "독일", "Germany", "Europe");

            assertThat(country.createdAt()).isEqualTo(country.updatedAt());
        }
    }

    @Nested
    @DisplayName("Country 엣지 케이스 테스트")
    class CountryEdgeCaseTest {

        @Test
        @DisplayName("특수문자가 포함된 문자열로도 Country를 생성할 수 있다")
        void canCreateCountryWithSpecialCharacters() {
            Country country = Country.of("!@#$", "한글!@#", "English$%^", "Asia&*()");

            assertThat(country.id().value()).isEqualTo("!@#$");
            assertThat(country.nameKo()).isEqualTo("한글!@#");
            assertThat(country.nameEn()).isEqualTo("English$%^");
            assertThat(country.continent()).isEqualTo("Asia&*()");
        }
    }
}