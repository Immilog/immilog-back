package com.backend.immilog.image.application.service

import com.backend.immilog.image.domain.ImageRepository
import com.backend.immilog.image.domain.model.Image
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ImageCommandService(
    private val imageRepository: ImageRepository
) {
    @Transactional
    fun save(image: Image): Image {
        return imageRepository.save(image)
    }
    
    @Transactional
    fun delete(image: Image) {
        imageRepository.delete(image)
    }
}
