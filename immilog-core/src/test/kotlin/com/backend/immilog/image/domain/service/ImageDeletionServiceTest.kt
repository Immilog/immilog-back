package com.backend.immilog.image.domain.service

import com.backend.immilog.image.domain.enums.ImageStatus
import com.backend.immilog.image.domain.enums.ImageType
import com.backend.immilog.image.domain.model.Image
import com.backend.immilog.image.domain.model.ImageId
import com.backend.immilog.image.domain.model.ImageMetadata
import com.backend.immilog.image.domain.model.ImagePath
import com.backend.immilog.image.exception.ImageErrorCode
import com.backend.immilog.image.exception.ImageException
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("ImageDeletionService 테스트")
class ImageDeletionServiceTest {

    private val imageDeletionService = ImageDeletionService()

    @Test
    @DisplayName("정상 상태의 이미지는 삭제 가능하다")
    fun `정상 상태의 이미지는 삭제 가능하다`() {
        // given
        val image = createNormalImage()

        // when & then
        assertThatCode { imageDeletionService.validateImageCanBeDeleted(image) }
            .doesNotThrowAnyException()
    }

    @Test
    @DisplayName("이미 삭제된 이미지는 삭제할 수 없다")
    fun `이미 삭제된 이미지는 삭제할 수 없다`() {
        // given
        val image = createNormalImage().delete()

        // when & then
        assertThatThrownBy { imageDeletionService.validateImageCanBeDeleted(image) }
            .isInstanceOf(ImageException::class.java)
            .hasMessageContaining(ImageErrorCode.IMAGE_ALREADY_DELETED.getMessage())
    }

    @Test
    @DisplayName("이전 경로와 새 경로가 다르면 이전 이미지를 삭제해야 한다")
    fun `이전 경로와 새 경로가 다르면 이전 이미지를 삭제해야 한다`() {
        // given
        val previousPath = "/images/old.jpg"
        val newPath = "/images/new.jpg"

        // when
        val result = imageDeletionService.shouldDeletePreviousImage(previousPath, newPath)

        // then
        assertThat(result).isTrue()
    }

    @Test
    @DisplayName("이전 경로가 null이면 이전 이미지를 삭제하지 않는다")
    fun `이전 경로가 null이면 이전 이미지를 삭제하지 않는다`() {
        // given
        val previousPath: String? = null
        val newPath = "/images/new.jpg"

        // when
        val result = imageDeletionService.shouldDeletePreviousImage(previousPath, newPath)

        // then
        assertThat(result).isFalse()
    }

    @Test
    @DisplayName("이전 경로가 빈 문자열이면 이전 이미지를 삭제하지 않는다")
    fun `이전 경로가 빈 문자열이면 이전 이미지를 삭제하지 않는다`() {
        // given
        val previousPath = ""
        val newPath = "/images/new.jpg"

        // when
        val result = imageDeletionService.shouldDeletePreviousImage(previousPath, newPath)

        // then
        assertThat(result).isFalse()
    }

    @Test
    @DisplayName("이전 경로와 새 경로가 같으면 이전 이미지를 삭제하지 않는다")
    fun `이전 경로와 새 경로가 같으면 이전 이미지를 삭제하지 않는다`() {
        // given
        val previousPath = "/images/same.jpg"
        val newPath = "/images/same.jpg"

        // when
        val result = imageDeletionService.shouldDeletePreviousImage(previousPath, newPath)

        // then
        assertThat(result).isFalse()
    }

    @Test
    @DisplayName("전체 URL에서 이미지 경로를 추출한다")
    fun `전체 URL에서 이미지 경로를 추출한다`() {
        // given
        val fullUrl = "http://localhost:8080/images/profiles/test.jpg"

        // when
        val imagePath = imageDeletionService.extractImagePathFromUrl(fullUrl)

        // then
        assertThat(imagePath).isEqualTo("profiles/test.jpg")
    }

    @Test
    @DisplayName("이미지를 삭제된 상태로 표시한다")
    fun `이미지를 삭제된 상태로 표시한다`() {
        // given
        val image = createNormalImage()

        // when
        val deletedImage = imageDeletionService.markImageAsDeleted(image)

        // then
        assertThat(deletedImage.isDeleted()).isTrue()
        assertThat(deletedImage.status).isEqualTo(ImageStatus.DELETED)
    }

    @Test
    @DisplayName("이미 삭제된 이미지를 다시 삭제하려 하면 예외가 발생한다")
    fun `이미 삭제된 이미지를 다시 삭제하려 하면 예외가 발생한다`() {
        // given
        val image = createNormalImage().delete()

        // when & then
        assertThatThrownBy { imageDeletionService.markImageAsDeleted(image) }
            .isInstanceOf(ImageException::class.java)
            .hasMessageContaining(ImageErrorCode.IMAGE_ALREADY_DELETED.getMessage())
    }

    private fun createNormalImage(): Image {
        val imagePath = ImagePath.of("/images/test.jpg")
        val imageMetadata = ImageMetadata.of(ImageType.PROFILE)
        return Image.restore(
            id = ImageId.of("test-id"),
            path = imagePath,
            metadata = imageMetadata,
            status = ImageStatus.NORMAL
        )
    }
}