package com.backend.immilog.company.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CompanyId 도메인 모델 테스트")
class CompanyIdTest {

    @Test
    @DisplayName("양수 값으로 CompanyId를 생성할 수 있다")
    void shouldCreateCompanyIdWithPositiveValue() {
        // given
        String validValue = "1";

        // when
        CompanyId companyId = CompanyId.of(validValue);

        // then
        assertThat(companyId.value()).isEqualTo(validValue);
    }

    @Test
    @DisplayName("null 값으로 CompanyId 생성 시 예외가 발생한다")
    void shouldThrowExceptionWhenCreateWithNullValue() {
        // given
        String nullValue = null;

        // when & then
        assertThatThrownBy(() -> CompanyId.of(nullValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CompanyId value must be not null or empty");
    }

    @Test
    @DisplayName("빈값 으로 CompanyId 생성 시 예외가 발생한다")
    void shouldThrowExceptionWhenCreateWithNegativeValue() {
        // given
        String negativeValue = "";

        // when & then
        assertThatThrownBy(() -> CompanyId.of(negativeValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CompanyId value must be not null or empty");
    }

    @Test
    @DisplayName("generate 메서드로 null 값을 가진 CompanyId를 생성할 수 있다")
    void shouldGenerateCompanyIdWithNullValue() {
        // when
        CompanyId companyId = CompanyId.generate();

        // then
        assertThat(companyId.value()).isNull();
    }

    @Test
    @DisplayName("같은 값을 가진 CompanyId는 동등하다")
    void shouldBeEqualWhenSameValue() {
        // given
        String value = "1";
        CompanyId companyId1 = CompanyId.of(value);
        CompanyId companyId2 = CompanyId.of(value);

        // when & then
        assertThat(companyId1).isEqualTo(companyId2);
        assertThat(companyId1.hashCode()).isEqualTo(companyId2.hashCode());
    }

    @Test
    @DisplayName("다른 값을 가진 CompanyId는 동등하지 않다")
    void shouldNotBeEqualWhenDifferentValue() {
        // given
        CompanyId companyId1 = CompanyId.of("1");
        CompanyId companyId2 = CompanyId.of("2");

        // when & then
        assertThat(companyId1).isNotEqualTo(companyId2);
    }
}