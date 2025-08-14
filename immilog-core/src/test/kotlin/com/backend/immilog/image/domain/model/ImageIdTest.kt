package com.backend.immilog.image.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("ImageId 도메인 모델 테스트")
class ImageIdTest {

    @Test
    @DisplayName("유효한 값으로 ImageId를 생성한다")
    fun `유효한 값으로 ImageId를 생성한다`() {
        // given
        val value = "test-image-id"

        // when
        val imageId = ImageId.of(value)

        // then
        assertThat(imageId.value).isEqualTo(value)
    }

    @Test
    @DisplayName("null 값으로 ImageId 생성시 예외가 발생한다")
    fun `null 값으로 ImageId 생성시 예외가 발생한다`() {
        // given
        val value: String? = null

        // when & then
        assertThatThrownBy { ImageId.of(value) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("ImageId value must be not null or empty")
    }

    @Test
    @DisplayName("빈 값으로 ImageId 생성시 예외가 발생한다")
    fun `빈 값으로 ImageId 생성시 예외가 발생한다`() {
        // given
        val value = ""

        // when & then
        assertThatThrownBy { ImageId.of(value) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("ImageId value must be not null or empty")
    }

    @Test
    @DisplayName("generate 메서드는 null을 반환한다")
    fun `generate 메서드는 null을 반환한다`() {
        // when
        val imageId = ImageId.generate()

        // then
        assertThat(imageId).isNull()
    }

    @Test
    @DisplayName("같은 값을 가진 ImageId는 동일하다")
    fun `같은 값을 가진 ImageId는 동일하다`() {
        // given
        val value = "test-image-id"
        val imageId1 = ImageId.of(value)
        val imageId2 = ImageId.of(value)

        // when & then
        assertThat(imageId1).isEqualTo(imageId2)
        assertThat(imageId1.hashCode()).isEqualTo(imageId2.hashCode())
    }

    @Test
    @DisplayName("다른 값을 가진 ImageId는 다르다")
    fun `다른 값을 가진 ImageId는 다르다`() {
        // given
        val imageId1 = ImageId.of("test-image-id-1")
        val imageId2 = ImageId.of("test-image-id-2")

        // when & then
        assertThat(imageId1).isNotEqualTo(imageId2)
    }
}