package com.backend.immilog.user.application.result;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserNickNameResult 테스트")
class UserNickNameResultTest {

    @Test
    @DisplayName("정상적인 값들로 UserNickNameResult를 생성할 수 있다")
    void createUserNickNameResult() {
        // given
        String userId = "user123";
        String nickName = "테스트유저";

        // when
        UserNickNameResult result = new UserNickNameResult(userId, nickName);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.nickName()).isEqualTo(nickName);
    }

    @Test
    @DisplayName("null 값들로도 UserNickNameResult를 생성할 수 있다")
    void createUserNickNameResultWithNullValues() {
        // given & when
        UserNickNameResult result = new UserNickNameResult(null, null);

        // then
        assertThat(result.userId()).isNull();
        assertThat(result.nickName()).isNull();
    }

    @Test
    @DisplayName("빈 문자열로도 UserNickNameResult를 생성할 수 있다")
    void createUserNickNameResultWithEmptyStrings() {
        // given & when
        UserNickNameResult result = new UserNickNameResult("", "");

        // then
        assertThat(result.userId()).isEmpty();
        assertThat(result.nickName()).isEmpty();
    }

    @Test
    @DisplayName("다양한 닉네임으로 UserNickNameResult를 생성할 수 있다")
    void createUserNickNameResultWithVariousNicknames() {
        // given & when
        UserNickNameResult koreanResult = new UserNickNameResult("user1", "한글닉네임");
        UserNickNameResult englishResult = new UserNickNameResult("user2", "EnglishNickname");
        UserNickNameResult mixedResult = new UserNickNameResult("user3", "Mixed닉네임123");
        UserNickNameResult specialResult = new UserNickNameResult("user4", "닉네임!@#");

        // then
        assertThat(koreanResult.nickName()).isEqualTo("한글닉네임");
        assertThat(englishResult.nickName()).isEqualTo("EnglishNickname");
        assertThat(mixedResult.nickName()).isEqualTo("Mixed닉네임123");
        assertThat(specialResult.nickName()).isEqualTo("닉네임!@#");
    }

    @Test
    @DisplayName("UserNickNameResult record의 동등성이 정상 작동한다")
    void userNickNameResultEquality() {
        // given
        UserNickNameResult result1 = new UserNickNameResult("user123", "테스트유저");
        UserNickNameResult result2 = new UserNickNameResult("user123", "테스트유저");
        UserNickNameResult result3 = new UserNickNameResult("user456", "테스트유저");
        UserNickNameResult result4 = new UserNickNameResult("user123", "다른유저");

        // when & then
        assertThat(result1).isEqualTo(result2);
        assertThat(result1).isNotEqualTo(result3);
        assertThat(result1).isNotEqualTo(result4);
        assertThat(result1.hashCode()).isEqualTo(result2.hashCode());
    }

    @Test
    @DisplayName("UserNickNameResult record의 toString이 정상 작동한다")
    void userNickNameResultToString() {
        // given
        UserNickNameResult result = new UserNickNameResult("user123", "테스트유저");

        // when
        String toString = result.toString();

        // then
        assertThat(toString).contains("UserNickNameResult");
        assertThat(toString).contains("user123");
        assertThat(toString).contains("테스트유저");
    }

    @Test
    @DisplayName("긴 사용자 ID로 UserNickNameResult를 생성할 수 있다")
    void createUserNickNameResultWithLongUserId() {
        // given
        String longUserId = "very-long-user-id-with-many-characters-1234567890";
        String nickname = "닉네임";

        // when
        UserNickNameResult result = new UserNickNameResult(longUserId, nickname);

        // then
        assertThat(result.userId()).isEqualTo(longUserId);
        assertThat(result.nickName()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("공백이 포함된 닉네임으로 UserNickNameResult를 생성할 수 있다")
    void createUserNickNameResultWithSpacesInNickname() {
        // given
        String userId = "user123";
        String nickname = "닉네임 with spaces";

        // when
        UserNickNameResult result = new UserNickNameResult(userId, nickname);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.nickName()).isEqualTo(nickname);
    }
}