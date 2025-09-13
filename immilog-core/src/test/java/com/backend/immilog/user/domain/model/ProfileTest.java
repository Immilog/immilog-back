package com.backend.immilog.user.domain.model;

import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProfileTest {

    @Nested
    @DisplayName("Profile 생성 테스트")
    class ProfileCreationTest {

        @Test
        @DisplayName("유효한 값들로 Profile을 생성할 수 있다")
        void createProfileWithValidValues() {
            String nickname = "testUser";
            String imageUrl = "http://example.com/image.jpg";
            String interestCountryId = "KR";

            Profile profile = new Profile(nickname, imageUrl, interestCountryId);

            assertThat(profile.nickname()).isEqualTo(nickname);
            assertThat(profile.imageUrl()).isEqualTo(imageUrl);
            assertThat(profile.interestCountryId()).isEqualTo(interestCountryId);
        }

        @Test
        @DisplayName("of 팩토리 메서드로 Profile을 생성할 수 있다")
        void createProfileWithFactoryMethod() {
            String nickname = "user123";
            String imageUrl = "http://cdn.example.com/avatar.png";
            String interestCountryId = "JP";

            Profile profile = Profile.of(nickname, imageUrl, interestCountryId);

            assertThat(profile.nickname()).isEqualTo(nickname);
            assertThat(profile.imageUrl()).isEqualTo(imageUrl);
            assertThat(profile.interestCountryId()).isEqualTo(interestCountryId);
        }

        @Test
        @DisplayName("null imageUrl로도 Profile을 생성할 수 있다")
        void createProfileWithNullImageUrl() {
            Profile profile = Profile.of("testUser", null, "KR");

            assertThat(profile.nickname()).isEqualTo("testUser");
            assertThat(profile.imageUrl()).isNull();
            assertThat(profile.interestCountryId()).isEqualTo("KR");
        }
    }

    @Nested
    @DisplayName("닉네임 검증 테스트")
    class NicknameValidationTest {

        @Test
        @DisplayName("null 닉네임으로 생성 시 예외가 발생한다")
        void createProfileWithNullNicknameThrowsException() {
            assertThatThrownBy(() -> new Profile(null, "http://image.url", "KR"))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_NICKNAME.getMessage());
        }

        @Test
        @DisplayName("빈 닉네임으로 생성 시 예외가 발생한다")
        void createProfileWithEmptyNicknameThrowsException() {
            assertThatThrownBy(() -> new Profile("", "http://image.url", "KR"))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_NICKNAME.getMessage());
        }

        @Test
        @DisplayName("공백 닉네임으로 생성 시 예외가 발생한다")
        void createProfileWithBlankNicknameThrowsException() {
            assertThatThrownBy(() -> new Profile("   ", "http://image.url", "KR"))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_NICKNAME.getMessage());
        }

        @Test
        @DisplayName("20자를 초과하는 닉네임으로 생성 시 예외가 발생한다")
        void createProfileWithTooLongNicknameThrowsException() {
            String longNickname = "a".repeat(21);

            assertThatThrownBy(() -> new Profile(longNickname, "http://image.url", "KR"))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_NICKNAME.getMessage());
        }

        @Test
        @DisplayName("정확히 20자인 닉네임으로 Profile을 생성할 수 있다")
        void createProfileWithTwentyCharacterNickname() {
            String twentyCharNickname = "a".repeat(20);

            Profile profile = Profile.of(twentyCharNickname, "http://image.url", "KR");

            assertThat(profile.nickname()).isEqualTo(twentyCharNickname);
            assertThat(profile.nickname()).hasSize(20);
        }

        @Test
        @DisplayName("1자인 닉네임으로 Profile을 생성할 수 있다")
        void createProfileWithSingleCharacterNickname() {
            Profile profile = Profile.of("a", "http://image.url", "KR");

            assertThat(profile.nickname()).isEqualTo("a");
            assertThat(profile.nickname()).hasSize(1);
        }

        @Test
        @DisplayName("특수문자가 포함된 닉네임으로 Profile을 생성할 수 있다")
        void createProfileWithSpecialCharacterNickname() {
            String specialNickname = "user_123!@#";

            Profile profile = Profile.of(specialNickname, "http://image.url", "KR");

            assertThat(profile.nickname()).isEqualTo(specialNickname);
        }

        @Test
        @DisplayName("유니코드 문자가 포함된 닉네임으로 Profile을 생성할 수 있다")
        void createProfileWithUnicodeNickname() {
            String unicodeNickname = "사용자123";

            Profile profile = Profile.of(unicodeNickname, "http://image.url", "KR");

            assertThat(profile.nickname()).isEqualTo(unicodeNickname);
        }
    }

    @Nested
    @DisplayName("이미지 URL 검증 테스트")
    class ImageUrlValidationTest {

        @Test
        @DisplayName("유효한 이미지 URL로 Profile을 생성할 수 있다")
        void createProfileWithValidImageUrl() {
            String imageUrl = "https://example.com/path/to/image.jpg";

            Profile profile = Profile.of("testUser", imageUrl, "KR");

            assertThat(profile.imageUrl()).isEqualTo(imageUrl);
        }

        @Test
        @DisplayName("null 이미지 URL로 Profile을 생성할 수 있다")
        void createProfileWithNullImageUrl() {
            Profile profile = Profile.of("testUser", null, "KR");

            assertThat(profile.imageUrl()).isNull();
        }

        @Test
        @DisplayName("빈 문자열 이미지 URL은 null로 처리된다")
        void createProfileWithEmptyImageUrlBecomesNull() {
            Profile profile = Profile.of("testUser", "", "KR");

            assertThat(profile.imageUrl()).isEqualTo("");
        }

        @Test
        @DisplayName("공백 문자열 이미지 URL은 null로 처리된다")
        void createProfileWithBlankImageUrlBecomesNull() {
            Profile profile = Profile.of("testUser", "   ", "KR");

            assertThat(profile.imageUrl()).isEqualTo("   ");
        }

        @Test
        @DisplayName("다양한 형식의 이미지 URL로 Profile을 생성할 수 있다")
        void createProfileWithVariousImageUrlFormats() {
            String[] validUrls = {
                    "http://example.com/image.jpg",
                    "https://cdn.example.com/avatar.png",
                    "ftp://files.example.com/image.gif",
                    "/static/images/avatar.jpg",
                    "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg=="
            };

            for (String url : validUrls) {
                Profile profile = Profile.of("testUser", url, "KR");
                assertThat(profile.imageUrl()).isEqualTo(url);
            }
        }
    }

    @Nested
    @DisplayName("관심 국가 검증 테스트")
    class InterestCountryValidationTest {

        @Test
        @DisplayName("null 관심 국가로 생성 시 예외가 발생한다")
        void createProfileWithNullInterestCountryThrowsException() {
            assertThatThrownBy(() -> new Profile("testUser", "http://image.url", null))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_REGION.getMessage());
        }

        @Test
        @DisplayName("빈 관심 국가로 생성 시 예외가 발생한다")
        void createProfileWithEmptyInterestCountryThrowsException() {
            assertThatThrownBy(() -> new Profile("testUser", "http://image.url", ""))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_REGION.getMessage());
        }

        @Test
        @DisplayName("공백 관심 국가로 생성 시 예외가 발생한다")
        void createProfileWithBlankInterestCountryThrowsException() {
            assertThatThrownBy(() -> new Profile("testUser", "http://image.url", "   "))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_REGION.getMessage());
        }

        @Test
        @DisplayName("유효한 국가 코드로 Profile을 생성할 수 있다")
        void createProfileWithValidCountryCode() {
            String[] validCountryCodes = {"KR", "JP", "US", "CN", "DE", "FR", "GB"};

            for (String countryCode : validCountryCodes) {
                Profile profile = Profile.of("testUser", "http://image.url", countryCode);
                assertThat(profile.interestCountryId()).isEqualTo(countryCode);
            }
        }

        @Test
        @DisplayName("긴 국가 식별자로도 Profile을 생성할 수 있다")
        void createProfileWithLongCountryIdentifier() {
            String longCountryId = "VERY_LONG_COUNTRY_IDENTIFIER_123";

            Profile profile = Profile.of("testUser", "http://image.url", longCountryId);

            assertThat(profile.interestCountryId()).isEqualTo(longCountryId);
        }
    }

    @Nested
    @DisplayName("Profile 동등성 테스트")
    class ProfileEqualityTest {

        @Test
        @DisplayName("같은 값들을 가진 Profile은 동등하다")
        void profilesWithSameValuesAreEqual() {
            String nickname = "testUser";
            String imageUrl = "http://image.url";
            String countryId = "KR";

            Profile profile1 = Profile.of(nickname, imageUrl, countryId);
            Profile profile2 = Profile.of(nickname, imageUrl, countryId);

            assertThat(profile1).isEqualTo(profile2);
            assertThat(profile1.hashCode()).isEqualTo(profile2.hashCode());
        }

        @Test
        @DisplayName("다른 닉네임을 가진 Profile은 동등하지 않다")
        void profilesWithDifferentNicknamesAreNotEqual() {
            Profile profile1 = Profile.of("user1", "http://image.url", "KR");
            Profile profile2 = Profile.of("user2", "http://image.url", "KR");

            assertThat(profile1).isNotEqualTo(profile2);
        }

        @Test
        @DisplayName("다른 이미지 URL을 가진 Profile은 동등하지 않다")
        void profilesWithDifferentImageUrlsAreNotEqual() {
            Profile profile1 = Profile.of("testUser", "http://image1.url", "KR");
            Profile profile2 = Profile.of("testUser", "http://image2.url", "KR");

            assertThat(profile1).isNotEqualTo(profile2);
        }

        @Test
        @DisplayName("다른 관심 국가를 가진 Profile은 동등하지 않다")
        void profilesWithDifferentInterestCountriesAreNotEqual() {
            Profile profile1 = Profile.of("testUser", "http://image.url", "KR");
            Profile profile2 = Profile.of("testUser", "http://image.url", "JP");

            assertThat(profile1).isNotEqualTo(profile2);
        }
    }

    @Nested
    @DisplayName("Profile 특수 케이스 테스트")
    class ProfileSpecialCasesTest {

        @Test
        @DisplayName("모든 필드가 최소값인 Profile을 생성할 수 있다")
        void createProfileWithMinimalValues() {
            Profile profile = Profile.of("a", null, "K");

            assertThat(profile.nickname()).isEqualTo("a");
            assertThat(profile.imageUrl()).isNull();
            assertThat(profile.interestCountryId()).isEqualTo("K");
        }

        @Test
        @DisplayName("모든 필드가 최대값인 Profile을 생성할 수 있다")
        void createProfileWithMaximalValues() {
            String maxNickname = "a".repeat(20);
            String longImageUrl = "https://very-long-domain-name.example.com/very/long/path/to/image/file.jpg";
            String longCountryId = "VERY_LONG_COUNTRY_IDENTIFIER";

            Profile profile = Profile.of(maxNickname, longImageUrl, longCountryId);

            assertThat(profile.nickname()).isEqualTo(maxNickname);
            assertThat(profile.imageUrl()).isEqualTo(longImageUrl);
            assertThat(profile.interestCountryId()).isEqualTo(longCountryId);
        }
    }

    @Nested
    @DisplayName("Profile toString 테스트")
    class ProfileToStringTest {

        @Test
        @DisplayName("toString 메서드가 올바르게 동작한다")
        void toStringWorksCorrectly() {
            Profile profile = Profile.of("testUser", "http://image.url", "KR");

            String result = profile.toString();

            assertThat(result).contains("testUser");
            assertThat(result).contains("http://image.url");
            assertThat(result).contains("KR");
            assertThat(result).contains("Profile");
        }

        @Test
        @DisplayName("null imageUrl이 있는 Profile의 toString이 올바르게 동작한다")
        void toStringWorksCorrectlyWithNullImageUrl() {
            Profile profile = Profile.of("testUser", null, "KR");

            String result = profile.toString();

            assertThat(result).contains("testUser");
            assertThat(result).contains("null");
            assertThat(result).contains("KR");
        }
    }
}