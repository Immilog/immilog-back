package com.backend.immilog.comment.domain.model;

import com.backend.immilog.shared.enums.ContentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommentTest {

    @Test
    @DisplayName("댓글 생성 - 정상 케이스")
    void createCommentSuccessfully() {
        //given
        String userId = "userId";
        String postId = "postId";
        String content = "댓글 내용";
        ReferenceType referenceType = ReferenceType.POST;

        //when
        Comment comment = Comment.of(userId, postId, content, referenceType);

        //then
        assertThat(comment.userId()).isEqualTo(userId);
        assertThat(comment.content()).isEqualTo(content);
        assertThat(comment.postId()).isEqualTo(postId);
        assertThat(comment.referenceType()).isEqualTo(referenceType);
        assertThat(comment.replyCount()).isEqualTo(0);
        assertThat(comment.likeCount()).isEqualTo(0);
        assertThat(comment.status()).isEqualTo(ContentStatus.NORMAL);
        assertThat(comment.likeUsers()).isEmpty();
        assertThat(comment.createdAt()).isNotNull();
        assertThat(comment.updatedAt()).isNull();
    }

    @Test
    @DisplayName("댓글 삭제 - 정상 케이스")
    void deleteCommentSuccessfully() {
        //given
        Comment comment = createTestComment();

        //when
        Comment deletedComment = comment.delete();

        //then
        assertThat(deletedComment.status()).isEqualTo(ContentStatus.DELETED);
        assertThat(deletedComment.updatedAt()).isNotNull();
        assertThat(deletedComment.id()).isEqualTo(comment.id());
        assertThat(deletedComment.userId()).isEqualTo(comment.userId());
        assertThat(deletedComment.content()).isEqualTo(comment.content());
    }

    @Test
    @DisplayName("댓글 좋아요 추가 - 정상 케이스")
    void addLikeUserSuccessfully() {
        //given
        Comment comment = createTestComment();
        String likeUserId = "likeUserId";

        //when
        Comment likedComment = comment.addLikeUser(likeUserId);

        //then
        assertThat(likedComment.likeCount()).isEqualTo(comment.likeCount() + 1);
        assertThat(likedComment.likeUsers()).contains(likeUserId);
        assertThat(likedComment.id()).isEqualTo(comment.id());
        assertThat(likedComment.userId()).isEqualTo(comment.userId());
    }

    @Test
    @DisplayName("댓글 좋아요 추가 - likeUsers가 null인 경우")
    void addLikeUserWhenLikeUsersIsNull() {
        //given
        Comment comment = createCommentWithNullLikeUsers();
        String likeUserId = "likeUserId";

        //when
        Comment result = comment.addLikeUser(likeUserId);

        //then
        assertThat(result).isEqualTo(comment);
    }

    @Test
    @DisplayName("답글 수 증가 - 정상 케이스")
    void increaseReplyCountSuccessfully() {
        //given
        Comment comment = createTestComment();
        int originalReplyCount = comment.replyCount();

        //when
        Comment updatedComment = comment.increaseReplyCount();

        //then
        assertThat(updatedComment.replyCount()).isEqualTo(originalReplyCount + 1);
        assertThat(updatedComment.id()).isEqualTo(comment.id());
        assertThat(updatedComment.userId()).isEqualTo(comment.userId());
        assertThat(updatedComment.content()).isEqualTo(comment.content());
    }

    @Test
    @DisplayName("댓글 내용 업데이트 - 정상 케이스")
    void updateContentSuccessfully() {
        //given
        Comment comment = createTestComment();
        String newContent = "수정된 댓글 내용";

        //when
        Comment updatedComment = comment.updateContent(newContent);

        //then
        assertThat(updatedComment.content()).isEqualTo(newContent);
        assertThat(updatedComment.updatedAt()).isNotNull();
        assertThat(updatedComment.id()).isEqualTo(comment.id());
        assertThat(updatedComment.userId()).isEqualTo(comment.userId());
        assertThat(updatedComment.replyCount()).isEqualTo(comment.replyCount());
    }

    @Test
    @DisplayName("댓글 ID 설정 - 정상 케이스")
    void withIdSuccessfully() {
        //given
        Comment comment = createTestComment();
        String newId = "newCommentId";

        //when
        Comment commentWithId = comment.withId(newId);

        //then
        assertThat(commentWithId.id()).isEqualTo(newId);
        assertThat(commentWithId.userId()).isEqualTo(comment.userId());
        assertThat(commentWithId.content()).isEqualTo(comment.content());
        assertThat(commentWithId.replyCount()).isEqualTo(comment.replyCount());
    }

    @Test
    @DisplayName("postId 조회 - 정상 케이스")
    void getPostIdSuccessfully() {
        //given
        String expectedPostId = "postId";
        Comment comment = Comment.of("userId", expectedPostId, "content", ReferenceType.POST);

        //when
        String actualPostId = comment.postId();

        //then
        assertThat(actualPostId).isEqualTo(expectedPostId);
    }

    @Test
    @DisplayName("parentId 조회 - 정상 케이스")
    void getParentIdSuccessfully() {
        //given
        String parentId = "parentId";
        Comment comment = createCommentWithParent(parentId);

        //when
        String actualParentId = comment.parentId();

        //then
        assertThat(actualParentId).isEqualTo(parentId);
    }

    @Test
    @DisplayName("referenceType 조회 - 정상 케이스")
    void getReferenceTypeSuccessfully() {
        //given
        ReferenceType expectedType = ReferenceType.COMMENT;
        Comment comment = Comment.of("userId", "postId", "content", expectedType);

        //when
        ReferenceType actualType = comment.referenceType();

        //then
        assertThat(actualType).isEqualTo(expectedType);
    }

    @Test
    @DisplayName("댓글 생성 - 다양한 ReferenceType")
    void createCommentWithDifferentReferenceTypes() {
        //given
        String userId = "userId";
        String postId = "postId";
        String content = "댓글 내용";

        //when & then
        for (ReferenceType type : ReferenceType.values()) {
            Comment comment = Comment.of(userId, postId, content, type);
            assertThat(comment.referenceType()).isEqualTo(type);
        }
    }

    @Test
    @DisplayName("여러 사용자 좋아요 추가")
    void addMultipleLikeUsers() {
        //given
        Comment comment = createTestComment();
        List<String> likeUserIds = List.of("user1", "user2", "user3");

        //when
        Comment result = comment;
        for (String userId : likeUserIds) {
            result = result.addLikeUser(userId);
        }

        //then
        assertThat(result.likeCount()).isEqualTo(comment.likeCount() + likeUserIds.size());
        assertThat(result.likeUsers()).containsAll(likeUserIds);
    }

    @Test
    @DisplayName("답글 수 여러 번 증가")
    void increaseReplyCountMultipleTimes() {
        //given
        Comment comment = createTestComment();
        int increaseCount = 5;

        //when
        Comment result = comment;
        for (int i = 0; i < increaseCount; i++) {
            result = result.increaseReplyCount();
        }

        //then
        assertThat(result.replyCount()).isEqualTo(comment.replyCount() + increaseCount);
    }

    @Test
    @DisplayName("빈 내용으로 댓글 생성")
    void createCommentWithEmptyContent() {
        //given
        String userId = "userId";
        String postId = "postId";
        String content = "";
        ReferenceType referenceType = ReferenceType.POST;

        //when
        Comment comment = Comment.of(userId, postId, content, referenceType);

        //then
        assertThat(comment.content()).isEmpty();
        assertThat(comment.userId()).isEqualTo(userId);
        assertThat(comment.postId()).isEqualTo(postId);
    }

    @Test
    @DisplayName("null 내용으로 댓글 업데이트")
    void updateContentWithNull() {
        //given
        Comment comment = createTestComment();
        String newContent = null;

        //when
        Comment updatedComment = comment.updateContent(newContent);

        //then
        assertThat(updatedComment.content()).isNull();
        assertThat(updatedComment.updatedAt()).isNotNull();
    }

    @Test
    @DisplayName("댓글 생성 시간 검증")
    void validateCreatedTime() {
        //given
        LocalDateTime beforeCreate = LocalDateTime.now().minusSeconds(1);
        
        //when
        Comment comment = Comment.of("userId", "postId", "content", ReferenceType.POST);
        
        //then
        LocalDateTime afterCreate = LocalDateTime.now().plusSeconds(1);
        assertThat(comment.createdAt()).isAfter(beforeCreate);
        assertThat(comment.createdAt()).isBefore(afterCreate);
    }

    private Comment createTestComment() {
        return new Comment(
                "commentId",
                "userId",
                "댓글 내용",
                CommentRelation.of("postId", null, ReferenceType.POST),
                0,
                0,
                ContentStatus.NORMAL,
                new ArrayList<>(),
                LocalDateTime.now(),
                null
        );
    }

    private Comment createCommentWithNullLikeUsers() {
        return new Comment(
                "commentId",
                "userId",
                "댓글 내용",
                CommentRelation.of("postId", null, ReferenceType.POST),
                0,
                0,
                ContentStatus.NORMAL,
                null,
                LocalDateTime.now(),
                null
        );
    }

    private Comment createCommentWithParent(String parentId) {
        return new Comment(
                "commentId",
                "userId",
                "댓글 내용",
                CommentRelation.of("postId", parentId, ReferenceType.COMMENT),
                0,
                0,
                ContentStatus.NORMAL,
                new ArrayList<>(),
                LocalDateTime.now(),
                null
        );
    }
}