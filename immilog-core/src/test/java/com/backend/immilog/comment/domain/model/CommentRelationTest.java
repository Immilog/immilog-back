package com.backend.immilog.comment.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CommentRelation Value Object")
class CommentRelationTest {

    @Nested
    @DisplayName("생성")
    class Create {

        @Test
        @DisplayName("게시물 댓글 관계 생성")
        void createPostCommentRelation() {
            var relation = CommentRelation.of("post123", null, ReferenceType.POST);

            assertThat(relation.postId()).isEqualTo("post123");
            assertThat(relation.parentId()).isNull();
            assertThat(relation.referenceType()).isEqualTo(ReferenceType.POST);
        }

        @Test
        @DisplayName("대댓글 관계 생성")
        void createReplyCommentRelation() {
            var relation = CommentRelation.of("post123", "parent456", ReferenceType.COMMENT);

            assertThat(relation.postId()).isEqualTo("post123");
            assertThat(relation.parentId()).isEqualTo("parent456");
            assertThat(relation.referenceType()).isEqualTo(ReferenceType.COMMENT);
        }

        @Test
        @DisplayName("직접 생성")
        void createDirect() {
            var relation = new CommentRelation("post789", "parent123", ReferenceType.POST);

            assertThat(relation.postId()).isEqualTo("post789");
            assertThat(relation.parentId()).isEqualTo("parent123");
            assertThat(relation.referenceType()).isEqualTo(ReferenceType.POST);
        }
    }

    @Nested
    @DisplayName("동등성")
    class Equality {

        @Test
        @DisplayName("같은 값으로 생성된 객체는 동일")
        void sameValues() {
            var relation1 = CommentRelation.of("post123", "parent456", ReferenceType.COMMENT);
            var relation2 = CommentRelation.of("post123", "parent456", ReferenceType.COMMENT);

            assertThat(relation1).isEqualTo(relation2);
            assertThat(relation1.hashCode()).isEqualTo(relation2.hashCode());
        }

        @Test
        @DisplayName("다른 값으로 생성된 객체는 다름")
        void differentValues() {
            var relation1 = CommentRelation.of("post123", "parent456", ReferenceType.COMMENT);
            var relation2 = CommentRelation.of("post123", "parent789", ReferenceType.COMMENT);
            var relation3 = CommentRelation.of("post123", "parent456", ReferenceType.POST);

            assertThat(relation1).isNotEqualTo(relation2);
            assertThat(relation1).isNotEqualTo(relation3);
        }
    }
}