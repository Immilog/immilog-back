package com.backend.immilog.report.domain.model;

import com.backend.immilog.report.domain.enums.ReportTargetType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ReportTarget Value Object")
class ReportTargetTest {

    @Nested
    @DisplayName("생성")
    class Create {

        @Test
        @DisplayName("사용자 대상 생성")
        void createUserTarget() {
            var target = ReportTarget.user("user123");

            assertThat(target.type()).isEqualTo(ReportTargetType.USER);
            assertThat(target.targetId()).isEqualTo("user123");
        }

        @Test
        @DisplayName("게시물 대상 생성")
        void createPostTarget() {
            var target = ReportTarget.post("post123");

            assertThat(target.type()).isEqualTo(ReportTargetType.POST);
            assertThat(target.targetId()).isEqualTo("post123");
        }

        @Test
        @DisplayName("댓글 대상 생성")
        void createCommentTarget() {
            var target = ReportTarget.comment("comment123");

            assertThat(target.type()).isEqualTo(ReportTargetType.COMMENT);
            assertThat(target.targetId()).isEqualTo("comment123");
        }

        @Test
        @DisplayName("일반 생성 메서드")
        void createWithOf() {
            var target = ReportTarget.of(ReportTargetType.USER, "user123");

            assertThat(target.type()).isEqualTo(ReportTargetType.USER);
            assertThat(target.targetId()).isEqualTo("user123");
        }

        @Test
        @DisplayName("타입이 null인 경우 예외")
        void nullType() {
            assertThatThrownBy(() -> ReportTarget.of(null, "user123"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("ReportTargetType cannot be null");
        }

        @Test
        @DisplayName("대상 ID가 null인 경우 예외")
        void nullTargetId() {
            assertThatThrownBy(() -> ReportTarget.of(ReportTargetType.USER, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Target ID must be not null or blank");
        }

        @Test
        @DisplayName("대상 ID가 빈 문자열인 경우 예외")
        void blankTargetId() {
            assertThatThrownBy(() -> ReportTarget.of(ReportTargetType.USER, ""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Target ID must be not null or blank");

            assertThatThrownBy(() -> ReportTarget.of(ReportTargetType.USER, "   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Target ID must be not null or blank");
        }
    }

    @Nested
    @DisplayName("동등성")
    class Equality {

        @Test
        @DisplayName("같은 값으로 생성된 객체는 동일")
        void sameValues() {
            var target1 = ReportTarget.user("user123");
            var target2 = ReportTarget.user("user123");

            assertThat(target1).isEqualTo(target2);
            assertThat(target1.hashCode()).isEqualTo(target2.hashCode());
        }

        @Test
        @DisplayName("다른 값으로 생성된 객체는 다름")
        void differentValues() {
            var target1 = ReportTarget.user("user123");
            var target2 = ReportTarget.user("user456");
            var target3 = ReportTarget.post("user123");

            assertThat(target1).isNotEqualTo(target2);
            assertThat(target1).isNotEqualTo(target3);
        }
    }
}