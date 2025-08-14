package com.backend.immilog.image.domain.service

import com.backend.immilog.image.exception.ImageErrorCode
import com.backend.immilog.image.exception.ImageException
import org.springframework.web.multipart.MultipartFile

class ImageValidationService {

    companion object {
        private const val MAX_FILE_SIZE = 10 * 1024 * 1024L // 10MB
        private val ALLOWED_CONTENT_TYPES = setOf(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
        )
        private val ALLOWED_EXTENSIONS = setOf("jpg", "jpeg", "png", "gif", "webp")
    }

    fun validateImageFile(file: MultipartFile) {
        validateFileNotEmpty(file)
        validateFileSize(file)
        validateContentType(file)
        validateFileExtension(file)
    }

    fun validateImageFiles(files: List<MultipartFile>) {
        if (files.isEmpty()) {
            throw ImageException(ImageErrorCode.NO_FILES_PROVIDED)
        }

        files.forEach { file ->
            validateImageFile(file)
        }
    }

    private fun validateFileNotEmpty(file: MultipartFile) {
        if (file.isEmpty) {
            throw ImageException(ImageErrorCode.INVALID_IMAGE_FILE)
        }
    }

    private fun validateFileSize(file: MultipartFile) {
        if (file.size > MAX_FILE_SIZE) {
            throw ImageException(ImageErrorCode.IMAGE_FILE_TOO_LARGE)
        }
    }

    private fun validateContentType(file: MultipartFile) {
        val contentType = file.contentType
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.lowercase())) {
            throw ImageException(ImageErrorCode.UNSUPPORTED_IMAGE_FORMAT)
        }
    }

    private fun validateFileExtension(file: MultipartFile) {
        val fileName = file.originalFilename ?: ""
        val extension = fileName.substringAfterLast('.', "").lowercase()

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw ImageException(ImageErrorCode.UNSUPPORTED_IMAGE_FORMAT)
        }
    }
}