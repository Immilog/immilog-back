package com.backend.immilog.image.infrastructure.persistence.jpa

import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import com.backend.immilog.image.domain.enums.ImageStatus
import com.backend.immilog.image.domain.enums.ImageType
import com.backend.immilog.image.domain.model.Image
import com.backend.immilog.image.domain.model.ImageId
import com.backend.immilog.image.domain.model.ImageMetadata
import com.backend.immilog.image.domain.model.ImagePath
import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate

@DynamicUpdate
@Entity
@Table(name = "image")
open class ImageJpaEntity(
    @Id
    @Column(name = "image_id")
    open var id: String? = null,

    @Column(name = "path")
    open var path: String = "",

    @Column(name = "image_type")
    @Enumerated(EnumType.STRING)
    open var imageType: ImageType = ImageType.POST,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    open var status: ImageStatus = ImageStatus.NORMAL
) {
    @PrePersist
    fun generateId() {
        if (this.id == null) {
            this.id = NanoIdUtils.randomNanoId()
        }
    }

    companion object {
        fun from(image: Image): ImageJpaEntity =
            ImageJpaEntity(
                id = image.id?.value,
                path = image.path.value,
                imageType = image.metadata.imageType,
                status = image.status
            )
    }

    fun toDomain(): Image {
        val imagePath = if (this.path.isNotBlank()) {
            ImagePath.of(this.path)
        } else {
            ImagePath.of("default.jpg")
        }
        val metadata = ImageMetadata.of(
            imageType = this.imageType,
            originalFileName = null,
            fileSize = null,
            contentType = null
        )

        return if (this.id != null) {
            Image.restore(
                id = ImageId.of(this.id!!),
                path = imagePath,
                metadata = metadata,
                status = this.status
            )
        } else {
            Image.create(
                path = imagePath,
                metadata = metadata
            )
        }
    }
}
