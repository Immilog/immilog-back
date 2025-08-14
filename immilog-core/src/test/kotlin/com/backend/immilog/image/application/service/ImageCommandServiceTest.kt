package com.backend.immilog.image.application.service

import com.backend.immilog.image.domain.enums.ImageStatus
import com.backend.immilog.image.domain.enums.ImageType
import com.backend.immilog.image.domain.model.Image
import com.backend.immilog.image.domain.model.ImageId
import com.backend.immilog.image.domain.model.ImageMetadata
import com.backend.immilog.image.domain.model.ImagePath
import com.backend.immilog.image.domain.repository.ImageRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
@DisplayName("ImageCommandService 테스트")
class ImageCommandServiceTest {

    @Mock
    private lateinit var imageRepository: ImageRepository

    @InjectMocks
    private lateinit var imageCommandService: ImageCommandService

    @Test
    @DisplayName("이미지를 저장한다")
    fun `이미지를 저장한다`() {
        // given
        val imagePath = ImagePath.of("/images/test.jpg")
        val imageMetadata = ImageMetadata.of(ImageType.PROFILE)
        val image = Image.create(imagePath, imageMetadata)
        val savedImage = image.copy(id = ImageId.of("saved-id"))

        `when`(imageRepository.save(image)).thenReturn(savedImage)

        // when
        val result = imageCommandService.save(image)

        // then
        assertThat(result.id?.value).isEqualTo("saved-id")
        verify(imageRepository).save(image)
    }

    @Test
    @DisplayName("이미지를 삭제한다")
    fun `이미지를 삭제한다`() {
        // given
        val imagePath = ImagePath.of("/images/test.jpg")
        val imageMetadata = ImageMetadata.of(ImageType.PROFILE)
        val image = Image.restore(
            id = ImageId.of("test-id"),
            path = imagePath,
            metadata = imageMetadata,
            status = ImageStatus.NORMAL
        )

        // when
        imageCommandService.delete(image)

        // then
        verify(imageRepository).delete(image)
    }
}