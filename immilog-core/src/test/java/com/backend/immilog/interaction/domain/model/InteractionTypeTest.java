package com.backend.immilog.interaction.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class InteractionTypeTest {

    @Test
    @DisplayName("LIKE 타입 검증")
    void verifyLikeType() {
        //given
        InteractionType likeType = InteractionType.LIKE;

        //when
        String typeName = likeType.name();

        //then
        assertThat(typeName).isEqualTo("LIKE");
        assertThat(likeType).isEqualTo(InteractionType.LIKE);
    }

    @Test
    @DisplayName("BOOKMARK 타입 검증")
    void verifyBookmarkType() {
        //given
        InteractionType bookmarkType = InteractionType.BOOKMARK;

        //when
        String typeName = bookmarkType.name();

        //then
        assertThat(typeName).isEqualTo("BOOKMARK");
        assertThat(bookmarkType).isEqualTo(InteractionType.BOOKMARK);
    }

    @Test
    @DisplayName("모든 InteractionType 값 검증")
    void verifyAllInteractionTypes() {
        //given
        InteractionType[] allTypes = InteractionType.values();

        //when & then
        assertThat(allTypes).hasSize(2);
        assertThat(allTypes).contains(InteractionType.LIKE, InteractionType.BOOKMARK);
    }

    @Test
    @DisplayName("enum valueOf 검증 - LIKE")
    void verifyValueOfLike() {
        //given
        String typeName = "LIKE";

        //when
        InteractionType type = InteractionType.valueOf(typeName);

        //then
        assertThat(type).isEqualTo(InteractionType.LIKE);
    }

    @Test
    @DisplayName("enum valueOf 검증 - BOOKMARK")
    void verifyValueOfBookmark() {
        //given
        String typeName = "BOOKMARK";

        //when
        InteractionType type = InteractionType.valueOf(typeName);

        //then
        assertThat(type).isEqualTo(InteractionType.BOOKMARK);
    }

    @Test
    @DisplayName("존재하지 않는 타입으로 valueOf 호출 시 예외 발생")
    void throwExceptionForInvalidValueOf() {
        //given
        String invalidTypeName = "INVALID_TYPE";

        //when & then
        assertThatThrownBy(() -> InteractionType.valueOf(invalidTypeName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("InteractionType 순서 검증")
    void verifyInteractionTypeOrder() {
        //given
        InteractionType[] types = InteractionType.values();

        //when & then
        assertThat(types[0]).isEqualTo(InteractionType.LIKE);
        assertThat(types[1]).isEqualTo(InteractionType.BOOKMARK);
    }

    @Test
    @DisplayName("InteractionType ordinal 값 검증")
    void verifyInteractionTypeOrdinals() {
        //when & then
        assertThat(InteractionType.LIKE.ordinal()).isEqualTo(0);
        assertThat(InteractionType.BOOKMARK.ordinal()).isEqualTo(1);
    }

    @Test
    @DisplayName("InteractionType 동등성 검증")
    void verifyInteractionTypeEquality() {
        //given
        InteractionType like1 = InteractionType.LIKE;
        InteractionType like2 = InteractionType.valueOf("LIKE");
        InteractionType bookmark1 = InteractionType.BOOKMARK;
        InteractionType bookmark2 = InteractionType.valueOf("BOOKMARK");

        //when & then
        assertThat(like1).isEqualTo(like2);
        assertThat(bookmark1).isEqualTo(bookmark2);
        assertThat(like1).isNotEqualTo(bookmark1);
        assertThat(like2).isNotEqualTo(bookmark2);
    }

    @Test
    @DisplayName("InteractionType toString 검증")
    void verifyInteractionTypeToString() {
        //when & then
        assertThat(InteractionType.LIKE.toString()).isEqualTo("LIKE");
        assertThat(InteractionType.BOOKMARK.toString()).isEqualTo("BOOKMARK");
    }
}