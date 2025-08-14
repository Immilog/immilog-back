package com.backend.immilog.image.domain.model

import com.backend.immilog.image.domain.enums.ImageStatus
import com.backend.immilog.image.domain.enums.ImageType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Image 도메인 모델 테스트")
class ImageTest {

    @Test
    @DisplayName("이미지를 생성한다")
    fun `이미지를 생성한다`() {
        // given
        val imagePath = ImagePath.of("/images/test.jpg")
        val imageMetadata = ImageMetadata.of(ImageType.PROFILE)

        // when
        val image = Image.create(imagePath, imageMetadata)

        // then
        assertThat(image.id).isNull()
        assertThat(image.path).isEqualTo(imagePath)
        assertThat(image.metadata).isEqualTo(imageMetadata)
        assertThat(image.status).isEqualTo(ImageStatus.NORMAL)
    }

    @Test
    @DisplayName("이미지를 복원한다")
    fun `이미지를 복원한다`() {
        // given
        val imageId = ImageId.of("test-id")
        val imagePath = ImagePath.of("/images/test.jpg")
        val imageMetadata = ImageMetadata.of(ImageType.PROFILE)
        val status = ImageStatus.NORMAL

        // when
        val image = Image.restore(imageId, imagePath, imageMetadata, status)

        // then
        assertThat(image.id).isEqualTo(imageId)
        assertThat(image.path).isEqualTo(imagePath)
        assertThat(image.metadata).isEqualTo(imageMetadata)
        assertThat(image.status).isEqualTo(status)
    }

    @Test
    @DisplayName("이미지를 삭제한다")
    fun `이미지를 삭제한다`() {
        // given
        val imagePath = ImagePath.of("/images/test.jpg")
        val imageMetadata = ImageMetadata.of(ImageType.PROFILE)
        val image = Image.create(imagePath, imageMetadata)

        // when
        val deletedImage = image.delete()

        // then
        assertThat(deletedImage.status).isEqualTo(ImageStatus.DELETED)
        assertThat(deletedImage.metadata.updatedAt).isAfter(image.metadata.updatedAt)
    }

    @Test
    @DisplayName("이미지를 활성화한다")
    fun `이미지를 활성화한다`() {
        // given
        val imagePath = ImagePath.of("/images/test.jpg")
        val imageMetadata = ImageMetadata.of(ImageType.PROFILE)
        val image = Image.create(imagePath, imageMetadata).delete()

        // when
        val activatedImage = image.activate()

        // then
        assertThat(activatedImage.status).isEqualTo(ImageStatus.NORMAL)
        assertThat(activatedImage.metadata.updatedAt).isAfter(image.metadata.updatedAt)
    }

    @Test
    @DisplayName("삭제된 이미지인지 확인한다")
    fun `삭제된 이미지인지 확인한다`() {
        // given
        val imagePath = ImagePath.of("/images/test.jpg")
        val imageMetadata = ImageMetadata.of(ImageType.PROFILE)
        val image = Image.create(imagePath, imageMetadata)
        val deletedImage = image.delete()

        // when & then
        assertThat(image.isDeleted()).isFalse()
        assertThat(deletedImage.isDeleted()).isTrue()
    }

    @Test
    @DisplayName("활성화된 이미지인지 확인한다")
    fun `활성화된 이미지인지 확인한다`() {
        // given
        val imagePath = ImagePath.of("/images/test.jpg")
        val imageMetadata = ImageMetadata.of(ImageType.PROFILE)
        val image = Image.create(imagePath, imageMetadata)
        val deletedImage = image.delete()

        // when & then
        assertThat(image.isActive()).isTrue()
        assertThat(deletedImage.isActive()).isFalse()
    }

    @Test
    @DisplayName("이미지 경로를 업데이트한다")
    fun `이미지 경로를 업데이트한다`() {
        // given
        val imagePath = ImagePath.of("/images/test.jpg")
        val imageMetadata = ImageMetadata.of(ImageType.PROFILE)
        val image = Image.create(imagePath, imageMetadata)
        val newPath = ImagePath.of("/images/updated.jpg")

        // when
        val updatedImage = image.updatePath(newPath)

        // then
        assertThat(updatedImage.path).isEqualTo(newPath)
        assertThat(updatedImage.metadata.updatedAt).isAfter(image.metadata.updatedAt)
    }
}