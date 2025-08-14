package com.backend.immilog.user.application.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserSignUpCommand 테스트")
class UserSignUpCommandTest {

    @Test
    @DisplayName("정상적인 값들로 UserSignUpCommand를 생성할 수 있다")
    void createUserSignUpCommand() {
        // given
        String nickName = "테스트유저";
        String password = "password123";
        String email = "test@example.com";
        String country = "SOUTH_KOREA";
        String interestCountry = "JAPAN";
        String region = "서울특별시";
        String profileImage = "https://example.com/image.jpg";

        // when
        UserSignUpCommand command = new UserSignUpCommand(
                nickName,
                password,
                email,
                country,
                interestCountry,
                region,
                profileImage
        );

        // then
        assertThat(command.nickName()).isEqualTo(nickName);
        assertThat(command.password()).isEqualTo(password);
        assertThat(command.email()).isEqualTo(email);
        assertThat(command.country()).isEqualTo(country);
        assertThat(command.interestCountry()).isEqualTo(interestCountry);
        assertThat(command.region()).isEqualTo(region);
        assertThat(command.profileImage()).isEqualTo(profileImage);
    }

    @Test
    @DisplayName("null 값들로도 UserSignUpCommand를 생성할 수 있다")
    void createUserSignUpCommandWithNullValues() {
        // given & when
        UserSignUpCommand command = new UserSignUpCommand(
                "nickname", "password", "email@test.com", "SOUTH_KOREA",
                null, "region", null
        );

        // then
        assertThat(command.nickName()).isEqualTo("nickname");
        assertThat(command.password()).isEqualTo("password");
        assertThat(command.email()).isEqualTo("email@test.com");
        assertThat(command.country()).isEqualTo("SOUTH_KOREA");
        assertThat(command.interestCountry()).isNull();
        assertThat(command.region()).isEqualTo("region");
        assertThat(command.profileImage()).isNull();
    }

    @Test
    @DisplayName("UserSignUpCommand record의 동등성이 정상 작동한다")
    void userSignUpCommandEquality() {
        // given
        UserSignUpCommand command1 = new UserSignUpCommand(
                "nickname",
                "password",
                "email@test.com",
                "SOUTH_KOREA",
                "JAPAN",
                "region",
                "image.jpg"
        );
        UserSignUpCommand command2 = new UserSignUpCommand(
                "nickname",
                "password",
                "email@test.com",
                "SOUTH_KOREA",
                "JAPAN",
                "region",
                "image.jpg"
        );
        UserSignUpCommand command3 = new UserSignUpCommand(
                "different",
                "password",
                "email@test.com",
                "SOUTH_KOREA",
                "JAPAN",
                "region",
                "image.jpg"
        );

        // when & then
        assertThat(command1).isEqualTo(command2);
        assertThat(command1).isNotEqualTo(command3);
        assertThat(command1.hashCode()).isEqualTo(command2.hashCode());
    }

    @Test
    @DisplayName("UserSignUpCommand record의 toString이 정상 작동한다")
    void userSignUpCommandToString() {
        // given
        UserSignUpCommand command = new UserSignUpCommand(
                "nickname",
                "password",
                "email@test.com",
                "SOUTH_KOREA",
                "JAPAN",
                "region",
                "image.jpg"
        );

        // when
        String toString = command.toString();

        // then
        assertThat(toString).contains("UserSignUpCommand");
        assertThat(toString).contains("nickname");
        assertThat(toString).contains("email@test.com");
        assertThat(toString).contains("SOUTH_KOREA");
        // 보안상 password는 toString에 포함되지만 실제 서비스에서는 마스킹 고려
    }

    @Test
    @DisplayName("빈 문자열로도 UserSignUpCommand를 생성할 수 있다")
    void createUserSignUpCommandWithEmptyStrings() {
        // given & when
        UserSignUpCommand command = new UserSignUpCommand(
                "", "", "", "", "", "", ""
        );

        // then
        assertThat(command.nickName()).isEmpty();
        assertThat(command.password()).isEmpty();
        assertThat(command.email()).isEmpty();
        assertThat(command.country()).isEmpty();
        assertThat(command.interestCountry()).isEmpty();
        assertThat(command.region()).isEmpty();
        assertThat(command.profileImage()).isEmpty();
    }
}