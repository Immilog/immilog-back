package com.backend.immilog.comment.application.dto;

import com.backend.immilog.comment.domain.model.ReferenceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommentCreateCommandTest {

    @Test
    @DisplayName("CommentCreateCommand 생성 - 정상 케이스")
    void createCommentCreateCommandSuccessfully() {
        //given
        String userId = "userId";
        String postId = "postId";
        String content = "댓글 내용";
        ReferenceType referenceType = ReferenceType.POST;

        //when
        CommentCreateCommand command = new CommentCreateCommand(userId, postId, content, null, referenceType);

        //then
        assertThat(command.userId()).isEqualTo(userId);
        assertThat(command.postId()).isEqualTo(postId);
        assertThat(command.content()).isEqualTo(content);
        assertThat(command.referenceType()).isEqualTo(referenceType);
    }

    @Test
    @DisplayName("CommentCreateCommand 생성 - COMMENT 타입")
    void createCommentCreateCommandWithCommentType() {
        //given
        String userId = "userId";
        String postId = "postId";
        String content = "대댓글 내용";
        ReferenceType referenceType = ReferenceType.COMMENT;

        //when
        CommentCreateCommand command = new CommentCreateCommand(userId, postId, content, null, referenceType);

        //then
        assertThat(command.referenceType()).isEqualTo(ReferenceType.COMMENT);
        assertThat(command.content()).isEqualTo(content);
    }


    @Test
    @DisplayName("CommentCreateCommand 생성 - null 필드들")
    void createCommentCreateCommandWithNullFields() {
        //given
        String userId = null;
        String postId = null;
        String content = null;
        ReferenceType referenceType = null;

        //when
        CommentCreateCommand command = new CommentCreateCommand(userId, postId, content, null, referenceType);

        //then
        assertThat(command.userId()).isNull();
        assertThat(command.postId()).isNull();
        assertThat(command.content()).isNull();
        assertThat(command.referenceType()).isNull();
    }

    @Test
    @DisplayName("CommentCreateCommand 생성 - 빈 문자열 필드들")
    void createCommentCreateCommandWithEmptyFields() {
        //given
        String userId = "";
        String postId = "";
        String content = "";
        ReferenceType referenceType = ReferenceType.POST;

        //when
        CommentCreateCommand command = new CommentCreateCommand(userId, postId, content, null, referenceType);

        //then
        assertThat(command.userId()).isEmpty();
        assertThat(command.postId()).isEmpty();
        assertThat(command.content()).isEmpty();
        assertThat(command.referenceType()).isEqualTo(ReferenceType.POST);
    }

    @Test
    @DisplayName("CommentCreateCommand equals 검증")
    void verifyCommentCreateCommandEquals() {
        //given
        CommentCreateCommand command1 = new CommentCreateCommand(
                "userId", "postId", "content", null, ReferenceType.POST
        );
        CommentCreateCommand command2 = new CommentCreateCommand(
                "userId", "postId", "content", null, ReferenceType.POST
        );
        CommentCreateCommand command3 = new CommentCreateCommand(
                "differentUserId", "postId", "content", null, ReferenceType.POST
        );

        //when & then
        assertThat(command1).isEqualTo(command2);
        assertThat(command1).isNotEqualTo(command3);
        assertThat(command1.hashCode()).isEqualTo(command2.hashCode());
    }

    @Test
    @DisplayName("CommentCreateCommand toString 검증")
    void verifyCommentCreateCommandToString() {
        //given
        CommentCreateCommand command = new CommentCreateCommand(
                "userId", "postId", "content", null, ReferenceType.POST
        );

        //when
        String toString = command.toString();

        //then
        assertThat(toString).contains("userId");
        assertThat(toString).contains("postId");
        assertThat(toString).contains("content");
        assertThat(toString).contains("POST");
    }

    @Test
    @DisplayName("모든 ReferenceType으로 CommentCreateCommand 생성")
    void createCommentCreateCommandWithAllReferenceTypes() {
        //given
        String userId = "userId";
        String postId = "postId";
        String content = "댓글 내용";

        //when & then
        for (ReferenceType type : ReferenceType.values()) {
            CommentCreateCommand command = new CommentCreateCommand(userId, postId, content, null, type);
            assertThat(command.referenceType()).isEqualTo(type);
            assertThat(command.userId()).isEqualTo(userId);
            assertThat(command.postId()).isEqualTo(postId);
            assertThat(command.content()).isEqualTo(content);
        }
    }

    @Test
    @DisplayName("긴 내용으로 CommentCreateCommand 생성")
    void createCommentCreateCommandWithLongContent() {
        //given
        String userId = "userId";
        String postId = "postId";
        String longContent = "a".repeat(1000);
        ReferenceType referenceType = ReferenceType.POST;

        //when
        CommentCreateCommand command = new CommentCreateCommand(userId, postId, longContent, null, referenceType);

        //then
        assertThat(command.content()).hasSize(1000);
        assertThat(command.content()).isEqualTo(longContent);
    }

    @Test
    @DisplayName("특수 문자가 포함된 내용으로 CommentCreateCommand 생성")
    void createCommentCreateCommandWithSpecialCharacters() {
        //given
        String userId = "userId";
        String postId = "postId";
        String specialContent = "댓글 내용 @#$%^&*()_+{}[]|\\:;\"'<>?,./";
        ReferenceType referenceType = ReferenceType.POST;

        //when
        CommentCreateCommand command = new CommentCreateCommand(userId, postId, specialContent, null, referenceType);

        //then
        assertThat(command.content()).isEqualTo(specialContent);
        assertThat(command.userId()).isEqualTo(userId);
        assertThat(command.postId()).isEqualTo(postId);
    }

    @Test
    @DisplayName("유니코드 문자가 포함된 내용으로 CommentCreateCommand 생성")
    void createCommentCreateCommandWithUnicodeContent() {
        //given
        String userId = "userId";
        String postId = "postId";
        String unicodeContent = "댓글 내용 😀😃😄😁😆😅😂🤣";
        ReferenceType referenceType = ReferenceType.POST;

        //when
        CommentCreateCommand command = new CommentCreateCommand(userId, postId, unicodeContent, null, referenceType);

        //then
        assertThat(command.content()).isEqualTo(unicodeContent);
        assertThat(command.userId()).isEqualTo(userId);
        assertThat(command.postId()).isEqualTo(postId);
    }

    @Test
    @DisplayName("줄바꿈이 포함된 내용으로 CommentCreateCommand 생성")
    void createCommentCreateCommandWithMultilineContent() {
        //given
        String userId = "userId";
        String postId = "postId";
        String multilineContent = "첫 번째 줄\n두 번째 줄\r\n세 번째 줄";
        ReferenceType referenceType = ReferenceType.POST;

        //when
        CommentCreateCommand command = new CommentCreateCommand(userId, postId, multilineContent, null, referenceType);

        //then
        assertThat(command.content()).isEqualTo(multilineContent);
        assertThat(command.content()).contains("\n");
        assertThat(command.content()).contains("\r\n");
    }

    @Test
    @DisplayName("공백만 있는 내용으로 CommentCreateCommand 생성")
    void createCommentCreateCommandWithWhitespaceContent() {
        //given
        String userId = "userId";
        String postId = "postId";
        String whitespaceContent = "   \t\n\r   ";
        ReferenceType referenceType = ReferenceType.POST;

        //when
        CommentCreateCommand command = new CommentCreateCommand(userId, postId, whitespaceContent, null, referenceType);

        //then
        assertThat(command.content()).isEqualTo(whitespaceContent);
        assertThat(command.content().trim()).isEmpty();
    }
}