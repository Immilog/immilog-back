package com.backend.immilog.report.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ReportIdTest {

    @Test
    @DisplayName("ReportId 생성 - 정상 케이스")
    void createReportIdSuccessfully() {
        //given
        String value = "reportId123";

        //when
        ReportId reportId = ReportId.of(value);

        //then
        assertThat(reportId.value()).isEqualTo(value);
    }

    @Test
    @DisplayName("ReportId 생성 실패 - null 값")
    void createReportIdFailWhenValueIsNull() {
        //given
        String value = null;

        //when & then
        assertThatThrownBy(() -> ReportId.of(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ReportId value must be not null or empty");
    }

    @Test
    @DisplayName("ReportId 생성 실패 - 빈 문자열")
    void createReportIdFailWhenValueIsEmpty() {
        //given
        String value = "";

        //when & then
        assertThatThrownBy(() -> ReportId.of(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ReportId value must be not null or empty");
    }

    @Test
    @DisplayName("ReportId 생성 실패 - 공백 문자열")
    void createReportIdFailWhenValueIsBlank() {
        //given
        String value = "   ";

        //when & then
        assertThatThrownBy(() -> ReportId.of(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ReportId value must be not null or empty");
    }

    @Test
    @DisplayName("ReportId 동등성 테스트")
    void equalityTest() {
        //given
        String value = "reportId123";
        ReportId reportId1 = ReportId.of(value);
        ReportId reportId2 = ReportId.of(value);
        ReportId reportId3 = ReportId.of("differentId");

        //when & then
        assertThat(reportId1).isEqualTo(reportId2);
        assertThat(reportId1).isNotEqualTo(reportId3);
        assertThat(reportId1.hashCode()).isEqualTo(reportId2.hashCode());
    }
}