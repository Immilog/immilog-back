package com.backend.immilog.user.application.result;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("userNicknameResult 테스트")
class userNicknameResultTest {

    @Test
    @DisplayName("정상적인 값들로 userNicknameResult를 생성할 수 있다")
    void createuserNicknameResult() {
        // given
        String userId = "user123";
        String nickName = "테스트유저";

        // when
        userNicknameResult result = new userNicknameResult(userId, nickName);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.nickName()).isEqualTo(nickName);
    }

    @Test
    @DisplayName("null 값들로도 userNicknameResult를 생성할 수 있다")
    void createuserNicknameResultWithNullValues() {
        // given & when
        userNicknameResult result = new userNicknameResult(null, null);

        // then
        assertThat(result.userId()).isNull();
        assertThat(result.nickName()).isNull();
    }

    @Test
    @DisplayName("빈 문자열로도 userNicknameResult를 생성할 수 있다")
    void createuserNicknameResultWithEmptyStrings() {
        // given & when
        userNicknameResult result = new userNicknameResult("", "");

        // then
        assertThat(result.userId()).isEmpty();
        assertThat(result.nickName()).isEmpty();
    }

    @Test
    @DisplayName("다양한 닉네임으로 userNicknameResult를 생성할 수 있다")
    void createuserNicknameResultWithVariousNicknames() {
        // given & when
        userNicknameResult koreanResult = new userNicknameResult("user1", "한글닉네임");
        userNicknameResult englishResult = new userNicknameResult("user2", "EnglishNickname");
        userNicknameResult mixedResult = new userNicknameResult("user3", "Mixed닉네임123");
        userNicknameResult specialResult = new userNicknameResult("user4", "닉네임!@#");

        // then
        assertThat(koreanResult.nickName()).isEqualTo("한글닉네임");
        assertThat(englishResult.nickName()).isEqualTo("EnglishNickname");
        assertThat(mixedResult.nickName()).isEqualTo("Mixed닉네임123");
        assertThat(specialResult.nickName()).isEqualTo("닉네임!@#");
    }

    @Test
    @DisplayName("userNicknameResult record의 동등성이 정상 작동한다")
    void userNicknameResultEquality() {
        // given
        userNicknameResult result1 = new userNicknameResult("user123", "테스트유저");
        userNicknameResult result2 = new userNicknameResult("user123", "테스트유저");
        userNicknameResult result3 = new userNicknameResult("user456", "테스트유저");
        userNicknameResult result4 = new userNicknameResult("user123", "다른유저");

        // when & then
        assertThat(result1).isEqualTo(result2);
        assertThat(result1).isNotEqualTo(result3);
        assertThat(result1).isNotEqualTo(result4);
        assertThat(result1.hashCode()).isEqualTo(result2.hashCode());
    }

    @Test
    @DisplayName("userNicknameResult record의 toString이 정상 작동한다")
    void userNicknameResultToString() {
        // given
        userNicknameResult result = new userNicknameResult("user123", "테스트유저");

        // when
        String toString = result.toString();

        // then
        assertThat(toString).contains("userNicknameResult");
        assertThat(toString).contains("user123");
        assertThat(toString).contains("테스트유저");
    }

    @Test
    @DisplayName("긴 사용자 ID로 userNicknameResult를 생성할 수 있다")
    void createuserNicknameResultWithLongUserId() {
        // given
        String longUserId = "very-long-user-id-with-many-characters-1234567890";
        String nickname = "닉네임";

        // when
        userNicknameResult result = new userNicknameResult(longUserId, nickname);

        // then
        assertThat(result.userId()).isEqualTo(longUserId);
        assertThat(result.nickName()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("공백이 포함된 닉네임으로 userNicknameResult를 생성할 수 있다")
    void createuserNicknameResultWithSpacesInNickname() {
        // given
        String userId = "user123";
        String nickname = "닉네임 with spaces";

        // when
        userNicknameResult result = new userNicknameResult(userId, nickname);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.nickName()).isEqualTo(nickname);
    }
}