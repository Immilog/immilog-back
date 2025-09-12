package com.backend.immilog.post.domain.model;

import com.backend.immilog.post.domain.model.post.PublicStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PublicStatus Value Object")
class PublicStatusTest {

    @Test
    @DisplayName("PUBLIC status의 getValue는 Y 반환")
    void publicStatusValue() {
        assertThat(PublicStatus.PUBLIC.getValue()).isEqualTo("Y");
    }

    @Test
    @DisplayName("PRIVATE status의 getValue는 N 반환")
    void privateStatusValue() {
        assertThat(PublicStatus.PRIVATE.getValue()).isEqualTo("N");
    }

    @Test
    @DisplayName("PUBLIC status의 isPublic은 true 반환")
    void publicStatusIsPublic() {
        assertThat(PublicStatus.PUBLIC.isPublic()).isTrue();
        assertThat(PublicStatus.PUBLIC.isPrivate()).isFalse();
    }

    @Test
    @DisplayName("PRIVATE status의 isPrivate은 true 반환")
    void privateStatusIsPrivate() {
        assertThat(PublicStatus.PRIVATE.isPrivate()).isTrue();
        assertThat(PublicStatus.PRIVATE.isPublic()).isFalse();
    }

    @Test
    @DisplayName("fromValue Y는 PUBLIC 반환")
    void fromValueY() {
        assertThat(PublicStatus.fromValue("Y")).isEqualTo(PublicStatus.PUBLIC);
        assertThat(PublicStatus.fromValue("TRUE")).isEqualTo(PublicStatus.PUBLIC);
        assertThat(PublicStatus.fromValue("PUBLIC")).isEqualTo(PublicStatus.PUBLIC);
        assertThat(PublicStatus.fromValue("true")).isEqualTo(PublicStatus.PUBLIC);
        assertThat(PublicStatus.fromValue("public")).isEqualTo(PublicStatus.PUBLIC);
    }

    @Test
    @DisplayName("fromValue N은 PRIVATE 반환")
    void fromValueN() {
        assertThat(PublicStatus.fromValue("N")).isEqualTo(PublicStatus.PRIVATE);
        assertThat(PublicStatus.fromValue("FALSE")).isEqualTo(PublicStatus.PRIVATE);
        assertThat(PublicStatus.fromValue("PRIVATE")).isEqualTo(PublicStatus.PRIVATE);
        assertThat(PublicStatus.fromValue("false")).isEqualTo(PublicStatus.PRIVATE);
        assertThat(PublicStatus.fromValue("private")).isEqualTo(PublicStatus.PRIVATE);
    }

    @Test
    @DisplayName("fromValue null 시 예외 발생")
    void fromValueWithNull() {
        assertThatThrownBy(() -> PublicStatus.fromValue(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("PublicStatus value cannot be null");
    }

    @Test
    @DisplayName("fromValue 잘못된 값 시 예외 발생")
    void fromValueWithInvalidValue() {
        assertThatThrownBy(() -> PublicStatus.fromValue("INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid PublicStatus value: INVALID");
    }

    @Test
    @DisplayName("fromBoolean true는 PUBLIC 반환")
    void fromBooleanTrue() {
        assertThat(PublicStatus.fromBoolean(true)).isEqualTo(PublicStatus.PUBLIC);
    }

    @Test
    @DisplayName("fromBoolean false는 PRIVATE 반환")
    void fromBooleanFalse() {
        assertThat(PublicStatus.fromBoolean(false)).isEqualTo(PublicStatus.PRIVATE);
    }

    @Test
    @DisplayName("fromBoolean null 시 예외 발생")
    void fromBooleanWithNull() {
        assertThatThrownBy(() -> PublicStatus.fromBoolean(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("PublicStatus boolean cannot be null");
    }

    @Test
    @DisplayName("toString은 소문자 name 반환")
    void toStringReturnsLowerCaseName() {
        assertThat(PublicStatus.PUBLIC.toString()).isEqualTo("public");
        assertThat(PublicStatus.PRIVATE.toString()).isEqualTo("private");
    }
}