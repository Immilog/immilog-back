package com.backend.immilog.user.application.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserPasswordChangeCommand 테스트")
class UserPasswordChangeCommandTest {

    @Test
    @DisplayName("정상적인 값들로 UserPasswordChangeCommand를 생성할 수 있다")
    void createUserPasswordChangeCommand() {
        // given
        String existingPassword = "oldPassword123";
        String newPassword = "newPassword456";

        // when
        UserPasswordChangeCommand command = new UserPasswordChangeCommand(existingPassword, newPassword);

        // then
        assertThat(command.existingPassword()).isEqualTo(existingPassword);
        assertThat(command.newPassword()).isEqualTo(newPassword);
    }

    @Test
    @DisplayName("다양한 비밀번호 패턴으로 UserPasswordChangeCommand를 생성할 수 있다")
    void createUserPasswordChangeCommandWithVariousPatterns() {
        // given & when
        UserPasswordChangeCommand simpleCommand = new UserPasswordChangeCommand("simple123", "newSimple456");
        UserPasswordChangeCommand complexCommand = new UserPasswordChangeCommand(
                "Complex!@#$123",
                "NewComplex!@#$456"
        );
        UserPasswordChangeCommand longCommand = new UserPasswordChangeCommand(
                "veryLongOldPasswordWithManyCharacters123",
                "veryLongNewPasswordWithManyCharacters456"
        );

        // then
        assertThat(simpleCommand.existingPassword()).isEqualTo("simple123");
        assertThat(simpleCommand.newPassword()).isEqualTo("newSimple456");

        assertThat(complexCommand.existingPassword()).isEqualTo("Complex!@#$123");
        assertThat(complexCommand.newPassword()).isEqualTo("NewComplex!@#$456");

        assertThat(longCommand.existingPassword()).isEqualTo("veryLongOldPasswordWithManyCharacters123");
        assertThat(longCommand.newPassword()).isEqualTo("veryLongNewPasswordWithManyCharacters456");
    }

    @Test
    @DisplayName("특수문자가 포함된 비밀번호로 UserPasswordChangeCommand를 생성할 수 있다")
    void createUserPasswordChangeCommandWithSpecialCharacters() {
        // given & when
        UserPasswordChangeCommand command = new UserPasswordChangeCommand(
                "Old!@#$%^&*()_+-=[]{}|;':\",./<>?",
                "New!@#$%^&*()_+-=[]{}|;':\",./<>?"
        );

        // then
        assertThat(command.existingPassword()).isEqualTo("Old!@#$%^&*()_+-=[]{}|;':\",./<>?");
        assertThat(command.newPassword()).isEqualTo("New!@#$%^&*()_+-=[]{}|;':\",./<>?");
    }

    @Test
    @DisplayName("숫자만 포함된 비밀번호로 UserPasswordChangeCommand를 생성할 수 있다")
    void createUserPasswordChangeCommandWithNumericPasswords() {
        // given & when
        UserPasswordChangeCommand command = new UserPasswordChangeCommand("123456789", "987654321");

        // then
        assertThat(command.existingPassword()).isEqualTo("123456789");
        assertThat(command.newPassword()).isEqualTo("987654321");
    }

    @Test
    @DisplayName("문자만 포함된 비밀번호로 UserPasswordChangeCommand를 생성할 수 있다")
    void createUserPasswordChangeCommandWithAlphabeticPasswords() {
        // given & when
        UserPasswordChangeCommand upperCaseCommand = new UserPasswordChangeCommand("OLDPASSWORD", "NEWPASSWORD");
        UserPasswordChangeCommand lowerCaseCommand = new UserPasswordChangeCommand("oldpassword", "newpassword");
        UserPasswordChangeCommand mixedCaseCommand = new UserPasswordChangeCommand("OldPassword", "NewPassword");

        // then
        assertThat(upperCaseCommand.existingPassword()).isEqualTo("OLDPASSWORD");
        assertThat(upperCaseCommand.newPassword()).isEqualTo("NEWPASSWORD");

        assertThat(lowerCaseCommand.existingPassword()).isEqualTo("oldpassword");
        assertThat(lowerCaseCommand.newPassword()).isEqualTo("newpassword");

        assertThat(mixedCaseCommand.existingPassword()).isEqualTo("OldPassword");
        assertThat(mixedCaseCommand.newPassword()).isEqualTo("NewPassword");
    }

    @Test
    @DisplayName("공백이 포함된 비밀번호로 UserPasswordChangeCommand를 생성할 수 있다")
    void createUserPasswordChangeCommandWithSpaces() {
        // given & when
        UserPasswordChangeCommand command = new UserPasswordChangeCommand(
                "old password with spaces",
                "new password with spaces"
        );

        // then
        assertThat(command.existingPassword()).isEqualTo("old password with spaces");
        assertThat(command.newPassword()).isEqualTo("new password with spaces");
    }

    @Test
    @DisplayName("매우 짧은 비밀번호로 UserPasswordChangeCommand를 생성할 수 있다")
    void createUserPasswordChangeCommandWithShortPasswords() {
        // given & when
        UserPasswordChangeCommand command = new UserPasswordChangeCommand("a", "b");

        // then
        assertThat(command.existingPassword()).isEqualTo("a");
        assertThat(command.newPassword()).isEqualTo("b");
    }

    @Test
    @DisplayName("빈 문자열 비밀번호로 UserPasswordChangeCommand를 생성할 수 있다")
    void createUserPasswordChangeCommandWithEmptyPasswords() {
        // given & when
        UserPasswordChangeCommand command = new UserPasswordChangeCommand("", "");

        // then
        assertThat(command.existingPassword()).isEqualTo("");
        assertThat(command.newPassword()).isEqualTo("");
    }

    @Test
    @DisplayName("null 비밀번호로 UserPasswordChangeCommand를 생성할 수 있다")
    void createUserPasswordChangeCommandWithNullPasswords() {
        // given & when
        UserPasswordChangeCommand command = new UserPasswordChangeCommand(null, null);

        // then
        assertThat(command.existingPassword()).isNull();
        assertThat(command.newPassword()).isNull();
    }

    @Test
    @DisplayName("기존 비밀번호와 새 비밀번호가 같은 경우")
    void createUserPasswordChangeCommandWithSamePasswords() {
        // given & when
        UserPasswordChangeCommand command = new UserPasswordChangeCommand("samePassword123", "samePassword123");

        // then
        assertThat(command.existingPassword()).isEqualTo("samePassword123");
        assertThat(command.newPassword()).isEqualTo("samePassword123");
        assertThat(command.existingPassword()).isEqualTo(command.newPassword());
    }

    @Test
    @DisplayName("유니코드 문자가 포함된 비밀번호로 UserPasswordChangeCommand를 생성할 수 있다")
    void createUserPasswordChangeCommandWithUnicodePasswords() {
        // given & when
        UserPasswordChangeCommand koreanCommand = new UserPasswordChangeCommand("기존비밀번호123", "새비밀번호456");
        UserPasswordChangeCommand japaneseCommand = new UserPasswordChangeCommand("古いパスワード123", "新しいパスワード456");
        UserPasswordChangeCommand emojiCommand = new UserPasswordChangeCommand("password😀🔐", "newpass🔑✨");

        // then
        assertThat(koreanCommand.existingPassword()).isEqualTo("기존비밀번호123");
        assertThat(koreanCommand.newPassword()).isEqualTo("새비밀번호456");

        assertThat(japaneseCommand.existingPassword()).isEqualTo("古いパスワード123");
        assertThat(japaneseCommand.newPassword()).isEqualTo("新しいパスワード456");

        assertThat(emojiCommand.existingPassword()).isEqualTo("password😀🔐");
        assertThat(emojiCommand.newPassword()).isEqualTo("newpass🔑✨");
    }

    @Test
    @DisplayName("UserPasswordChangeCommand record의 동등성이 정상 작동한다")
    void userPasswordChangeCommandEquality() {
        // given
        UserPasswordChangeCommand command1 = new UserPasswordChangeCommand("oldPass123", "newPass456");
        UserPasswordChangeCommand command2 = new UserPasswordChangeCommand("oldPass123", "newPass456");
        UserPasswordChangeCommand command3 = new UserPasswordChangeCommand("differentOld", "newPass456");

        // when & then
        assertThat(command1).isEqualTo(command2);
        assertThat(command1).isNotEqualTo(command3);
        assertThat(command1.hashCode()).isEqualTo(command2.hashCode());
    }

    @Test
    @DisplayName("UserPasswordChangeCommand record의 toString이 정상 작동한다")
    void userPasswordChangeCommandToString() {
        // given
        UserPasswordChangeCommand command = new UserPasswordChangeCommand("existingPass123", "newPass456");

        // when
        String toString = command.toString();

        // then
        assertThat(toString).contains("UserPasswordChangeCommand");
        assertThat(toString).contains("existingPass123");
        assertThat(toString).contains("newPass456");
    }

    @Test
    @DisplayName("다양한 길이의 비밀번호로 UserPasswordChangeCommand를 생성할 수 있다")
    void createUserPasswordChangeCommandWithVariousLengths() {
        // given & when
        UserPasswordChangeCommand veryShortCommand = new UserPasswordChangeCommand("12", "34");
        UserPasswordChangeCommand shortCommand = new UserPasswordChangeCommand("short123", "newShort456");
        UserPasswordChangeCommand mediumCommand = new UserPasswordChangeCommand("mediumLengthPassword123", "newMediumLengthPassword456");
        UserPasswordChangeCommand longCommand = new UserPasswordChangeCommand(
                "thisIsAVeryLongPasswordThatExceedsNormalLengthLimits123456789",
                "thisIsAnotherVeryLongPasswordForTestingPurposesOnly987654321"
        );

        // then
        assertThat(veryShortCommand.existingPassword()).hasSize(2);
        assertThat(veryShortCommand.newPassword()).hasSize(2);

        assertThat(shortCommand.existingPassword()).hasSize(8);
        assertThat(shortCommand.newPassword()).hasSize(11);

        assertThat(mediumCommand.existingPassword()).hasSize(23);
        assertThat(mediumCommand.newPassword()).hasSize(26);

        assertThat(longCommand.existingPassword()).hasSize(61);
        assertThat(longCommand.newPassword()).hasSize(60);
    }

    @Test
    @DisplayName("escape 문자가 포함된 비밀번호로 UserPasswordChangeCommand를 생성할 수 있다")
    void createUserPasswordChangeCommandWithEscapeCharacters() {
        // given & when
        UserPasswordChangeCommand command = new UserPasswordChangeCommand(
                "old\\n\\t\\r\\\"password",
                "new\\n\\t\\r\\\"password"
        );

        // then
        assertThat(command.existingPassword()).isEqualTo("old\\n\\t\\r\\\"password");
        assertThat(command.newPassword()).isEqualTo("new\\n\\t\\r\\\"password");
    }

    @Test
    @DisplayName("실제 개행문자가 포함된 비밀번호로 UserPasswordChangeCommand를 생성할 수 있다")
    void createUserPasswordChangeCommandWithActualNewlines() {
        // given & when
        UserPasswordChangeCommand command = new UserPasswordChangeCommand(
                "old\npassword\nwith\nnewlines",
                "new\npassword\nwith\nnewlines"
        );

        // then
        assertThat(command.existingPassword()).isEqualTo("old\npassword\nwith\nnewlines");
        assertThat(command.newPassword()).isEqualTo("new\npassword\nwith\nnewlines");
    }

    @Test
    @DisplayName("탭 문자가 포함된 비밀번호로 UserPasswordChangeCommand를 생성할 수 있다")
    void createUserPasswordChangeCommandWithTabs() {
        // given & when
        UserPasswordChangeCommand command = new UserPasswordChangeCommand(
                "old\tpassword\twith\ttabs",
                "new\tpassword\twith\ttabs"
        );

        // then
        assertThat(command.existingPassword()).isEqualTo("old\tpassword\twith\ttabs");
        assertThat(command.newPassword()).isEqualTo("new\tpassword\twith\ttabs");
    }

    @Test
    @DisplayName("Base64 인코딩된 문자열과 같은 비밀번호로 UserPasswordChangeCommand를 생성할 수 있다")
    void createUserPasswordChangeCommandWithBase64LikePasswords() {
        // given & when
        UserPasswordChangeCommand command = new UserPasswordChangeCommand(
                "SGVsbG8gV29ybGQ=",
                "Tm93IGlzIHRoZSB0aW1l"
        );

        // then
        assertThat(command.existingPassword()).isEqualTo("SGVsbG8gV29ybGQ=");
        assertThat(command.newPassword()).isEqualTo("Tm93IGlzIHRoZSB0aW1l");
    }

    @Test
    @DisplayName("연속된 같은 문자로 구성된 비밀번호로 UserPasswordChangeCommand를 생성할 수 있다")
    void createUserPasswordChangeCommandWithRepeatingCharacters() {
        // given & when
        UserPasswordChangeCommand command = new UserPasswordChangeCommand("aaaaaaaaaa", "bbbbbbbbbb");

        // then
        assertThat(command.existingPassword()).isEqualTo("aaaaaaaaaa");
        assertThat(command.newPassword()).isEqualTo("bbbbbbbbbb");
    }
}