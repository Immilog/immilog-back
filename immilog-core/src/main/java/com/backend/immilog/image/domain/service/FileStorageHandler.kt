package com.backend.immilog.image.domain.service

import org.springframework.web.multipart.MultipartFile

interface FileStorageHandler {
    fun uploadFile(file: MultipartFile, imagePath: String): String
    fun deleteFile(imagePath: String?)
}
