package com.backend.immilog.image.domain.service

import com.backend.immilog.image.exception.ImageErrorCode
import com.backend.immilog.image.exception.ImageException
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.mock.web.MockMultipartFile

@DisplayName("ImageValidationService 테스트")
class ImageValidationServiceTest {

    private val imageValidationService = ImageValidationService()

    @ParameterizedTest
    @ValueSource(strings = ["image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"])
    @DisplayName("유효한 이미지 파일을 검증한다")
    fun `유효한 이미지 파일을 검증한다`(contentType: String) {
        // given
        val file = MockMultipartFile(
            "file",
            "test.jpg",
            contentType,
            "test content".toByteArray()
        )

        // when & then
        assertThatCode { imageValidationService.validateImageFile(file) }
            .doesNotThrowAnyException()
    }

    @Test
    @DisplayName("빈 파일 검증시 예외가 발생한다")
    fun `빈 파일 검증시 예외가 발생한다`() {
        // given
        val file = MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            ByteArray(0)
        )

        // when & then
        assertThatThrownBy { imageValidationService.validateImageFile(file) }
            .isInstanceOf(ImageException::class.java)
            .hasMessageContaining(ImageErrorCode.INVALID_IMAGE_FILE.getMessage())
    }

    @Test
    @DisplayName("파일 크기가 너무 큰 경우 예외가 발생한다")
    fun `파일 크기가 너무 큰 경우 예외가 발생한다`() {
        // given
        val largeContent = ByteArray(11 * 1024 * 1024) // 11MB
        val file = MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            largeContent
        )

        // when & then
        assertThatThrownBy { imageValidationService.validateImageFile(file) }
            .isInstanceOf(ImageException::class.java)
            .hasMessageContaining(ImageErrorCode.IMAGE_FILE_TOO_LARGE.getMessage())
    }

    @ParameterizedTest
    @ValueSource(strings = ["text/plain", "application/pdf", "video/mp4", "audio/mp3"])
    @DisplayName("지원하지 않는 콘텐츠 타입으로 검증시 예외가 발생한다")
    fun `지원하지 않는 콘텐츠 타입으로 검증시 예외가 발생한다`(contentType: String) {
        // given
        val file = MockMultipartFile(
            "file",
            "test.txt",
            contentType,
            "test content".toByteArray()
        )

        // when & then
        assertThatThrownBy { imageValidationService.validateImageFile(file) }
            .isInstanceOf(ImageException::class.java)
            .hasMessageContaining(ImageErrorCode.UNSUPPORTED_IMAGE_FORMAT.getMessage())
    }

    @Test
    @DisplayName("콘텐츠 타입이 null인 경우 예외가 발생한다")
    fun `콘텐츠 타입이 null인 경우 예외가 발생한다`() {
        // given
        val file = MockMultipartFile(
            "file",
            "test.jpg",
            null,
            "test content".toByteArray()
        )

        // when & then
        assertThatThrownBy { imageValidationService.validateImageFile(file) }
            .isInstanceOf(ImageException::class.java)
            .hasMessageContaining(ImageErrorCode.UNSUPPORTED_IMAGE_FORMAT.getMessage())
    }

    @ParameterizedTest
    @ValueSource(strings = ["test.txt", "test.pdf", "test.doc", "test.exe", "test"])
    @DisplayName("지원하지 않는 파일 확장자로 검증시 예외가 발생한다")
    fun `지원하지 않는 파일 확장자로 검증시 예외가 발생한다`(fileName: String) {
        // given
        val file = MockMultipartFile(
            "file",
            fileName,
            "image/jpeg",
            "test content".toByteArray()
        )

        // when & then
        assertThatThrownBy { imageValidationService.validateImageFile(file) }
            .isInstanceOf(ImageException::class.java)
            .hasMessageContaining(ImageErrorCode.UNSUPPORTED_IMAGE_FORMAT.getMessage())
    }

    @Test
    @DisplayName("여러 유효한 이미지 파일들을 검증한다")
    fun `여러 유효한 이미지 파일들을 검증한다`() {
        // given
        val files = listOf(
            MockMultipartFile("file1", "test1.jpg", "image/jpeg", "content1".toByteArray()),
            MockMultipartFile("file2", "test2.png", "image/png", "content2".toByteArray())
        )

        // when & then
        assertThatCode { imageValidationService.validateImageFiles(files) }
            .doesNotThrowAnyException()
    }

    @Test
    @DisplayName("빈 파일 목록으로 검증시 예외가 발생한다")
    fun `빈 파일 목록으로 검증시 예외가 발생한다`() {
        // given
        val files = emptyList<MockMultipartFile>()

        // when & then
        assertThatThrownBy { imageValidationService.validateImageFiles(files) }
            .isInstanceOf(ImageException::class.java)
            .hasMessageContaining(ImageErrorCode.NO_FILES_PROVIDED.getMessage())
    }

    @Test
    @DisplayName("파일 목록 중 하나라도 유효하지 않으면 예외가 발생한다")
    fun `파일 목록 중 하나라도 유효하지 않으면 예외가 발생한다`() {
        // given
        val files = listOf(
            MockMultipartFile("file1", "test1.jpg", "image/jpeg", "content1".toByteArray()),
            MockMultipartFile("file2", "test2.txt", "text/plain", "content2".toByteArray())
        )

        // when & then
        assertThatThrownBy { imageValidationService.validateImageFiles(files) }
            .isInstanceOf(ImageException::class.java)
            .hasMessageContaining(ImageErrorCode.UNSUPPORTED_IMAGE_FORMAT.getMessage())
    }
}