package com.backend.immilog.image.domain.model

import com.backend.immilog.image.domain.enums.ImageType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("ImageMetadata 도메인 모델 테스트")
class ImageMetadataTest {

    @Test
    @DisplayName("최소 정보로 ImageMetadata를 생성한다")
    fun `최소 정보로 ImageMetadata를 생성한다`() {
        // given
        val imageType = ImageType.PROFILE

        // when
        val metadata = ImageMetadata.of(imageType)

        // then
        assertThat(metadata.imageType).isEqualTo(imageType)
        assertThat(metadata.originalFileName).isNull()
        assertThat(metadata.fileSize).isNull()
        assertThat(metadata.contentType).isNull()
        assertThat(metadata.createdAt).isBeforeOrEqualTo(LocalDateTime.now())
        assertThat(metadata.updatedAt).isBeforeOrEqualTo(LocalDateTime.now())
    }

    @Test
    @DisplayName("모든 정보로 ImageMetadata를 생성한다")
    fun `모든 정보로 ImageMetadata를 생성한다`() {
        // given
        val imageType = ImageType.PROFILE
        val originalFileName = "test.jpg"
        val fileSize = 12345L
        val contentType = "image/jpeg"

        // when
        val metadata = ImageMetadata.of(imageType, originalFileName, fileSize, contentType)

        // then
        assertThat(metadata.imageType).isEqualTo(imageType)
        assertThat(metadata.originalFileName).isEqualTo(originalFileName)
        assertThat(metadata.fileSize).isEqualTo(fileSize)
        assertThat(metadata.contentType).isEqualTo(contentType)
        assertThat(metadata.createdAt).isBeforeOrEqualTo(LocalDateTime.now())
        assertThat(metadata.updatedAt).isBeforeOrEqualTo(LocalDateTime.now())
    }

    @Test
    @DisplayName("타임스탬프를 업데이트한다")
    fun `타임스탬프를 업데이트한다`() {
        // given
        val imageType = ImageType.PROFILE
        val metadata = ImageMetadata.of(imageType)
        val originalUpdatedAt = metadata.updatedAt

        // 시간 차이를 보장하기 위해 잠시 대기
        Thread.sleep(1)

        // when
        val updatedMetadata = metadata.updateTimestamp()

        // then
        assertThat(updatedMetadata.imageType).isEqualTo(metadata.imageType)
        assertThat(updatedMetadata.originalFileName).isEqualTo(metadata.originalFileName)
        assertThat(updatedMetadata.fileSize).isEqualTo(metadata.fileSize)
        assertThat(updatedMetadata.contentType).isEqualTo(metadata.contentType)
        assertThat(updatedMetadata.createdAt).isEqualTo(metadata.createdAt)
        assertThat(updatedMetadata.updatedAt).isAfter(originalUpdatedAt)
    }

    @Test
    @DisplayName("모든 ImageType에 대해 메타데이터를 생성할 수 있다")
    fun `모든 ImageType에 대해 메타데이터를 생성할 수 있다`() {
        // given & when & then
        ImageType.values().forEach { imageType ->
            val metadata = ImageMetadata.of(imageType)
            assertThat(metadata.imageType).isEqualTo(imageType)
        }
    }
}