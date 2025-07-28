package com.backend.immilog.image.domain.model

data class ImageId(val value: Long) {
    companion object {
        fun of(value: Long?): ImageId {
            require(value != null && value > 0) { "ImageId value must be positive" }
            return ImageId(value)
        }
        
        fun generate(): ImageId? = null
    }
}