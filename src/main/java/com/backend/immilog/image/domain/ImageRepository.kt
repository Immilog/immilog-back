package com.backend.immilog.image.domain

import com.backend.immilog.image.domain.model.Image

interface ImageRepository {
    fun save(image: Image): Image
    fun findByPath(imagePath: String): Image?
    fun findById(imageId: Long): Image?
    fun findByImageType(imageType: ImageType): List<Image>
    fun existsByPath(imagePath: String): Boolean
    fun delete(image: Image)
}
