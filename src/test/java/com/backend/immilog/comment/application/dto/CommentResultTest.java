package com.backend.immilog.comment.application.dto;

import com.backend.immilog.comment.domain.model.Comment;
import com.backend.immilog.comment.domain.model.CommentRelation;
import com.backend.immilog.comment.domain.model.ReferenceType;
import com.backend.immilog.post.domain.model.post.PostStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

class CommentResultTest {

    @Test
    @DisplayName("Comment로부터 CommentResult 생성 - 정상 케이스")
    void createCommentResultFromComment() {
        //given
        Comment comment = createTestComment();

        //when
        CommentResult result = CommentResult.from(comment);

        //then
        assertThat(result.id()).isEqualTo(comment.id());
        assertThat(result.userId()).isEqualTo(comment.userId());
        assertThat(result.content()).isEqualTo(comment.content());
        assertThat(result.postId()).isEqualTo(comment.postId());
        assertThat(result.parentId()).isEqualTo(comment.parentId());
        assertThat(result.referenceType()).isEqualTo(comment.referenceType());
        assertThat(result.replyCount()).isEqualTo(comment.replyCount());
        assertThat(result.likeCount()).isEqualTo(comment.likeCount());
        assertThat(result.status()).isEqualTo(comment.status());
        assertThat(result.createdAt()).isEqualTo(comment.createdAt());
        assertThat(result.updatedAt()).isEqualTo(comment.updatedAt());
    }

    @Test
    @DisplayName("Comment로부터 CommentResult 생성 - parentId가 null인 경우")
    void createCommentResultFromCommentWithNullParentId() {
        //given
        Comment comment = createTestCommentWithNullParent();

        //when
        CommentResult result = CommentResult.from(comment);

        //then
        assertThat(result.parentId()).isNull();
        assertThat(result.id()).isEqualTo(comment.id());
        assertThat(result.postId()).isEqualTo(comment.postId());
    }

    @Test
    @DisplayName("Comment로부터 CommentResult 생성 - postId가 null인 경우")
    void createCommentResultFromCommentWithNullPostId() {
        //given
        Comment comment = createTestCommentWithNullPostId();

        //when
        CommentResult result = CommentResult.from(comment);

        //then
        assertThat(result.postId()).isNull();
        assertThat(result.id()).isEqualTo(comment.id());
        assertThat(result.userId()).isEqualTo(comment.userId());
    }

    @Test
    @DisplayName("CommentResult 생성자 - 모든 필드 검증")
    void createCommentResultWithAllFields() {
        //given
        String id = "commentId";
        String userId = "userId";
        String content = "댓글 내용";
        String postId = "postId";
        String parentId = "parentId";
        ReferenceType referenceType = ReferenceType.COMMENT;
        int replyCount = 5;
        Integer likeCount = 10;
        PostStatus status = PostStatus.NORMAL;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        //when
        CommentResult result = new CommentResult(
                id, userId, content, postId, parentId, referenceType,
                replyCount, likeCount, status, createdAt, updatedAt
        );

        //then
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.content()).isEqualTo(content);
        assertThat(result.postId()).isEqualTo(postId);
        assertThat(result.parentId()).isEqualTo(parentId);
        assertThat(result.referenceType()).isEqualTo(referenceType);
        assertThat(result.replyCount()).isEqualTo(replyCount);
        assertThat(result.likeCount()).isEqualTo(likeCount);
        assertThat(result.status()).isEqualTo(status);
        assertThat(result.createdAt()).isEqualTo(createdAt);
        assertThat(result.updatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("CommentResult equals 검증")
    void verifyCommentResultEquals() {
        //given
        LocalDateTime now = LocalDateTime.now();
        CommentResult result1 = new CommentResult(
                "id", "userId", "content", "postId", null,
                ReferenceType.POST, 0, 0, PostStatus.NORMAL, now, null
        );
        CommentResult result2 = new CommentResult(
                "id", "userId", "content", "postId", null,
                ReferenceType.POST, 0, 0, PostStatus.NORMAL, now, null
        );
        CommentResult result3 = new CommentResult(
                "differentId", "userId", "content", "postId", null,
                ReferenceType.POST, 0, 0, PostStatus.NORMAL, now, null
        );

        //when & then
        assertThat(result1).isEqualTo(result2);
        assertThat(result1).isNotEqualTo(result3);
        assertThat(result1.hashCode()).isEqualTo(result2.hashCode());
    }

    @Test
    @DisplayName("CommentResult toString 검증")
    void verifyCommentResultToString() {
        //given
        CommentResult result = new CommentResult(
                "id", "userId", "content", "postId", null,
                ReferenceType.POST, 0, 0, PostStatus.NORMAL,
                LocalDateTime.now(), null
        );

        //when
        String toString = result.toString();

        //then
        assertThat(toString).contains("id");
        assertThat(toString).contains("userId");
        assertThat(toString).contains("content");
        assertThat(toString).contains("postId");
    }

    @Test
    @DisplayName("다양한 ReferenceType으로 CommentResult 생성")
    void createCommentResultWithDifferentReferenceTypes() {
        //given & when & then
        for (ReferenceType type : ReferenceType.values()) {
            CommentResult result = new CommentResult(
                    "id", "userId", "content", "postId", null,
                    type, 0, 0, PostStatus.NORMAL,
                    LocalDateTime.now(), null
            );
            assertThat(result.referenceType()).isEqualTo(type);
        }
    }

    @Test
    @DisplayName("다양한 PostStatus로 CommentResult 생성")
    void createCommentResultWithDifferentPostStatus() {
        //given & when & then
        for (PostStatus status : PostStatus.values()) {
            CommentResult result = new CommentResult(
                    "id", "userId", "content", "postId", null,
                    ReferenceType.POST, 0, 0, status,
                    LocalDateTime.now(), null
            );
            assertThat(result.status()).isEqualTo(status);
        }
    }

    @Test
    @DisplayName("null 필드들로 CommentResult 생성")
    void createCommentResultWithNullFields() {
        //given
        CommentResult result = new CommentResult(
                null, null, null, null, null,
                null, 0, null, null, null, null
        );

        //when & then
        assertThat(result.id()).isNull();
        assertThat(result.userId()).isNull();
        assertThat(result.content()).isNull();
        assertThat(result.postId()).isNull();
        assertThat(result.parentId()).isNull();
        assertThat(result.referenceType()).isNull();
        assertThat(result.replyCount()).isEqualTo(0);
        assertThat(result.likeCount()).isNull();
        assertThat(result.status()).isNull();
        assertThat(result.createdAt()).isNull();
        assertThat(result.updatedAt()).isNull();
    }

    @Test
    @DisplayName("빈 문자열 필드들로 CommentResult 생성")
    void createCommentResultWithEmptyStringFields() {
        //given
        CommentResult result = new CommentResult(
                "", "", "", "", "",
                ReferenceType.POST, 0, 0, PostStatus.NORMAL,
                LocalDateTime.now(), null
        );

        //when & then
        assertThat(result.id()).isEmpty();
        assertThat(result.userId()).isEmpty();
        assertThat(result.content()).isEmpty();
        assertThat(result.postId()).isEmpty();
        assertThat(result.parentId()).isEmpty();
    }

    @Test
    @DisplayName("음수 카운트로 CommentResult 생성")
    void createCommentResultWithNegativeCounts() {
        //given
        CommentResult result = new CommentResult(
                "id", "userId", "content", "postId", null,
                ReferenceType.POST, -1, -5, PostStatus.NORMAL,
                LocalDateTime.now(), null
        );

        //when & then
        assertThat(result.replyCount()).isEqualTo(-1);
        assertThat(result.likeCount()).isEqualTo(-5);
    }

    private Comment createTestComment() {
        return new Comment(
                "commentId",
                "userId",
                "댓글 내용",
                CommentRelation.of("postId", "parentId", ReferenceType.COMMENT),
                3,
                5,
                PostStatus.NORMAL,
                new ArrayList<>(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private Comment createTestCommentWithNullParent() {
        return new Comment(
                "commentId",
                "userId",
                "댓글 내용",
                CommentRelation.of("postId", null, ReferenceType.POST),
                0,
                0,
                PostStatus.NORMAL,
                new ArrayList<>(),
                LocalDateTime.now(),
                null
        );
    }

    private Comment createTestCommentWithNullPostId() {
        return new Comment(
                "commentId",
                "userId",
                "댓글 내용",
                CommentRelation.of(null, null, ReferenceType.POST),
                0,
                0,
                PostStatus.NORMAL,
                new ArrayList<>(),
                LocalDateTime.now(),
                null
        );
    }
}