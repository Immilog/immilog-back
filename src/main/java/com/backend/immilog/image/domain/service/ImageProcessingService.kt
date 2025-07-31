package com.backend.immilog.image.domain.service

import com.backend.immilog.image.domain.enums.ImageType
import com.backend.immilog.image.domain.model.Image
import com.backend.immilog.image.domain.model.ImageMetadata
import com.backend.immilog.image.domain.model.ImagePath
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

class ImageProcessingService {

    fun createImageFromFile(
        file: MultipartFile,
        storedPath: String,
        imageType: ImageType
    ): Image {
        val imagePath = ImagePath.of(storedPath)
        val metadata = ImageMetadata.of(
            imageType = imageType,
            originalFileName = file.originalFilename,
            fileSize = file.size,
            contentType = file.contentType
        )

        return Image.create(imagePath, metadata)
    }

    fun buildFullImageUrl(relativePath: String): String {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
            .path(relativePath)
            .build()
            .toUriString()
    }

    fun generateImagePathPrefix(imageType: ImageType): String {
        return when (imageType) {
            ImageType.PROFILE -> "/images/profiles"
            ImageType.POST -> "/images/posts"
            ImageType.JOB_POST -> "/images/job-posts"
        }
    }
}