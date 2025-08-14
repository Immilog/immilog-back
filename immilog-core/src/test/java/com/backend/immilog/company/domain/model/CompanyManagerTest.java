package com.backend.immilog.company.domain.model;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CompanyManager 도메인 모델 테스트")
class CompanyManagerTest {

    @Test
    @DisplayName("유효한 파라미터로 CompanyManager를 생성할 수 있다")
    void shouldCreateCompanyManagerWithValidParameters() {
        // given
        String country = "KR";
        String region = "서울";
        String userId = "1";

        // when
        CompanyManager companyManager = CompanyManager.of(country, region, userId);

        // then
        assertThat(companyManager.countryId()).isEqualTo(country);
        assertThat(companyManager.region()).isEqualTo(region);
        assertThat(companyManager.userId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("country가 null이면 예외가 발생한다")
    void shouldThrowExceptionWhenCountryIsNull() {
        // given
        String country = null;
        String region = "서울";
        String userId = "1";

        // when & then
        assertThatThrownBy(() -> CompanyManager.of(country, region, userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CountryId cannot be null or empty");
    }

    @Test
    @DisplayName("region이 null이면 예외가 발생한다")
    void shouldThrowExceptionWhenRegionIsNull() {
        // given
        String country = "KR";
        String region = null;
        String userId = "1";

        // when & then
        assertThatThrownBy(() -> CompanyManager.of(country, region, userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Region cannot be null or empty");
    }

    @Test
    @DisplayName("region이 공백이면 예외가 발생한다")
    void shouldThrowExceptionWhenRegionIsEmpty() {
        // given
        String country = "KR";
        String region = "   ";
        String userId = "1";

        // when & then
        assertThatThrownBy(() -> CompanyManager.of(country, region, userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Region cannot be null or empty");
    }

    @Test
    @DisplayName("userId가 null이면 예외가 발생한다")
    void shouldThrowExceptionWhenUserIdIsNull() {
        // given
        String country = "KR";
        String region = "서울";
        String userId = null;

        // when & then
        assertThatThrownBy(() -> CompanyManager.of(country, region, userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("UserId must be not null or blank");
    }

    @Test
    @DisplayName("userId가 빈값이면 예외가 발생한다")
    void shouldThrowExceptionWhenUserIdIsNegative() {
        // given
        String country = "KR";
        String region = "서울";
        String userId = "";

        // when & then
        assertThatThrownBy(() -> CompanyManager.of(country, region, userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("UserId must be not null or blank");
    }

    @Test
    @DisplayName("createEmpty로 빈 CompanyManager를 생성할 수 있다")
    void shouldCreateEmptyCompanyManager() {
        // when
        CompanyManager companyManager = CompanyManager.createEmpty();

        // then
        assertThat(companyManager.countryId()).isNull();
        assertThat(companyManager.region()).isNull();
        assertThat(companyManager.userId()).isNull();
    }

    @Test
    @DisplayName("withCountry로 새로운 country를 가진 CompanyManager를 생성할 수 있다")
    void shouldCreateCompanyManagerWithNewcountryId() {
        // given
        CompanyManager original = CompanyManager.of("KR", "서울", "1");
        String newCountry = "JP";

        // when
        CompanyManager updated = original.withCountry(newCountry);

        // then
        assertThat(updated.countryId()).isEqualTo(newCountry);
        assertThat(updated.region()).isEqualTo(original.region());
        assertThat(updated.userId()).isEqualTo(original.userId());
        assertThat(updated).isNotSameAs(original);
    }

    @Test
    @DisplayName("withRegion으로 새로운 region을 가진 CompanyManager를 생성할 수 있다")
    void shouldCreateCompanyManagerWithNewRegion() {
        // given
        CompanyManager original = CompanyManager.of("KR", "서울", "1");
        String newRegion = "부산";

        // when
        CompanyManager updated = original.withRegion(newRegion);

        // then
        assertThat(updated.countryId()).isEqualTo(original.countryId());
        assertThat(updated.region()).isEqualTo(newRegion);
        assertThat(updated.userId()).isEqualTo(original.userId());
        assertThat(updated).isNotSameAs(original);
    }

    @Test
    @DisplayName("withUserId로 새로운 userId를 가진 CompanyManager를 생성할 수 있다")
    void shouldCreateCompanyManagerWithNewUserId() {
        // given
        CompanyManager original = CompanyManager.of("KR", "서울", "1");
        String newUserId = "2";

        // when
        CompanyManager updated = original.withUserId(newUserId);

        // then
        assertThat(updated.countryId()).isEqualTo(original.countryId());
        assertThat(updated.region()).isEqualTo(original.region());
        assertThat(updated.userId()).isEqualTo(newUserId);
        assertThat(updated).isNotSameAs(original);
    }

    @Test
    @DisplayName("같은 값을 가진 CompanyManager는 동등하다")
    void shouldBeEqualWhenSameValues() {
        // given
        String country = "KR";
        String region = "서울";
        String userId = "1";

        CompanyManager manager1 = CompanyManager.of(country, region, userId);
        CompanyManager manager2 = CompanyManager.of(country, region, userId);

        // when & then
        assertThat(manager1).isEqualTo(manager2);
        assertThat(manager1.hashCode()).isEqualTo(manager2.hashCode());
    }
}