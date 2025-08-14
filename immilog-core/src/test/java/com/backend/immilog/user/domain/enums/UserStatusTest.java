package com.backend.immilog.user.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("UserStatus enum 테스트")
class UserStatusTest {

    @Test
    @DisplayName("모든 UserStatus 값이 존재한다")
    void allUserStatusValues() {
        // given
        UserStatus[] statuses = UserStatus.values();

        // when & then
        assertThat(statuses).hasSize(5);
        assertThat(statuses).contains(
                UserStatus.ACTIVE,
                UserStatus.INACTIVE,
                UserStatus.PENDING,
                UserStatus.REPORTED,
                UserStatus.BLOCKED
        );
    }

    @Test
    @DisplayName("UserStatus의 동등성이 정상 작동한다")
    void userStatusEquality() {
        // given
        UserStatus status1 = UserStatus.ACTIVE;
        UserStatus status2 = UserStatus.ACTIVE;
        UserStatus status3 = UserStatus.PENDING;

        // when & then
        assertThat(status1).isEqualTo(status2);
        assertThat(status1).isNotEqualTo(status3);
        assertThat(status1.hashCode()).isEqualTo(status2.hashCode());
    }

    @Test
    @DisplayName("UserStatus의 toString이 정상 작동한다")
    void userStatusToString() {
        // when & then
        assertThat(UserStatus.ACTIVE.toString()).isEqualTo("ACTIVE");
        assertThat(UserStatus.INACTIVE.toString()).isEqualTo("INACTIVE");
        assertThat(UserStatus.PENDING.toString()).isEqualTo("PENDING");
        assertThat(UserStatus.REPORTED.toString()).isEqualTo("REPORTED");
        assertThat(UserStatus.BLOCKED.toString()).isEqualTo("BLOCKED");
    }

    @Test
    @DisplayName("UserStatus는 name()으로 올바른 값을 반환한다")
    void userStatusName() {
        // when & then
        assertThat(UserStatus.ACTIVE.name()).isEqualTo("ACTIVE");
        assertThat(UserStatus.INACTIVE.name()).isEqualTo("INACTIVE");
        assertThat(UserStatus.PENDING.name()).isEqualTo("PENDING");
        assertThat(UserStatus.REPORTED.name()).isEqualTo("REPORTED");
        assertThat(UserStatus.BLOCKED.name()).isEqualTo("BLOCKED");
    }

    @Test
    @DisplayName("UserStatus.valueOf()가 정상 작동한다")
    void userStatusValueOf() {
        // when & then
        assertThat(UserStatus.valueOf("ACTIVE")).isEqualTo(UserStatus.ACTIVE);
        assertThat(UserStatus.valueOf("INACTIVE")).isEqualTo(UserStatus.INACTIVE);
        assertThat(UserStatus.valueOf("PENDING")).isEqualTo(UserStatus.PENDING);
        assertThat(UserStatus.valueOf("REPORTED")).isEqualTo(UserStatus.REPORTED);
        assertThat(UserStatus.valueOf("BLOCKED")).isEqualTo(UserStatus.BLOCKED);
    }

    @Test
    @DisplayName("잘못된 값으로 valueOf() 호출 시 예외가 발생한다")
    void userStatusValueOfWithInvalidValue() {
        // when & then
        assertThatThrownBy(() -> UserStatus.valueOf("INVALID"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> UserStatus.valueOf("active"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> UserStatus.valueOf(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("UserStatus의 ordinal이 정상 작동한다")
    void userStatusOrdinal() {
        // when & then
        assertThat(UserStatus.ACTIVE.ordinal()).isEqualTo(0);
        assertThat(UserStatus.INACTIVE.ordinal()).isEqualTo(1);
        assertThat(UserStatus.PENDING.ordinal()).isEqualTo(2);
        assertThat(UserStatus.REPORTED.ordinal()).isEqualTo(3);
        assertThat(UserStatus.BLOCKED.ordinal()).isEqualTo(4);
    }
}