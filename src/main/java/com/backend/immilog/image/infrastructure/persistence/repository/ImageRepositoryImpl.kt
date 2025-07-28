package com.backend.immilog.image.infrastructure.persistence.repository

import com.backend.immilog.image.domain.ImageRepository
import com.backend.immilog.image.domain.ImageType
import com.backend.immilog.image.domain.model.Image
import com.backend.immilog.image.infrastructure.persistence.jpa.ImageJpaEntity
import com.backend.immilog.image.infrastructure.persistence.jpa.ImageJpaRepository
import org.springframework.stereotype.Repository

@Repository
class ImageRepositoryImpl(
    private val imageJpaRepository: ImageJpaRepository
) : ImageRepository {
    override fun save(image: Image): Image {
        return imageJpaRepository.save(ImageJpaEntity.from(image)).toDomain()
    }

    override fun findByPath(imagePath: String): Image? {
        return imageJpaRepository.findByPath(imagePath)
            ?.toDomain()
            ?: throw IllegalArgumentException("Image with path $imagePath not found")
    }

    override fun findById(imageId: Long): Image? {
        return imageJpaRepository.findById(imageId).orElse(null)?.toDomain()
    }

    override fun findByImageType(imageType: ImageType): List<Image> {
        return imageJpaRepository.findByImageType(imageType).map { it.toDomain() }
    }

    override fun existsByPath(imagePath: String): Boolean {
        return imageJpaRepository.existsByPath(imagePath)
    }

    override fun delete(image: Image) {
        image.id?.let { id ->
            imageJpaRepository.deleteById(id.value)
        }
    }
}
