package com.backend.immilog.report.domain.model;

import com.backend.immilog.report.domain.enums.ReportReason;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ReportDescription Value Object")
class ReportDescriptionTest {

    @Nested
    @DisplayName("생성")
    class Create {

        @Test
        @DisplayName("유효한 설명으로 생성")
        void createWithValidDescription() {
            var description = ReportDescription.of("유효한 설명입니다");

            assertThat(description.value()).isEqualTo("유효한 설명입니다");
        }

        @Test
        @DisplayName("앞뒤 공백 제거")
        void trimWhitespace() {
            var description = ReportDescription.of("   앞뒤 공백   ");

            assertThat(description.value()).isEqualTo("앞뒤 공백");
        }

        @Test
        @DisplayName("최대 길이까지 허용")
        void maxLength() {
            var longText = "a".repeat(1000);
            var description = ReportDescription.of(longText);

            assertThat(description.value()).hasSize(1000);
        }

        @Test
        @DisplayName("사유에서 설명 생성")
        void fromReason() {
            var description = ReportDescription.fromReason(ReportReason.SPAM);

            assertThat(description.value()).isEqualTo(ReportReason.SPAM.getDescription());
        }

        @Test
        @DisplayName("null 값으로 생성 시 예외")
        void nullValue() {
            assertThatThrownBy(() -> ReportDescription.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Report description cannot be null or empty");
        }

        @Test
        @DisplayName("빈 문자열로 생성 시 예외")
        void emptyValue() {
            assertThatThrownBy(() -> ReportDescription.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Report description cannot be null or empty");

            assertThatThrownBy(() -> ReportDescription.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Report description cannot be null or empty");
        }

        @Test
        @DisplayName("최대 길이 초과 시 예외")
        void exceedMaxLength() {
            var tooLongText = "a".repeat(1001);

            assertThatThrownBy(() -> ReportDescription.of(tooLongText))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Report description cannot exceed 1000 characters");
        }
    }

    @Nested
    @DisplayName("동등성")
    class Equality {

        @Test
        @DisplayName("같은 값으로 생성된 객체는 동일")
        void sameValues() {
            var desc1 = ReportDescription.of("같은 설명");
            var desc2 = ReportDescription.of("같은 설명");

            assertThat(desc1).isEqualTo(desc2);
            assertThat(desc1.hashCode()).isEqualTo(desc2.hashCode());
        }

        @Test
        @DisplayName("다른 값으로 생성된 객체는 다름")
        void differentValues() {
            var desc1 = ReportDescription.of("설명1");
            var desc2 = ReportDescription.of("설명2");

            assertThat(desc1).isNotEqualTo(desc2);
        }
    }
}