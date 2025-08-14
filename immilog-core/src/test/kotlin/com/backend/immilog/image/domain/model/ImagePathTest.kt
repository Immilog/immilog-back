package com.backend.immilog.image.domain.model

import com.backend.immilog.image.exception.ImageException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("ImagePath 도메인 모델 테스트")
class ImagePathTest {

    @ParameterizedTest
    @ValueSource(strings = ["/images/test.jpg", "/images/test.jpeg", "/images/test.png", "/images/test.gif", "/images/test.webp"])
    @DisplayName("유효한 이미지 경로로 ImagePath를 생성한다")
    fun `유효한 이미지 경로로 ImagePath를 생성한다`(path: String) {
        // when
        val imagePath = ImagePath.of(path)

        // then
        assertThat(imagePath.value).isEqualTo(path)
    }

    @ParameterizedTest
    @ValueSource(strings = ["/images/TEST.JPG", "/images/TEST.JPEG", "/images/TEST.PNG", "/images/TEST.GIF", "/images/TEST.WEBP"])
    @DisplayName("대문자 확장자도 유효한 이미지 경로로 인식한다")
    fun `대문자 확장자도 유효한 이미지 경로로 인식한다`(path: String) {
        // when
        val imagePath = ImagePath.of(path)

        // then
        assertThat(imagePath.value).isEqualTo(path)
    }

    @Test
    @DisplayName("빈 경로로 ImagePath 생성시 예외가 발생한다")
    fun `빈 경로로 ImagePath 생성시 예외가 발생한다`() {
        // given
        val path = ""

        // when & then
        assertThatThrownBy { ImagePath.of(path) }
            .isInstanceOf(ImageException::class.java)
    }

    @Test
    @DisplayName("공백만 있는 경로로 ImagePath 생성시 예외가 발생한다")
    fun `공백만 있는 경로로 ImagePath 생성시 예외가 발생한다`() {
        // given
        val path = "   "

        // when & then
        assertThatThrownBy { ImagePath.of(path) }
            .isInstanceOf(ImageException::class.java)
    }

    @ParameterizedTest
    @ValueSource(strings = ["/images/test.txt", "/images/test.pdf", "/images/test.doc", "/images/test", "/images/test.exe"])
    @DisplayName("유효하지 않은 이미지 확장자로 ImagePath 생성시 예외가 발생한다")
    fun `유효하지 않은 이미지 확장자로 ImagePath 생성시 예외가 발생한다`(path: String) {
        // when & then
        assertThatThrownBy { ImagePath.of(path) }
            .isInstanceOf(ImageException::class.java)
    }

    @Test
    @DisplayName("같은 값을 가진 ImagePath는 동일하다")
    fun `같은 값을 가진 ImagePath는 동일하다`() {
        // given
        val path = "/images/test.jpg"
        val imagePath1 = ImagePath.of(path)
        val imagePath2 = ImagePath.of(path)

        // when & then
        assertThat(imagePath1).isEqualTo(imagePath2)
        assertThat(imagePath1.hashCode()).isEqualTo(imagePath2.hashCode())
    }

    @Test
    @DisplayName("다른 값을 가진 ImagePath는 다르다")
    fun `다른 값을 가진 ImagePath는 다르다`() {
        // given
        val imagePath1 = ImagePath.of("/images/test1.jpg")
        val imagePath2 = ImagePath.of("/images/test2.jpg")

        // when & then
        assertThat(imagePath1).isNotEqualTo(imagePath2)
    }

    @Test
    @DisplayName("경로에 확장자가 없으면 예외가 발생한다")
    fun `경로에 확장자가 없으면 예외가 발생한다`() {
        // given
        val path = "/images/test"

        // when & then
        assertThatThrownBy { ImagePath.of(path) }
            .isInstanceOf(ImageException::class.java)
    }
}