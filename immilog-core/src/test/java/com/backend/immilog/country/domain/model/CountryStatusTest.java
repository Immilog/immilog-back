package com.backend.immilog.country.domain.model;

import com.backend.immilog.country.domain.CountryStatus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CountryStatusTest {

    @Test
    @DisplayName("ACTIVE 상수값이 올바르게 정의되어 있다")
    void activeConstantIsCorrectlyDefined() {
        assertThat(CountryStatus.ACTIVE).isNotNull();
        assertThat(CountryStatus.ACTIVE.name()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("INACTIVE 상수값이 올바르게 정의되어 있다")
    void inactiveConstantIsCorrectlyDefined() {
        assertThat(CountryStatus.INACTIVE).isNotNull();
        assertThat(CountryStatus.INACTIVE.name()).isEqualTo("INACTIVE");
    }

    @Test
    @DisplayName("CountryStatus enum의 모든 값을 검증한다")
    void allCountryStatusValues() {
        CountryStatus[] values = CountryStatus.values();
        
        assertThat(values).hasSize(2);
        assertThat(values).containsExactlyInAnyOrder(
            CountryStatus.ACTIVE,
            CountryStatus.INACTIVE
        );
    }

    @Test
    @DisplayName("valueOf 메서드로 문자열을 통해 enum 값을 가져올 수 있다")
    void valueOfMethod() {
        assertThat(CountryStatus.valueOf("ACTIVE")).isEqualTo(CountryStatus.ACTIVE);
        assertThat(CountryStatus.valueOf("INACTIVE")).isEqualTo(CountryStatus.INACTIVE);
    }

    @Test
    @DisplayName("enum 값의 순서가 올바르다")
    void enumOrderIsCorrect() {
        CountryStatus[] values = CountryStatus.values();
        
        assertThat(values[0]).isEqualTo(CountryStatus.ACTIVE);
        assertThat(values[1]).isEqualTo(CountryStatus.INACTIVE);
    }

    @Test
    @DisplayName("enum 값의 ordinal이 올바르다")
    void ordinalValues() {
        assertThat(CountryStatus.ACTIVE.ordinal()).isEqualTo(0);
        assertThat(CountryStatus.INACTIVE.ordinal()).isEqualTo(1);
    }
}