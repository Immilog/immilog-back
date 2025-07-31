package com.backend.immilog.user.application.command;

import com.backend.immilog.shared.enums.Country;
import com.backend.immilog.user.domain.enums.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserInfoUpdateCommand 테스트")
class UserInfoUpdateCommandTest {

    @Test
    @DisplayName("정상적인 값들로 UserInfoUpdateCommand를 생성할 수 있다")
    void createUserInfoUpdateCommand() {
        // given
        String nickName = "테스트유저";
        String profileImage = "https://example.com/image.jpg";
        Country country = Country.SOUTH_KOREA;
        Country interestCountry = Country.JAPAN;
        Double latitude = 37.5665;
        Double longitude = 126.9780;
        UserStatus status = UserStatus.ACTIVE;

        // when
        UserInfoUpdateCommand command = new UserInfoUpdateCommand(
                nickName, profileImage, country, interestCountry, latitude, longitude, status
        );

        // then
        assertThat(command.nickName()).isEqualTo(nickName);
        assertThat(command.profileImage()).isEqualTo(profileImage);
        assertThat(command.country()).isEqualTo(country);
        assertThat(command.interestCountry()).isEqualTo(interestCountry);
        assertThat(command.latitude()).isEqualTo(latitude);
        assertThat(command.longitude()).isEqualTo(longitude);
        assertThat(command.status()).isEqualTo(status);
    }

    @Test
    @DisplayName("null 프로필 이미지로 UserInfoUpdateCommand를 생성할 수 있다")
    void createUserInfoUpdateCommandWithNullProfileImage() {
        // given & when
        UserInfoUpdateCommand command = new UserInfoUpdateCommand(
                "닉네임",
                null,
                Country.SOUTH_KOREA,
                Country.SOUTH_KOREA,
                37.5665,
                126.9780,
                UserStatus.ACTIVE
        );

        // then
        assertThat(command.nickName()).isEqualTo("닉네임");
        assertThat(command.profileImage()).isNull();
        assertThat(command.country()).isEqualTo(Country.SOUTH_KOREA);
        assertThat(command.interestCountry()).isEqualTo(Country.SOUTH_KOREA);
        assertThat(command.latitude()).isEqualTo(37.5665);
        assertThat(command.longitude()).isEqualTo(126.9780);
        assertThat(command.status()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("null 좌표로 UserInfoUpdateCommand를 생성할 수 있다")
    void createUserInfoUpdateCommandWithNullCoordinates() {
        // given & when
        UserInfoUpdateCommand command = new UserInfoUpdateCommand(
                "닉네임",
                "https://example.com/image.jpg",
                Country.JAPAN,
                Country.SOUTH_KOREA,
                null,
                null,
                UserStatus.PENDING
        );

        // then
        assertThat(command.nickName()).isEqualTo("닉네임");
        assertThat(command.profileImage()).isEqualTo("https://example.com/image.jpg");
        assertThat(command.country()).isEqualTo(Country.JAPAN);
        assertThat(command.interestCountry()).isEqualTo(Country.SOUTH_KOREA);
        assertThat(command.latitude()).isNull();
        assertThat(command.longitude()).isNull();
        assertThat(command.status()).isEqualTo(UserStatus.PENDING);
    }

    @Test
    @DisplayName("다양한 국가 조합으로 UserInfoUpdateCommand를 생성할 수 있다")
    void createUserInfoUpdateCommandWithDifferentCountries() {
        // given & when
        UserInfoUpdateCommand koreanToJapan = new UserInfoUpdateCommand(
                "한일거주자",
                "https://example.com/image1.jpg",
                Country.SOUTH_KOREA,
                Country.JAPAN,
                37.5665,
                126.9780,
                UserStatus.ACTIVE
        );

        UserInfoUpdateCommand japanToKorea = new UserInfoUpdateCommand(
                "일한거주자",
                "https://example.com/image2.jpg",
                Country.JAPAN,
                Country.SOUTH_KOREA,
                35.6762,
                139.6503,
                UserStatus.ACTIVE
        );

        // then
        assertThat(koreanToJapan.country()).isEqualTo(Country.SOUTH_KOREA);
        assertThat(koreanToJapan.interestCountry()).isEqualTo(Country.JAPAN);

        assertThat(japanToKorea.country()).isEqualTo(Country.JAPAN);
        assertThat(japanToKorea.interestCountry()).isEqualTo(Country.SOUTH_KOREA);
    }

    @Test
    @DisplayName("다양한 사용자 상태로 UserInfoUpdateCommand를 생성할 수 있다")
    void createUserInfoUpdateCommandWithDifferentStatuses() {
        // given & when
        UserInfoUpdateCommand activeCommand = new UserInfoUpdateCommand(
                "활성유저", null, Country.SOUTH_KOREA, Country.SOUTH_KOREA,
                37.5665, 126.9780, UserStatus.ACTIVE
        );

        UserInfoUpdateCommand pendingCommand = new UserInfoUpdateCommand(
                "대기유저", null, Country.SOUTH_KOREA, Country.SOUTH_KOREA,
                37.5665, 126.9780, UserStatus.PENDING
        );

        UserInfoUpdateCommand blockedCommand = new UserInfoUpdateCommand(
                "차단유저", null, Country.SOUTH_KOREA, Country.SOUTH_KOREA,
                37.5665, 126.9780, UserStatus.BLOCKED
        );

        // then
        assertThat(activeCommand.status()).isEqualTo(UserStatus.ACTIVE);
        assertThat(pendingCommand.status()).isEqualTo(UserStatus.PENDING);
        assertThat(blockedCommand.status()).isEqualTo(UserStatus.BLOCKED);
    }

    @Test
    @DisplayName("극한값 좌표로 UserInfoUpdateCommand를 생성할 수 있다")
    void createUserInfoUpdateCommandWithExtremeCoordinates() {
        // given & when
        UserInfoUpdateCommand maxCoordinates = new UserInfoUpdateCommand(
                "극한좌표유저",
                "https://example.com/image.jpg",
                Country.SOUTH_KOREA,
                Country.SOUTH_KOREA,
                90.0,
                180.0,
                UserStatus.ACTIVE
        );

        UserInfoUpdateCommand minCoordinates = new UserInfoUpdateCommand(
                "최소좌표유저",
                "https://example.com/image.jpg",
                Country.SOUTH_KOREA,
                Country.SOUTH_KOREA,
                -90.0,
                -180.0,
                UserStatus.ACTIVE
        );

        // then
        assertThat(maxCoordinates.latitude()).isEqualTo(90.0);
        assertThat(maxCoordinates.longitude()).isEqualTo(180.0);

        assertThat(minCoordinates.latitude()).isEqualTo(-90.0);
        assertThat(minCoordinates.longitude()).isEqualTo(-180.0);
    }

    @Test
    @DisplayName("정밀한 소수점 좌표로 UserInfoUpdateCommand를 생성할 수 있다")
    void createUserInfoUpdateCommandWithPreciseCoordinates() {
        // given & when
        UserInfoUpdateCommand command = new UserInfoUpdateCommand(
                "정밀좌표유저",
                "https://example.com/image.jpg",
                Country.SOUTH_KOREA,
                Country.JAPAN,
                37.56656789,
                126.97801234,
                UserStatus.ACTIVE
        );

        // then
        assertThat(command.latitude()).isEqualTo(37.56656789);
        assertThat(command.longitude()).isEqualTo(126.97801234);
    }

    @Test
    @DisplayName("0.0 좌표로 UserInfoUpdateCommand를 생성할 수 있다")
    void createUserInfoUpdateCommandWithZeroCoordinates() {
        // given & when
        UserInfoUpdateCommand command = new UserInfoUpdateCommand(
                "영점좌표유저",
                "https://example.com/image.jpg",
                Country.SOUTH_KOREA,
                Country.SOUTH_KOREA,
                0.0,
                0.0,
                UserStatus.ACTIVE
        );

        // then
        assertThat(command.latitude()).isEqualTo(0.0);
        assertThat(command.longitude()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("긴 URL의 프로필 이미지로 UserInfoUpdateCommand를 생성할 수 있다")
    void createUserInfoUpdateCommandWithLongImageUrl() {
        // given
        String longImageUrl = "https://very-long-domain-name-for-testing-purposes.example.com/very/long/path/to/profile/image/with/many/subdirectories/and/a/very/long/filename.jpg";

        // when
        UserInfoUpdateCommand command = new UserInfoUpdateCommand(
                "긴URL유저",
                longImageUrl,
                Country.SOUTH_KOREA,
                Country.SOUTH_KOREA,
                37.5665,
                126.9780,
                UserStatus.ACTIVE
        );

        // then
        assertThat(command.profileImage()).isEqualTo(longImageUrl);
    }

    @Test
    @DisplayName("특수 문자가 포함된 닉네임으로 UserInfoUpdateCommand를 생성할 수 있다")
    void createUserInfoUpdateCommandWithSpecialCharactersInNickname() {
        // given & when
        UserInfoUpdateCommand koreanSpecial = new UserInfoUpdateCommand(
                "특수문자유저!@#$%",
                "https://example.com/image.jpg",
                Country.SOUTH_KOREA,
                Country.SOUTH_KOREA,
                37.5665,
                126.9780,
                UserStatus.ACTIVE
        );

        UserInfoUpdateCommand japaneseSpecial = new UserInfoUpdateCommand(
                "日本語ユーザー123",
                "https://example.com/image.jpg",
                Country.JAPAN,
                Country.JAPAN,
                35.6762,
                139.6503,
                UserStatus.ACTIVE
        );

        UserInfoUpdateCommand englishSpecial = new UserInfoUpdateCommand(
                "User_With-Special.Chars",
                "https://example.com/image.jpg",
                Country.SOUTH_KOREA,
                Country.JAPAN,
                37.5665,
                126.9780,
                UserStatus.ACTIVE
        );

        // then
        assertThat(koreanSpecial.nickName()).isEqualTo("특수문자유저!@#$%");
        assertThat(japaneseSpecial.nickName()).isEqualTo("日本語ユーザー123");
        assertThat(englishSpecial.nickName()).isEqualTo("User_With-Special.Chars");
    }

    @Test
    @DisplayName("UserInfoUpdateCommand record의 동등성이 정상 작동한다")
    void userInfoUpdateCommandEquality() {
        // given
        UserInfoUpdateCommand command1 = new UserInfoUpdateCommand(
                "테스트유저",
                "https://example.com/image.jpg",
                Country.SOUTH_KOREA,
                Country.JAPAN,
                37.5665,
                126.9780,
                UserStatus.ACTIVE
        );

        UserInfoUpdateCommand command2 = new UserInfoUpdateCommand(
                "테스트유저",
                "https://example.com/image.jpg",
                Country.SOUTH_KOREA,
                Country.JAPAN,
                37.5665,
                126.9780,
                UserStatus.ACTIVE
        );

        UserInfoUpdateCommand command3 = new UserInfoUpdateCommand(
                "다른유저",
                "https://example.com/image.jpg",
                Country.SOUTH_KOREA,
                Country.JAPAN,
                37.5665,
                126.9780,
                UserStatus.ACTIVE
        );

        // when & then
        assertThat(command1).isEqualTo(command2);
        assertThat(command1).isNotEqualTo(command3);
        assertThat(command1.hashCode()).isEqualTo(command2.hashCode());
    }

    @Test
    @DisplayName("UserInfoUpdateCommand record의 toString이 정상 작동한다")
    void userInfoUpdateCommandToString() {
        // given
        UserInfoUpdateCommand command = new UserInfoUpdateCommand(
                "테스트유저",
                "https://example.com/image.jpg",
                Country.SOUTH_KOREA,
                Country.JAPAN,
                37.5665,
                126.9780,
                UserStatus.ACTIVE
        );

        // when
        String toString = command.toString();

        // then
        assertThat(toString).contains("UserInfoUpdateCommand");
        assertThat(toString).contains("테스트유저");
        assertThat(toString).contains("https://example.com/image.jpg");
        assertThat(toString).contains("SOUTH_KOREA");
        assertThat(toString).contains("JAPAN");
        assertThat(toString).contains("37.5665");
        assertThat(toString).contains("126.978");
        assertThat(toString).contains("ACTIVE");
    }

    @Test
    @DisplayName("모든 필드가 null인 UserInfoUpdateCommand를 생성할 수 있다")
    void createUserInfoUpdateCommandWithAllNullFields() {
        // given & when
        UserInfoUpdateCommand command = new UserInfoUpdateCommand(
                null, null, null, null, null, null, null
        );

        // then
        assertThat(command.nickName()).isNull();
        assertThat(command.profileImage()).isNull();
        assertThat(command.country()).isNull();
        assertThat(command.interestCountry()).isNull();
        assertThat(command.latitude()).isNull();
        assertThat(command.longitude()).isNull();
        assertThat(command.status()).isNull();
    }

    @Test
    @DisplayName("빈 문자열 닉네임으로 UserInfoUpdateCommand를 생성할 수 있다")
    void createUserInfoUpdateCommandWithEmptyNickname() {
        // given & when
        UserInfoUpdateCommand command = new UserInfoUpdateCommand(
                "",
                "https://example.com/image.jpg",
                Country.SOUTH_KOREA,
                Country.SOUTH_KOREA,
                37.5665,
                126.9780,
                UserStatus.ACTIVE
        );

        // then
        assertThat(command.nickName()).isEqualTo("");
    }

    @Test
    @DisplayName("같은 국가를 거주지와 관심국가로 설정할 수 있다")
    void createUserInfoUpdateCommandWithSameCountryAndInterest() {
        // given & when
        UserInfoUpdateCommand command = new UserInfoUpdateCommand(
                "동일국가유저",
                "https://example.com/image.jpg",
                Country.SOUTH_KOREA,
                Country.SOUTH_KOREA,
                37.5665,
                126.9780,
                UserStatus.ACTIVE
        );

        // then
        assertThat(command.country()).isEqualTo(Country.SOUTH_KOREA);
        assertThat(command.interestCountry()).isEqualTo(Country.SOUTH_KOREA);
        assertThat(command.country()).isEqualTo(command.interestCountry());
    }
}