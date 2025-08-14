package com.backend.immilog.notice.domain.model;

import com.backend.immilog.notice.exception.NoticeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NoticeTargetingTest {

    @Test
    @DisplayName("NoticeTargeting 생성 - 정상 케이스")
    void createNoticeTargetingSuccessfully() {
        //given
        List<String> countryIds = List.of("KR", "JP");

        //when
        NoticeTargeting targeting = NoticeTargeting.of(countryIds);

        //then
        assertThat(targeting.targetCountryIds()).containsExactlyInAnyOrder("KR", "JP");
        assertThat(targeting.getTargetCount()).isEqualTo(2);
        assertThat(targeting.isGlobal()).isTrue();
    }

    @Test
    @DisplayName("NoticeTargeting 생성 - 단일 국가")
    void createNoticeTargetingWithSingleCountry() {
        //given
        List<String> countryIds = List.of("KR");

        //when
        NoticeTargeting targeting = NoticeTargeting.of(countryIds);

        //then
        assertThat(targeting.targetCountryIds()).containsExactly("KR");
        assertThat(targeting.getTargetCount()).isEqualTo(1);
        assertThat(targeting.isGlobal()).isFalse();
    }

    @Test
    @DisplayName("NoticeTargeting 생성 - 중복 국가 제거")
    void createNoticeTargetingWithDuplicateCountries() {
        //given
        List<String> countryIds = List.of("KR", "JP", "KR");

        //when
        NoticeTargeting targeting = NoticeTargeting.of(countryIds);

        //then
        assertThat(targeting.targetCountryIds()).containsExactlyInAnyOrder("KR", "JP");
        assertThat(targeting.getTargetCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("NoticeTargeting 생성 실패 - null 국가 목록")
    void createNoticeTargetingFailWhenCountriesIsNull() {
        //given
        List<String> countryIds = null;

        //when & then
        assertThatThrownBy(() -> NoticeTargeting.of(countryIds))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("NoticeTargeting 생성 실패 - 빈 국가 목록")
    void createNoticeTargetingFailWhenCountriesIsEmpty() {
        //given
        List<String> countryIds = List.of();

        //when & then
        assertThatThrownBy(() -> NoticeTargeting.of(countryIds))
                .isInstanceOf(NoticeException.class);
    }

//    @Test
//    @DisplayName("전체 국가 타겟팅 생성")
//    void createGlobalNoticeTargeting() {
//        //when
//        NoticeTargeting targeting = NoticeTargeting.all();
//
//        //then
//        assertThat(targeting.targetCountryIds()).contains("KR", "JP", "CN", "US", "UK", "DE", "FR", "CA", "AU", "SG", "MY", "TH", "VN", "PH", "IN", "BR", "MX", "AR", "CL", "CO");
//        assertThat(targeting.getTargetCount()).isEqualTo(20);
//        assertThat(targeting.isGlobal()).isTrue();
//    }

    @Test
    @DisplayName("타겟 국가 확인 - 포함된 경우")
    void isTargetedToWhenIncluded() {
        //given
        List<String> countryIds = List.of("KR", "JP");
        NoticeTargeting targeting = NoticeTargeting.of(countryIds);

        //when & then
        assertThat(targeting.isTargetedTo("KR")).isTrue();
        assertThat(targeting.isTargetedTo("JP")).isTrue();
    }

    @Test
    @DisplayName("타겟 국가 확인 - 포함되지 않은 경우")
    void isTargetedToWhenNotIncluded() {
        //given
        List<String> countryIds = List.of("KR", "JP");
        NoticeTargeting targeting = NoticeTargeting.of(countryIds);

        //when & then
        assertThat(targeting.isTargetedTo("CN")).isFalse();
        assertThat(targeting.isTargetedTo("MY")).isFalse();
    }

    @Test
    @DisplayName("글로벌 타겟팅 확인 - 모든 국가 포함")
    void isGlobalWhenAllCountriesIncluded() {
        //given
        List<String> allCountryIds = List.of("KR", "JP", "CN", "US", "UK", "DE", "FR", "CA", "AU", "SG", "MY", "TH", "VN", "PH", "IN", "BR", "MX", "AR", "CL", "CO");
        NoticeTargeting targeting = NoticeTargeting.of(allCountryIds);

        //when & then
        assertThat(targeting.isGlobal()).isTrue();
    }

    @Test
    @DisplayName("글로벌 타겟팅 확인 - 1개 초과 국가")
    void isGlobalWhenNotAllCountriesIncluded() {
        //given
        List<String> someCountryIds = List.of("KR", "JP");
        NoticeTargeting targeting = NoticeTargeting.of(someCountryIds);

        //when & then
        assertThat(targeting.isGlobal()).isTrue();
    }

    @Test
    @DisplayName("타겟 국가 수 확인")
    void getTargetCount() {
        //given
        List<String> countryIds = List.of("KR", "JP", "CN");
        NoticeTargeting targeting = NoticeTargeting.of(countryIds);

        //when & then
        assertThat(targeting.getTargetCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("NoticeTargeting 동등성 테스트")
    void equalityTest() {
        //given
        List<String> countryIds = List.of("KR", "JP");
        NoticeTargeting targeting1 = NoticeTargeting.of(countryIds);
        NoticeTargeting targeting2 = NoticeTargeting.of(countryIds);
        NoticeTargeting targeting3 = NoticeTargeting.of(List.of("CN"));

        //when & then
        assertThat(targeting1).isEqualTo(targeting2);
        assertThat(targeting1).isNotEqualTo(targeting3);
        assertThat(targeting1.hashCode()).isEqualTo(targeting2.hashCode());
    }

    @Test
    @DisplayName("모든 Country enum 값으로 타겟팅 생성")
    void createNoticeTargetingWithAllCountryValues() {
        //given
        List<String> allCountryIds = List.of("KR", "JP", "CN", "US", "UK", "DE", "FR", "CA", "AU", "SG", "MY", "TH", "VN", "PH", "IN", "BR", "MX", "AR", "CL", "CO");

        //when
        NoticeTargeting targeting = NoticeTargeting.of(allCountryIds);

        //then
        assertThat(targeting.targetCountryIds()).hasSize(20);
        for (String countryId : allCountryIds) {
            assertThat(targeting.isTargetedTo(countryId)).isTrue();
        }
    }

    @Test
    @DisplayName("순서가 다른 동일한 국가 목록으로 타겟팅 생성")
    void createNoticeTargetingWithDifferentOrder() {
        //given
        List<String> countryIds1 = List.of("KR", "JP", "CN");
        List<String> countryIds2 = List.of("CN", "KR", "JP");

        //when
        NoticeTargeting targeting1 = NoticeTargeting.of(countryIds1);
        NoticeTargeting targeting2 = NoticeTargeting.of(countryIds2);

        //then
        assertThat(targeting1.targetCountryIds()).containsExactlyInAnyOrderElementsOf(targeting2.targetCountryIds());
        assertThat(targeting1.getTargetCount()).isEqualTo(targeting2.getTargetCount());
    }
}