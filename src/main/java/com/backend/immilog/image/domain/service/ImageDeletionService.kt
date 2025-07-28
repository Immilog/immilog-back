package com.backend.immilog.image.domain.service

import com.backend.immilog.image.domain.model.Image
import com.backend.immilog.image.domain.model.ImagePath
import com.backend.immilog.image.exception.ImageErrorCode
import com.backend.immilog.image.exception.ImageException
import org.springframework.stereotype.Service

@Service
class ImageDeletionService {
    
    fun validateImageCanBeDeleted(image: Image) {
        if (image.isDeleted()) {
            throw ImageException(ImageErrorCode.IMAGE_ALREADY_DELETED)
        }
    }
    
    fun shouldDeletePreviousImage(previousPath: String?, newPath: String?): Boolean {
        return !previousPath.isNullOrBlank() && previousPath != newPath
    }
    
    fun extractImagePathFromUrl(fullUrl: String): String {
        // URL에서 실제 파일 경로 추출
        return fullUrl.substringAfter("/images/")
    }
    
    fun markImageAsDeleted(image: Image): Image {
        validateImageCanBeDeleted(image)
        return image.delete()
    }
}