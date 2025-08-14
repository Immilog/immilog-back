package com.backend.immilog.report.domain.model;

import com.backend.immilog.report.domain.enums.ReportReason;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ReportDescriptionTest {

    @Test
    @DisplayName("ReportDescription 생성 - 정상 케이스")
    void createReportDescriptionSuccessfully() {
        //given
        String value = "부적절한 내용입니다";

        //when
        ReportDescription description = ReportDescription.of(value);

        //then
        assertThat(description.value()).isEqualTo(value);
    }

    @Test
    @DisplayName("ReportDescription 생성 - 공백 제거")
    void createReportDescriptionWithTrimming() {
        //given
        String value = "  부적절한 내용입니다  ";
        String expectedValue = "부적절한 내용입니다";

        //when
        ReportDescription description = ReportDescription.of(value);

        //then
        assertThat(description.value()).isEqualTo(expectedValue);
    }

    @Test
    @DisplayName("ReportDescription 생성 실패 - null 값")
    void createReportDescriptionFailWhenValueIsNull() {
        //given
        String value = null;

        //when & then
        assertThatThrownBy(() -> ReportDescription.of(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Report description cannot be null or empty");
    }

    @Test
    @DisplayName("ReportDescription 생성 실패 - 빈 문자열")
    void createReportDescriptionFailWhenValueIsEmpty() {
        //given
        String value = "";

        //when & then
        assertThatThrownBy(() -> ReportDescription.of(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Report description cannot be null or empty");
    }

    @Test
    @DisplayName("ReportDescription 생성 실패 - 공백만 있는 문자열")
    void createReportDescriptionFailWhenValueIsBlank() {
        //given
        String value = "   ";

        //when & then
        assertThatThrownBy(() -> ReportDescription.of(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Report description cannot be null or empty");
    }

    @Test
    @DisplayName("ReportDescription 생성 실패 - 최대 길이 초과")
    void createReportDescriptionFailWhenValueExceedsMaxLength() {
        //given
        String value = "a".repeat(1001);

        //when & then
        assertThatThrownBy(() -> ReportDescription.of(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Report description cannot exceed 1000 characters");
    }

    @Test
    @DisplayName("ReportDescription 생성 - 최대 길이 경계값")
    void createReportDescriptionWithMaxLength() {
        //given
        String value = "a".repeat(1000);

        //when
        ReportDescription description = ReportDescription.of(value);

        //then
        assertThat(description.value()).hasSize(1000);
        assertThat(description.value()).isEqualTo(value);
    }

    @Test
    @DisplayName("신고 사유로부터 ReportDescription 생성")
    void createReportDescriptionFromReason() {
        //given
        ReportReason reason = ReportReason.INAPPROPRIATE_CONTENT;

        //when
        ReportDescription description = ReportDescription.fromReason(reason);

        //then
        assertThat(description.value()).isEqualTo(reason.getDescription());
    }

    @Test
    @DisplayName("모든 신고 사유로부터 ReportDescription 생성 테스트")
    void createReportDescriptionFromAllReasons() {
        //given & when & then
        for (ReportReason reason : ReportReason.values()) {
            ReportDescription description = ReportDescription.fromReason(reason);
            assertThat(description.value()).isEqualTo(reason.getDescription());
        }
    }

    @Test
    @DisplayName("ReportDescription 동등성 테스트")
    void equalityTest() {
        //given
        String value = "부적절한 내용입니다";
        ReportDescription description1 = ReportDescription.of(value);
        ReportDescription description2 = ReportDescription.of(value);
        ReportDescription description3 = ReportDescription.of("다른 내용입니다");

        //when & then
        assertThat(description1).isEqualTo(description2);
        assertThat(description1).isNotEqualTo(description3);
        assertThat(description1.hashCode()).isEqualTo(description2.hashCode());
    }
}