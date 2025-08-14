package com.backend.immilog.user.application.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserSignInCommand 테스트")
class UserSignInCommandTest {

    @Test
    @DisplayName("정상적인 값들로 UserSignInCommand를 생성할 수 있다")
    void createUserSignInCommand() {
        // given
        String email = "test@example.com";
        String password = "password123";
        Double latitude = 37.5665;
        Double longitude = 126.9780;

        // when
        UserSignInCommand command = new UserSignInCommand(email, password, latitude, longitude);

        // then
        assertThat(command.email()).isEqualTo(email);
        assertThat(command.password()).isEqualTo(password);
        assertThat(command.latitude()).isEqualTo(latitude);
        assertThat(command.longitude()).isEqualTo(longitude);
    }

    @Test
    @DisplayName("null 좌표로도 UserSignInCommand를 생성할 수 있다")
    void createUserSignInCommandWithNullCoordinates() {
        // given & when
        UserSignInCommand command = new UserSignInCommand(
                "test@example.com",
                "password123",
                null,
                null
        );

        // then
        assertThat(command.email()).isEqualTo("test@example.com");
        assertThat(command.password()).isEqualTo("password123");
        assertThat(command.latitude()).isNull();
        assertThat(command.longitude()).isNull();
    }

    @Test
    @DisplayName("다양한 좌표 값으로 UserSignInCommand를 생성할 수 있다")
    void createUserSignInCommandWithVariousCoordinates() {
        // given & when
        UserSignInCommand seoulCommand = new UserSignInCommand(
                "seoul@example.com",
                "password",
                37.5665,
                126.9780
        );
        UserSignInCommand tokyoCommand = new UserSignInCommand(
                "tokyo@example.com",
                "password",
                35.6762,
                139.6503
        );
        UserSignInCommand nyCommand = new UserSignInCommand(
                "ny@example.com",
                "password",
                40.7128,
                -74.0060
        );

        // then
        assertThat(seoulCommand.latitude()).isEqualTo(37.5665);
        assertThat(seoulCommand.longitude()).isEqualTo(126.9780);

        assertThat(tokyoCommand.latitude()).isEqualTo(35.6762);
        assertThat(tokyoCommand.longitude()).isEqualTo(139.6503);

        assertThat(nyCommand.latitude()).isEqualTo(40.7128);
        assertThat(nyCommand.longitude()).isEqualTo(-74.0060);
    }

    @Test
    @DisplayName("극한값 좌표로 UserSignInCommand를 생성할 수 있다")
    void createUserSignInCommandWithExtremeCoordinates() {
        // given & when
        UserSignInCommand command = new UserSignInCommand(
                "test@example.com",
                "password",
                90.0, 180.0 // 최대값
        );
        UserSignInCommand command2 = new UserSignInCommand(
                "test2@example.com",
                "password",
                -90.0, -180.0 // 최소값
        );

        // then
        assertThat(command.latitude()).isEqualTo(90.0);
        assertThat(command.longitude()).isEqualTo(180.0);
        assertThat(command2.latitude()).isEqualTo(-90.0);
        assertThat(command2.longitude()).isEqualTo(-180.0);
    }

    @Test
    @DisplayName("UserSignInCommand record의 동등성이 정상 작동한다")
    void userSignInCommandEquality() {
        // given
        UserSignInCommand command1 = new UserSignInCommand(
                "test@example.com",
                "password123",
                37.5665,
                126.9780
        );
        UserSignInCommand command2 = new UserSignInCommand(
                "test@example.com",
                "password123",
                37.5665,
                126.9780
        );
        UserSignInCommand command3 = new UserSignInCommand(
                "different@example.com",
                "password123",
                37.5665,
                126.9780
        );

        // when & then
        assertThat(command1).isEqualTo(command2);
        assertThat(command1).isNotEqualTo(command3);
        assertThat(command1.hashCode()).isEqualTo(command2.hashCode());
    }

    @Test
    @DisplayName("UserSignInCommand record의 toString이 정상 작동한다")
    void userSignInCommandToString() {
        // given
        UserSignInCommand command = new UserSignInCommand(
                "test@example.com",
                "password123",
                37.5665,
                126.978
        );

        // when
        String toString = command.toString();

        // then
        assertThat(toString).contains("UserSignInCommand");
        assertThat(toString).contains("test@example.com");
        assertThat(toString).contains("37.5665");
        assertThat(toString).contains("126.978");
    }

    @Test
    @DisplayName("소수점 정밀도가 있는 좌표로 UserSignInCommand를 생성할 수 있다")
    void createUserSignInCommandWithPreciseCoordinates() {
        // given & when
        UserSignInCommand command = new UserSignInCommand(
                "precise@example.com",

                "password",

                37.56656789,

                126.97801234
        );

        // then
        assertThat(command.latitude()).isEqualTo(37.56656789);
        assertThat(command.longitude()).isEqualTo(126.97801234);
    }

    @Test
    @DisplayName("0.0 좌표로 UserSignInCommand를 생성할 수 있다")
    void createUserSignInCommandWithZeroCoordinates() {
        // given & when
        UserSignInCommand command = new UserSignInCommand(
                "zero@example.com",
                "password",
                0.0,
                0.0
        );

        // then
        assertThat(command.latitude()).isEqualTo(0.0);
        assertThat(command.longitude()).isEqualTo(0.0);
    }
}