package com.backend.immilog.user.application.result;

import com.backend.immilog.user.domain.enums.UserRole;
import com.backend.immilog.user.domain.enums.UserStatus;
import com.backend.immilog.user.domain.model.*;
import com.backend.immilog.user.presentation.payload.UserSignInPayload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserSignInResult 테스트")
class UserSignInResultTest {

    private User createMockUser() {
        return User.restore(
                UserId.of("user123"),
                Auth.of("test@example.com", "encodedPassword"),
                UserRole.ROLE_USER,
                Profile.of("테스트유저", "https://example.com/image.jpg", "KR"),
                Location.of("KR", "서울특별시"),
                UserStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("정상적인 값들로 UserSignInResult를 생성할 수 있다")
    void createUserSignInResult() {
        // given
        String userId = "user123";
        String email = "test@example.com";
        String nickname = "테스트유저";
        String accessToken = "accessToken123";
        String refreshToken = "refreshToken123";
        String country = "KR";
        String interestCountry = "JP";
        String region = "서울특별시";
        String userProfileUrl = "https://example.com/image.jpg";
        Boolean isLocationMatch = true;

        // when
        UserSignInResult result = new UserSignInResult(
                userId, email, nickname, accessToken, refreshToken,
                country, interestCountry, region, userProfileUrl, isLocationMatch
        );

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.email()).isEqualTo(email);
        assertThat(result.nickname()).isEqualTo(nickname);
        assertThat(result.accessToken()).isEqualTo(accessToken);
        assertThat(result.refreshToken()).isEqualTo(refreshToken);
        assertThat(result.country()).isEqualTo(country);
        assertThat(result.interestCountry()).isEqualTo(interestCountry);
        assertThat(result.region()).isEqualTo(region);
        assertThat(result.userProfileUrl()).isEqualTo(userProfileUrl);
        assertThat(result.isLocationMatch()).isEqualTo(isLocationMatch);
    }

    @Test
    @DisplayName("User 도메인 객체로부터 UserSignInResult를 생성할 수 있다")
    void createUserSignInResultFromUser() {
        // given
        User user = createMockUser();
        String accessToken = "accessToken123";
        String refreshToken = "refreshToken123";
        boolean isLocationMatch = true;

        // when
        UserSignInResult result = UserSignInResult.of(user, accessToken, refreshToken, isLocationMatch);

        // then
        assertThat(result.userId()).isEqualTo("user123");
        assertThat(result.email()).isEqualTo("test@example.com");
        assertThat(result.nickname()).isEqualTo("테스트유저");
        assertThat(result.accessToken()).isEqualTo("accessToken123");
        assertThat(result.refreshToken()).isEqualTo("refreshToken123");
        assertThat(result.country()).isEqualTo("KR");
        assertThat(result.interestCountry()).isEqualTo("KR"); // 코드에서 interestCountry가 country로 설정됨
        assertThat(result.region()).isEqualTo("서울특별시");
        assertThat(result.userProfileUrl()).isEqualTo("https://example.com/image.jpg");
        assertThat(result.isLocationMatch()).isTrue();
    }

    @Test
    @DisplayName("null 토큰으로 UserSignInResult를 생성하면 빈 문자열로 처리된다")
    void createUserSignInResultWithNullTokens() {
        // given
        User user = createMockUser();
        String accessToken = null;
        String refreshToken = null;
        boolean isLocationMatch = false;

        // when
        UserSignInResult result = UserSignInResult.of(user, accessToken, refreshToken, isLocationMatch);

        // then
        assertThat(result.accessToken()).isEqualTo("");
        assertThat(result.refreshToken()).isEqualTo("");
        assertThat(result.isLocationMatch()).isFalse();
    }

    @Test
    @DisplayName("프로필 이미지가 null인 사용자로 UserSignInResult를 생성할 수 있다")
    void createUserSignInResultWithNullProfileImage() {
        // given
        User user = User.restore(
                UserId.of("user123"),
                Auth.of("test@example.com", "encodedPassword"),
                UserRole.ROLE_USER,
                Profile.of("테스트유저", null, "KR"), // null 이미지
                Location.of("KR", "서울특별시"),
                UserStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );

        // when
        UserSignInResult result = UserSignInResult.of(user, "token", "refresh", true);

        // then
        assertThat(result.userProfileUrl()).isNull();
    }

    @Test
    @DisplayName("관심 국가가 null인 경우를 처리한다")
    void createUserSignInResultWithNullInterestCountry() {
        // given
        User user = User.restore(
                UserId.of("user123"),
                Auth.of("test@example.com", "encodedPassword"),
                UserRole.ROLE_USER,
                Profile.of("테스트유저", "image.jpg", "KR"),
                Location.of("KR", "서울특별시"),
                UserStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );

        // when
        UserSignInResult result = UserSignInResult.of(user, "token", "refresh", true);

        // then
        assertThat(result.interestCountry()).isEqualTo("KR");
    }

    @Test
    @DisplayName("UserSignInResult를 UserSignInResponse로 변환할 수 있다")
    void convertToResponse() {
        // given
        UserSignInResult result = new UserSignInResult(
                "user123", "test@example.com", "테스트유저",
                "accessToken", "refreshToken", "KR", "JP",
                "서울특별시", "image.jpg", true
        );

        // when
        UserSignInPayload.UserSignInResponse response = UserSignInPayload.UserSignInResponse.success(result.toInfraDTO());

        // then
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(200);
        assertThat(response.message()).isEqualTo("success");
        assertThat(response.data().accessToken()).isEqualTo(result.toInfraDTO().accessToken());
    }

    @Test
    @DisplayName("다양한 국가의 사용자로 UserSignInResult를 생성할 수 있다")
    void createUserSignInResultWithDifferentCountries() {
        // given
        User japanUser = User.restore(
                UserId.of("japanUser123"),
                Auth.of("japan@example.com", "encodedPassword"),
                UserRole.ROLE_USER,
                Profile.of("일본유저", null, "JP"),
                Location.of("JP", "도쿄"),
                UserStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );

        // when
        UserSignInResult result = UserSignInResult.of(japanUser, "token", "refresh", false);

        // then
        assertThat(result.userId()).isEqualTo("japanUser123");
        assertThat(result.country()).isEqualTo("JP");
        assertThat(result.region()).isEqualTo("도쿄");
        assertThat(result.isLocationMatch()).isFalse();
    }

    @Test
    @DisplayName("UserSignInResult record의 동등성이 정상 작동한다")
    void userSignInResultEquality() {
        // given
        UserSignInResult result1 = new UserSignInResult(
                "user123", "test@example.com", "테스트유저",
                "token", "refresh", "KR", "JP",
                "서울", "image.jpg", true
        );
        UserSignInResult result2 = new UserSignInResult(
                "user123", "test@example.com", "테스트유저",
                "token", "refresh", "KR", "JP",
                "서울", "image.jpg", true
        );
        UserSignInResult result3 = new UserSignInResult(
                "user456", "test@example.com", "테스트유저",
                "token", "refresh", "KR", "JP",
                "서울", "image.jpg", true
        );

        // when & then
        assertThat(result1).isEqualTo(result2);
        assertThat(result1).isNotEqualTo(result3);
        assertThat(result1.hashCode()).isEqualTo(result2.hashCode());
    }

    @Test
    @DisplayName("UserSignInResult record의 toString이 정상 작동한다")
    void userSignInResultToString() {
        // given
        UserSignInResult result = new UserSignInResult(
                "user123", "test@example.com", "테스트유저",
                "accessToken", "refreshToken", "KR", "JP",
                "서울특별시", "image.jpg", true
        );

        // when
        String toString = result.toString();

        // then
        assertThat(toString).contains("UserSignInResult");
        assertThat(toString).contains("user123");
        assertThat(toString).contains("test@example.com");
        assertThat(toString).contains("테스트유저");
        assertThat(toString).contains("KR");
    }
}