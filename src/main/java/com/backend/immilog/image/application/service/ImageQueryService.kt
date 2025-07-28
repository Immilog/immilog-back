package com.backend.immilog.image.application.service

import com.backend.immilog.image.domain.ImageRepository
import com.backend.immilog.image.domain.ImageType
import com.backend.immilog.image.domain.model.Image
import com.backend.immilog.image.domain.model.ImageId
import com.backend.immilog.image.exception.ImageErrorCode
import com.backend.immilog.image.exception.ImageException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ImageQueryService(
    private val imageRepository: ImageRepository
) {
    @Transactional(readOnly = true)
    fun getImageByPath(imagePath: String): Image {
        return imageRepository.findByPath(imagePath)
            ?: throw ImageException(ImageErrorCode.IMAGE_NOT_FOUND)
    }
    
    @Transactional(readOnly = true)
    fun getImageById(imageId: ImageId): Image {
        return imageRepository.findById(imageId.value)
            ?: throw ImageException(ImageErrorCode.IMAGE_NOT_FOUND)
    }
    
    @Transactional(readOnly = true)
    fun getImagesByType(imageType: ImageType): List<Image> {
        return imageRepository.findByImageType(imageType)
    }
    
    @Transactional(readOnly = true)
    fun existsByPath(imagePath: String): Boolean {
        return imageRepository.existsByPath(imagePath)
    }
}
