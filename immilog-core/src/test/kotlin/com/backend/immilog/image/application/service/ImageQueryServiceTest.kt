package com.backend.immilog.image.application.service

import com.backend.immilog.image.domain.enums.ImageStatus
import com.backend.immilog.image.domain.enums.ImageType
import com.backend.immilog.image.domain.model.Image
import com.backend.immilog.image.domain.model.ImageId
import com.backend.immilog.image.domain.model.ImageMetadata
import com.backend.immilog.image.domain.model.ImagePath
import com.backend.immilog.image.domain.repository.ImageRepository
import com.backend.immilog.image.exception.ImageErrorCode
import com.backend.immilog.image.exception.ImageException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
@DisplayName("ImageQueryService 테스트")
class ImageQueryServiceTest {

    @Mock
    private lateinit var imageRepository: ImageRepository

    @InjectMocks
    private lateinit var imageQueryService: ImageQueryService

    @Test
    @DisplayName("경로로 이미지를 조회한다")
    fun `경로로 이미지를 조회한다`() {
        // given
        val imagePath = "/images/test.jpg"
        val image = createTestImage()
        `when`(imageRepository.findByPath(imagePath)).thenReturn(image)

        // when
        val result = imageQueryService.getImageByPath(imagePath)

        // then
        assertThat(result).isEqualTo(image)
    }

    @Test
    @DisplayName("존재하지 않는 경로로 이미지 조회시 예외가 발생한다")
    fun `존재하지 않는 경로로 이미지 조회시 예외가 발생한다`() {
        // given
        val imagePath = "/images/not-found.jpg"
        `when`(imageRepository.findByPath(imagePath)).thenReturn(null)

        // when & then
        assertThatThrownBy { imageQueryService.getImageByPath(imagePath) }
            .isInstanceOf(ImageException::class.java)
            .hasMessageContaining(ImageErrorCode.IMAGE_NOT_FOUND.getMessage())
    }

    @Test
    @DisplayName("ID로 이미지를 조회한다")
    fun `ID로 이미지를 조회한다`() {
        // given
        val imageId = ImageId.of("test-id")
        val image = createTestImage()
        `when`(imageRepository.findById(imageId.value)).thenReturn(image)

        // when
        val result = imageQueryService.getImageById(imageId)

        // then
        assertThat(result).isEqualTo(image)
    }

    @Test
    @DisplayName("존재하지 않는 ID로 이미지 조회시 예외가 발생한다")
    fun `존재하지 않는 ID로 이미지 조회시 예외가 발생한다`() {
        // given
        val imageId = ImageId.of("not-found-id")
        `when`(imageRepository.findById(imageId.value)).thenReturn(null)

        // when & then
        assertThatThrownBy { imageQueryService.getImageById(imageId) }
            .isInstanceOf(ImageException::class.java)
            .hasMessageContaining(ImageErrorCode.IMAGE_NOT_FOUND.getMessage())
    }

    @Test
    @DisplayName("타입으로 이미지 목록을 조회한다")
    fun `타입으로 이미지 목록을 조회한다`() {
        // given
        val imageType = ImageType.PROFILE
        val images = listOf(createTestImage(), createTestImage())
        `when`(imageRepository.findByImageType(imageType)).thenReturn(images)

        // when
        val result = imageQueryService.getImagesByType(imageType)

        // then
        assertThat(result).hasSize(2)
        assertThat(result).isEqualTo(images)
    }

    @Test
    @DisplayName("경로로 이미지 존재 여부를 확인한다")
    fun `경로로 이미지 존재 여부를 확인한다`() {
        // given
        val imagePath = "/images/test.jpg"
        `when`(imageRepository.existsByPath(imagePath)).thenReturn(true)

        // when
        val result = imageQueryService.existsByPath(imagePath)

        // then
        assertThat(result).isTrue()
    }

    @Test
    @DisplayName("존재하지 않는 경로로 이미지 존재 여부 확인시 false를 반환한다")
    fun `존재하지 않는 경로로 이미지 존재 여부 확인시 false를 반환한다`() {
        // given
        val imagePath = "/images/not-found.jpg"
        `when`(imageRepository.existsByPath(imagePath)).thenReturn(false)

        // when
        val result = imageQueryService.existsByPath(imagePath)

        // then
        assertThat(result).isFalse()
    }

    private fun createTestImage(): Image {
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