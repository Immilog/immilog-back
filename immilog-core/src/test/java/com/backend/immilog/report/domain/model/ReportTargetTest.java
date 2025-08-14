package com.backend.immilog.report.domain.model;

import com.backend.immilog.report.domain.enums.ReportTargetType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ReportTargetTest {

    @Test
    @DisplayName("ReportTarget 생성 - 정상 케이스")
    void createReportTargetSuccessfully() {
        //given
        ReportTargetType type = ReportTargetType.USER;
        String targetId = "userId123";

        //when
        ReportTarget reportTarget = ReportTarget.of(type, targetId);

        //then
        assertThat(reportTarget.type()).isEqualTo(type);
        assertThat(reportTarget.targetId()).isEqualTo(targetId);
    }

    @Test
    @DisplayName("ReportTarget 생성 실패 - null 타입")
    void createReportTargetFailWhenTypeIsNull() {
        //given
        ReportTargetType type = null;
        String targetId = "userId123";

        //when & then
        assertThatThrownBy(() -> ReportTarget.of(type, targetId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ReportTargetType cannot be null");
    }

    @Test
    @DisplayName("ReportTarget 생성 실패 - null 타겟 ID")
    void createReportTargetFailWhenTargetIdIsNull() {
        //given
        ReportTargetType type = ReportTargetType.USER;
        String targetId = null;

        //when & then
        assertThatThrownBy(() -> ReportTarget.of(type, targetId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Target ID must be not null or blank");
    }

    @Test
    @DisplayName("ReportTarget 생성 실패 - 빈 타겟 ID")
    void createReportTargetFailWhenTargetIdIsEmpty() {
        //given
        ReportTargetType type = ReportTargetType.USER;
        String targetId = "";

        //when & then
        assertThatThrownBy(() -> ReportTarget.of(type, targetId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Target ID must be not null or blank");
    }

    @Test
    @DisplayName("ReportTarget 생성 실패 - 공백 타겟 ID")
    void createReportTargetFailWhenTargetIdIsBlank() {
        //given
        ReportTargetType type = ReportTargetType.USER;
        String targetId = "   ";

        //when & then
        assertThatThrownBy(() -> ReportTarget.of(type, targetId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Target ID must be not null or blank");
    }

    @Test
    @DisplayName("사용자 타겟 생성")
    void createUserTarget() {
        //given
        String userId = "userId123";

        //when
        ReportTarget userTarget = ReportTarget.user(userId);

        //then
        assertThat(userTarget.type()).isEqualTo(ReportTargetType.USER);
        assertThat(userTarget.targetId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("게시글 타겟 생성")
    void createPostTarget() {
        //given
        String postId = "postId123";

        //when
        ReportTarget postTarget = ReportTarget.post(postId);

        //then
        assertThat(postTarget.type()).isEqualTo(ReportTargetType.POST);
        assertThat(postTarget.targetId()).isEqualTo(postId);
    }

    @Test
    @DisplayName("댓글 타겟 생성")
    void createCommentTarget() {
        //given
        String commentId = "commentId123";

        //when
        ReportTarget commentTarget = ReportTarget.comment(commentId);

        //then
        assertThat(commentTarget.type()).isEqualTo(ReportTargetType.COMMENT);
        assertThat(commentTarget.targetId()).isEqualTo(commentId);
    }

    @Test
    @DisplayName("ReportTarget 동등성 테스트")
    void equalityTest() {
        //given
        ReportTarget target1 = ReportTarget.user("userId123");
        ReportTarget target2 = ReportTarget.user("userId123");
        ReportTarget target3 = ReportTarget.user("differentUserId");
        ReportTarget target4 = ReportTarget.post("userId123");

        //when & then
        assertThat(target1).isEqualTo(target2);
        assertThat(target1).isNotEqualTo(target3);
        assertThat(target1).isNotEqualTo(target4);
        assertThat(target1.hashCode()).isEqualTo(target2.hashCode());
    }
}