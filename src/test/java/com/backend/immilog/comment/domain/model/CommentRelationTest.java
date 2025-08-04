package com.backend.immilog.comment.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CommentRelationTest {

    @Test
    @DisplayName("CommentRelation 생성 - 정상 케이스")
    void createCommentRelationSuccessfully() {
        //given
        String postId = "postId";
        String parentId = "parentId";
        ReferenceType referenceType = ReferenceType.COMMENT;

        //when
        CommentRelation relation = CommentRelation.of(postId, parentId, referenceType);

        //then
        assertThat(relation.postId()).isEqualTo(postId);
        assertThat(relation.parentId()).isEqualTo(parentId);
        assertThat(relation.referenceType()).isEqualTo(referenceType);
    }

    @Test
    @DisplayName("CommentRelation 생성 - parentId가 null인 경우")
    void createCommentRelationWithNullParentId() {
        //given
        String postId = "postId";
        String parentId = null;
        ReferenceType referenceType = ReferenceType.POST;

        //when
        CommentRelation relation = CommentRelation.of(postId, parentId, referenceType);

        //then
        assertThat(relation.postId()).isEqualTo(postId);
        assertThat(relation.parentId()).isNull();
        assertThat(relation.referenceType()).isEqualTo(referenceType);
    }

    @Test
    @DisplayName("CommentRelation 생성 - 모든 ReferenceType으로 생성")
    void createCommentRelationWithAllReferenceTypes() {
        //given
        String postId = "postId";
        String parentId = "parentId";

        //when & then
        for (ReferenceType type : ReferenceType.values()) {
            CommentRelation relation = CommentRelation.of(postId, parentId, type);
            assertThat(relation.referenceType()).isEqualTo(type);
            assertThat(relation.postId()).isEqualTo(postId);
            assertThat(relation.parentId()).isEqualTo(parentId);
        }
    }

    @Test
    @DisplayName("CommentRelation 생성 - postId가 null인 경우")
    void createCommentRelationWithNullPostId() {
        //given
        String postId = null;
        String parentId = "parentId";
        ReferenceType referenceType = ReferenceType.COMMENT;

        //when
        CommentRelation relation = CommentRelation.of(postId, parentId, referenceType);

        //then
        assertThat(relation.postId()).isNull();
        assertThat(relation.parentId()).isEqualTo(parentId);
        assertThat(relation.referenceType()).isEqualTo(referenceType);
    }

    @Test
    @DisplayName("CommentRelation 생성 - 빈 문자열 ID들")
    void createCommentRelationWithEmptyStrings() {
        //given
        String postId = "";
        String parentId = "";
        ReferenceType referenceType = ReferenceType.POST;

        //when
        CommentRelation relation = CommentRelation.of(postId, parentId, referenceType);

        //then
        assertThat(relation.postId()).isEmpty();
        assertThat(relation.parentId()).isEmpty();
        assertThat(relation.referenceType()).isEqualTo(referenceType);
    }

    @Test
    @DisplayName("CommentRelation equals 검증")
    void verifyCommentRelationEquals() {
        //given
        String postId = "postId";
        String parentId = "parentId";
        ReferenceType referenceType = ReferenceType.COMMENT;

        CommentRelation relation1 = CommentRelation.of(postId, parentId, referenceType);
        CommentRelation relation2 = CommentRelation.of(postId, parentId, referenceType);
        CommentRelation relation3 = CommentRelation.of("differentPostId", parentId, referenceType);

        //when & then
        assertThat(relation1).isEqualTo(relation2);
        assertThat(relation1).isNotEqualTo(relation3);
        assertThat(relation1.hashCode()).isEqualTo(relation2.hashCode());
    }

    @Test
    @DisplayName("CommentRelation toString 검증")
    void verifyCommentRelationToString() {
        //given
        String postId = "postId";
        String parentId = "parentId";
        ReferenceType referenceType = ReferenceType.COMMENT;

        //when
        CommentRelation relation = CommentRelation.of(postId, parentId, referenceType);
        String toString = relation.toString();

        //then
        assertThat(toString).contains(postId);
        assertThat(toString).contains(parentId);
        assertThat(toString).contains(referenceType.toString());
    }

    @Test
    @DisplayName("CommentRelation 생성 - null ReferenceType")
    void createCommentRelationWithNullReferenceType() {
        //given
        String postId = "postId";
        String parentId = "parentId";
        ReferenceType referenceType = null;

        //when
        CommentRelation relation = CommentRelation.of(postId, parentId, referenceType);

        //then
        assertThat(relation.postId()).isEqualTo(postId);
        assertThat(relation.parentId()).isEqualTo(parentId);
        assertThat(relation.referenceType()).isNull();
    }

    @Test
    @DisplayName("CommentRelation 생성 - 모든 필드가 null")
    void createCommentRelationWithAllNullFields() {
        //given
        String postId = null;
        String parentId = null;
        ReferenceType referenceType = null;

        //when
        CommentRelation relation = CommentRelation.of(postId, parentId, referenceType);

        //then
        assertThat(relation.postId()).isNull();
        assertThat(relation.parentId()).isNull();
        assertThat(relation.referenceType()).isNull();
    }
}