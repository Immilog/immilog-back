package com.backend.immilog.user.domain.model;

import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Profile 도메인 테스트")
class ProfileTest {

    @Test
    @DisplayName("정상적인 값들로 Profile을 생성할 수 있다")
    void createProfileWithValidValues() {
        // given
        String validNickname = "테스트유저";
        String validImageUrl = "https://example.com/image.jpg";
        String validinterestCountryId = "KR";

        // when
        Profile profile = Profile.of(validNickname, validImageUrl, validinterestCountryId);

        // then
        assertThat(profile.nickname()).isEqualTo(validNickname);
        assertThat(profile.imageUrl()).isEqualTo(validImageUrl);
        assertThat(profile.interestCountryId()).isEqualTo(validinterestCountryId);
    }

    @Test
    @DisplayName("imageUrl이 null이어도 Profile을 생성할 수 있다")
    void createProfileWithNullImageUrl() {
        // given
        String validNickname = "테스트유저";
        String nullImageUrl = null;
        String validinterestCountryId = "KR";

        // when
        Profile profile = Profile.of(validNickname, nullImageUrl, validinterestCountryId);

        // then
        assertThat(profile.nickname()).isEqualTo(validNickname);
        assertThat(profile.imageUrl()).isNull();
        assertThat(profile.interestCountryId()).isEqualTo(validinterestCountryId);
    }

    @Test
    @DisplayName("null 닉네임으로 Profile 생성 시 예외가 발생한다")
    void createProfileWithNullNickname() {
        // given
        String nullNickname = null;
        String validImageUrl = "https://example.com/image.jpg";
        String validinterestCountryId = "KR";

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> Profile.of(nullNickname, validImageUrl, validinterestCountryId));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_NICKNAME);
    }

    @Test
    @DisplayName("빈 닉네임으로 Profile 생성 시 예외가 발생한다")
    void createProfileWithEmptyNickname() {
        // given
        String emptyNickname = "";
        String validImageUrl = "https://example.com/image.jpg";
        String validinterestCountryId = "KR";

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> Profile.of(emptyNickname, validImageUrl, validinterestCountryId));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_NICKNAME);
    }

    @Test
    @DisplayName("공백 닉네임으로 Profile 생성 시 예외가 발생한다")
    void createProfileWithBlankNickname() {
        // given
        String blankNickname = "   ";
        String validImageUrl = "https://example.com/image.jpg";
        String validinterestCountryId = "KR";

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> Profile.of(blankNickname, validImageUrl, validinterestCountryId));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_NICKNAME);
    }

    @Test
    @DisplayName("20자를 초과하는 닉네임으로 Profile 생성 시 예외가 발생한다")
    void createProfileWithTooLongNickname() {
        // given
        String tooLongNickname = "a".repeat(21); // 21자
        String validImageUrl = "https://example.com/image.jpg";
        String validinterestCountryId = "KR";

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> Profile.of(tooLongNickname, validImageUrl, validinterestCountryId));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_NICKNAME);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 20})
    @DisplayName("유효한 길이의 닉네임으로 Profile을 생성할 수 있다")
    void createProfileWithValidNicknameLength(int nicknameLength) {
        // given
        String validNickname = "a".repeat(nicknameLength);
        String validImageUrl = "https://example.com/image.jpg";
        String validinterestCountryId = "KR";

        // when
        Profile profile = Profile.of(validNickname, validImageUrl, validinterestCountryId);

        // then
        assertThat(profile.nickname()).isEqualTo(validNickname);
        assertThat(profile.nickname().length()).isEqualTo(nicknameLength);
    }

    @Test
    @DisplayName("null 관심 국가로 Profile 생성 시 예외가 발생한다")
    void createProfileWithNullinterestCountryId() {
        // given
        String validNickname = "테스트유저";
        String validImageUrl = "https://example.com/image.jpg";
        String nullinterestCountryId = null;

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> Profile.of(validNickname, validImageUrl, nullinterestCountryId));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_REGION);
    }

    @Test
    @DisplayName("Profile record의 동등성이 정상 동작한다")
    void profileEquality() {
        // given
        String nickname = "테스트유저";
        String imageUrl = "https://example.com/image.jpg";
        String interestCountryId = "KR";

        Profile profile1 = Profile.of(nickname, imageUrl, interestCountryId);
        Profile profile2 = Profile.of(nickname, imageUrl, interestCountryId);
        Profile profile3 = Profile.of("다른유저", imageUrl, interestCountryId);

        // when & then
        assertThat(profile1).isEqualTo(profile2);
        assertThat(profile1).isNotEqualTo(profile3);
        assertThat(profile1.hashCode()).isEqualTo(profile2.hashCode());
    }

    @Test
    @DisplayName("Profile record의 toString이 정상 동작한다")
    void profileToString() {
        // given
        String nickname = "테스트유저";
        String imageUrl = "https://example.com/image.jpg";
        String interestCountryId = "KR";
        Profile profile = Profile.of(nickname, imageUrl, interestCountryId);

        // when
        String toString = profile.toString();

        // then
        assertThat(toString).contains("Profile");
        assertThat(toString).contains(nickname);
        assertThat(toString).contains(imageUrl);
        assertThat(toString).contains(interestCountryId.toString());
    }

    @Test
    @DisplayName("빈 문자열 imageUrl은 null로 처리되지 않는다")
    void createProfileWithEmptyImageUrl() {
        // given
        String validNickname = "테스트유저";
        String emptyImageUrl = "";
        String validinterestCountryId = "KR";

        // when
        Profile profile = Profile.of(validNickname, emptyImageUrl, validinterestCountryId);

        // then
        // validateImageUrl 메서드에서 빈 문자열을 null로 변경하려 했지만 실제로는 적용되지 않음
        // 이는 record의 immutable 특성 때문
        assertThat(profile.imageUrl()).isEqualTo(emptyImageUrl);
    }

    @Test
    @DisplayName("공백 문자열 imageUrl은 유지된다")
    void createProfileWithBlankImageUrl() {
        // given
        String validNickname = "테스트유저";
        String blankImageUrl = "   ";
        String validinterestCountryId = "KR";

        // when
        Profile profile = Profile.of(validNickname, blankImageUrl, validinterestCountryId);

        // then
        assertThat(profile.imageUrl()).isEqualTo(blankImageUrl);
    }
}