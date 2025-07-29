package com.backend.immilog.company.domain.model;

import com.backend.immilog.shared.enums.Country;
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
        Country country = Country.SOUTH_KOREA;
        String region = "서울";
        Long userSeq = 1L;

        // when
        CompanyManager companyManager = CompanyManager.of(country, region, userSeq);

        // then
        assertThat(companyManager.country()).isEqualTo(country);
        assertThat(companyManager.region()).isEqualTo(region);
        assertThat(companyManager.userSeq()).isEqualTo(userSeq);
    }

    @Test
    @DisplayName("country가 null이면 예외가 발생한다")
    void shouldThrowExceptionWhenCountryIsNull() {
        // given
        Country country = null;
        String region = "서울";
        Long userSeq = 1L;

        // when & then
        assertThatThrownBy(() -> CompanyManager.of(country, region, userSeq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Country cannot be null");
    }

    @Test
    @DisplayName("region이 null이면 예외가 발생한다")
    void shouldThrowExceptionWhenRegionIsNull() {
        // given
        Country country = Country.SOUTH_KOREA;
        String region = null;
        Long userSeq = 1L;

        // when & then
        assertThatThrownBy(() -> CompanyManager.of(country, region, userSeq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Region cannot be null or empty");
    }

    @Test
    @DisplayName("region이 공백이면 예외가 발생한다")
    void shouldThrowExceptionWhenRegionIsEmpty() {
        // given
        Country country = Country.SOUTH_KOREA;
        String region = "   ";
        Long userSeq = 1L;

        // when & then
        assertThatThrownBy(() -> CompanyManager.of(country, region, userSeq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Region cannot be null or empty");
    }

    @Test
    @DisplayName("userSeq가 null이면 예외가 발생한다")
    void shouldThrowExceptionWhenUserSeqIsNull() {
        // given
        Country country = Country.SOUTH_KOREA;
        String region = "서울";
        Long userSeq = null;

        // when & then
        assertThatThrownBy(() -> CompanyManager.of(country, region, userSeq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("UserSeq must be positive");
    }

    @Test
    @DisplayName("userSeq가 0이면 예외가 발생한다")
    void shouldThrowExceptionWhenUserSeqIsZero() {
        // given
        Country country = Country.SOUTH_KOREA;
        String region = "서울";
        Long userSeq = 0L;

        // when & then
        assertThatThrownBy(() -> CompanyManager.of(country, region, userSeq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("UserSeq must be positive");
    }

    @Test
    @DisplayName("userSeq가 음수이면 예외가 발생한다")
    void shouldThrowExceptionWhenUserSeqIsNegative() {
        // given
        Country country = Country.SOUTH_KOREA;
        String region = "서울";
        Long userSeq = -1L;

        // when & then
        assertThatThrownBy(() -> CompanyManager.of(country, region, userSeq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("UserSeq must be positive");
    }

    @Test
    @DisplayName("createEmpty로 빈 CompanyManager를 생성할 수 있다")
    void shouldCreateEmptyCompanyManager() {
        // when
        CompanyManager companyManager = CompanyManager.createEmpty();

        // then
        assertThat(companyManager.country()).isNull();
        assertThat(companyManager.region()).isNull();
        assertThat(companyManager.userSeq()).isNull();
    }

    @Test
    @DisplayName("withCountry로 새로운 country를 가진 CompanyManager를 생성할 수 있다")
    void shouldCreateCompanyManagerWithNewCountry() {
        // given
        CompanyManager original = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        Country newCountry = Country.JAPAN;

        // when
        CompanyManager updated = original.withCountry(newCountry);

        // then
        assertThat(updated.country()).isEqualTo(newCountry);
        assertThat(updated.region()).isEqualTo(original.region());
        assertThat(updated.userSeq()).isEqualTo(original.userSeq());
        assertThat(updated).isNotSameAs(original);
    }

    @Test
    @DisplayName("withRegion으로 새로운 region을 가진 CompanyManager를 생성할 수 있다")
    void shouldCreateCompanyManagerWithNewRegion() {
        // given
        CompanyManager original = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        String newRegion = "부산";

        // when
        CompanyManager updated = original.withRegion(newRegion);

        // then
        assertThat(updated.country()).isEqualTo(original.country());
        assertThat(updated.region()).isEqualTo(newRegion);
        assertThat(updated.userSeq()).isEqualTo(original.userSeq());
        assertThat(updated).isNotSameAs(original);
    }

    @Test
    @DisplayName("withUserSeq로 새로운 userSeq를 가진 CompanyManager를 생성할 수 있다")
    void shouldCreateCompanyManagerWithNewUserSeq() {
        // given
        CompanyManager original = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        Long newUserSeq = 2L;

        // when
        CompanyManager updated = original.withUserSeq(newUserSeq);

        // then
        assertThat(updated.country()).isEqualTo(original.country());
        assertThat(updated.region()).isEqualTo(original.region());
        assertThat(updated.userSeq()).isEqualTo(newUserSeq);
        assertThat(updated).isNotSameAs(original);
    }

    @Test
    @DisplayName("같은 값을 가진 CompanyManager는 동등하다")
    void shouldBeEqualWhenSameValues() {
        // given
        Country country = Country.SOUTH_KOREA;
        String region = "서울";
        Long userSeq = 1L;
        
        CompanyManager manager1 = CompanyManager.of(country, region, userSeq);
        CompanyManager manager2 = CompanyManager.of(country, region, userSeq);

        // when & then
        assertThat(manager1).isEqualTo(manager2);
        assertThat(manager1.hashCode()).isEqualTo(manager2.hashCode());
    }
}