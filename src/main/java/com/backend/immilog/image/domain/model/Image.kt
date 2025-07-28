package com.backend.immilog.image.domain.model

import com.backend.immilog.image.domain.ImageStatus

data class Image(
    val id: ImageId?,
    val path: ImagePath,
    val metadata: ImageMetadata,
    val status: ImageStatus
) {
    companion object {
        fun create(
            path: ImagePath,
            metadata: ImageMetadata
        ): Image {
            return Image(
                id = null,
                path = path,
                metadata = metadata,
                status = ImageStatus.NORMAL
            )
        }
        
        fun restore(
            id: ImageId,
            path: ImagePath,
            metadata: ImageMetadata,
            status: ImageStatus
        ): Image {
            return Image(
                id = id,
                path = path,
                metadata = metadata,
                status = status
            )
        }
    }
    
    fun delete(): Image = copy(
        status = ImageStatus.DELETED,
        metadata = metadata.updateTimestamp()
    )
    
    fun activate(): Image = copy(
        status = ImageStatus.NORMAL,
        metadata = metadata.updateTimestamp()
    )
    
    fun isDeleted(): Boolean = status == ImageStatus.DELETED
    
    fun isActive(): Boolean = status == ImageStatus.NORMAL
    
    fun updatePath(newPath: ImagePath): Image = copy(
        path = newPath,
        metadata = metadata.updateTimestamp()
    )
}