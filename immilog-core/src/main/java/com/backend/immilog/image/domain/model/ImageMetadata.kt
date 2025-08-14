package com.backend.immilog.image.domain.model

import com.backend.immilog.image.domain.enums.ImageType
import java.time.LocalDateTime

data class ImageMetadata(
    val imageType: ImageType,
    val originalFileName: String?,
    val fileSize: Long?,
    val contentType: String?,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun of(
            imageType: ImageType,
            originalFileName: String? = null,
            fileSize: Long? = null,
            contentType: String? = null
        ): ImageMetadata {
            return ImageMetadata(
                imageType = imageType,
                originalFileName = originalFileName,
                fileSize = fileSize,
                contentType = contentType
            )
        }
    }

    fun updateTimestamp(): ImageMetadata = copy(updatedAt = LocalDateTime.now())
}