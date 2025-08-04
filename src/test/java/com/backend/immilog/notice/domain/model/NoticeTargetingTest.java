package com.backend.immilog.notice.domain.model;

import com.backend.immilog.notice.exception.NoticeException;
import com.backend.immilog.shared.enums.Country;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class NoticeTargetingTest {

    @Test
    @DisplayName("NoticeTargeting 생성 - 정상 케이스")
    void createNoticeTargetingSuccessfully() {
        //given
        List<Country> countries = List.of(Country.SOUTH_KOREA, Country.JAPAN);

        //when
        NoticeTargeting targeting = NoticeTargeting.of(countries);

        //then
        assertThat(targeting.targetCountries()).containsExactlyInAnyOrder(Country.SOUTH_KOREA, Country.JAPAN);
        assertThat(targeting.getTargetCount()).isEqualTo(2);
        assertThat(targeting.isGlobal()).isFalse();
    }

    @Test
    @DisplayName("NoticeTargeting 생성 - 단일 국가")
    void createNoticeTargetingWithSingleCountry() {
        //given
        List<Country> countries = List.of(Country.SOUTH_KOREA);

        //when
        NoticeTargeting targeting = NoticeTargeting.of(countries);

        //then
        assertThat(targeting.targetCountries()).containsExactly(Country.SOUTH_KOREA);
        assertThat(targeting.getTargetCount()).isEqualTo(1);
        assertThat(targeting.isGlobal()).isFalse();
    }

    @Test
    @DisplayName("NoticeTargeting 생성 - 중복 국가 제거")
    void createNoticeTargetingWithDuplicateCountries() {
        //given
        List<Country> countries = List.of(Country.SOUTH_KOREA, Country.JAPAN, Country.SOUTH_KOREA);

        //when
        NoticeTargeting targeting = NoticeTargeting.of(countries);

        //then
        assertThat(targeting.targetCountries()).containsExactlyInAnyOrder(Country.SOUTH_KOREA, Country.JAPAN);
        assertThat(targeting.getTargetCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("NoticeTargeting 생성 실패 - null 국가 목록")
    void createNoticeTargetingFailWhenCountriesIsNull() {
        //given
        List<Country> countries = null;

        //when & then
        assertThatThrownBy(() -> NoticeTargeting.of(countries))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("NoticeTargeting 생성 실패 - 빈 국가 목록")
    void createNoticeTargetingFailWhenCountriesIsEmpty() {
        //given
        List<Country> countries = List.of();

        //when & then
        assertThatThrownBy(() -> NoticeTargeting.of(countries))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("전체 국가 타겟팅 생성")
    void createGlobalNoticeTargeting() {
        //when
        NoticeTargeting targeting = NoticeTargeting.all();

        //then
        assertThat(targeting.targetCountries()).containsExactlyInAnyOrder(Country.values());
        assertThat(targeting.getTargetCount()).isEqualTo(Country.values().length);
        assertThat(targeting.isGlobal()).isTrue();
    }

    @Test
    @DisplayName("타겟 국가 확인 - 포함된 경우")
    void isTargetedToWhenIncluded() {
        //given
        List<Country> countries = List.of(Country.SOUTH_KOREA, Country.JAPAN);
        NoticeTargeting targeting = NoticeTargeting.of(countries);

        //when & then
        assertThat(targeting.isTargetedTo(Country.SOUTH_KOREA)).isTrue();
        assertThat(targeting.isTargetedTo(Country.JAPAN)).isTrue();
    }

    @Test
    @DisplayName("타겟 국가 확인 - 포함되지 않은 경우")
    void isTargetedToWhenNotIncluded() {
        //given
        List<Country> countries = List.of(Country.SOUTH_KOREA, Country.JAPAN);
        NoticeTargeting targeting = NoticeTargeting.of(countries);

        //when & then
        assertThat(targeting.isTargetedTo(Country.CHINA)).isFalse();
        assertThat(targeting.isTargetedTo(Country.MALAYSIA)).isFalse();
    }

    @Test
    @DisplayName("글로벌 타겟팅 확인 - 모든 국가 포함")
    void isGlobalWhenAllCountriesIncluded() {
        //given
        List<Country> allCountries = List.of(Country.values());
        NoticeTargeting targeting = NoticeTargeting.of(allCountries);

        //when & then
        assertThat(targeting.isGlobal()).isTrue();
    }

    @Test
    @DisplayName("글로벌 타겟팅 확인 - 일부 국가만 포함")
    void isGlobalWhenNotAllCountriesIncluded() {
        //given
        List<Country> someCountries = List.of(Country.SOUTH_KOREA, Country.JAPAN);
        NoticeTargeting targeting = NoticeTargeting.of(someCountries);

        //when & then
        assertThat(targeting.isGlobal()).isFalse();
    }

    @Test
    @DisplayName("타겟 국가 수 확인")
    void getTargetCount() {
        //given
        List<Country> countries = List.of(Country.SOUTH_KOREA, Country.JAPAN, Country.CHINA);
        NoticeTargeting targeting = NoticeTargeting.of(countries);

        //when & then
        assertThat(targeting.getTargetCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("NoticeTargeting 동등성 테스트")
    void equalityTest() {
        //given
        List<Country> countries = List.of(Country.SOUTH_KOREA, Country.JAPAN);
        NoticeTargeting targeting1 = NoticeTargeting.of(countries);
        NoticeTargeting targeting2 = NoticeTargeting.of(countries);
        NoticeTargeting targeting3 = NoticeTargeting.of(List.of(Country.CHINA));

        //when & then
        assertThat(targeting1).isEqualTo(targeting2);
        assertThat(targeting1).isNotEqualTo(targeting3);
        assertThat(targeting1.hashCode()).isEqualTo(targeting2.hashCode());
    }

    @Test
    @DisplayName("모든 Country enum 값으로 타겟팅 생성")
    void createNoticeTargetingWithAllCountryValues() {
        //given
        List<Country> allCountries = List.of(Country.values());

        //when
        NoticeTargeting targeting = NoticeTargeting.of(allCountries);

        //then
        assertThat(targeting.targetCountries()).hasSize(Country.values().length);
        for (Country country : Country.values()) {
            assertThat(targeting.isTargetedTo(country)).isTrue();
        }
    }

    @Test
    @DisplayName("순서가 다른 동일한 국가 목록으로 타겟팅 생성")
    void createNoticeTargetingWithDifferentOrder() {
        //given
        List<Country> countries1 = List.of(Country.SOUTH_KOREA, Country.JAPAN, Country.CHINA);
        List<Country> countries2 = List.of(Country.CHINA, Country.SOUTH_KOREA, Country.JAPAN);

        //when
        NoticeTargeting targeting1 = NoticeTargeting.of(countries1);
        NoticeTargeting targeting2 = NoticeTargeting.of(countries2);

        //then
        assertThat(targeting1.targetCountries()).containsExactlyInAnyOrderElementsOf(targeting2.targetCountries());
        assertThat(targeting1.getTargetCount()).isEqualTo(targeting2.getTargetCount());
    }

    @Test
    @DisplayName("글로벌 타겟팅에서 모든 국가 확인")
    void globalTargetingIncludesAllCountries() {
        //given
        NoticeTargeting globalTargeting = NoticeTargeting.all();

        //when & then
        for (Country country : Country.values()) {
            assertThat(globalTargeting.isTargetedTo(country)).isTrue();
        }
    }
}