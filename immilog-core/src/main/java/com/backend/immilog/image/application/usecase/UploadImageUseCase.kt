package com.backend.immilog.image.application.usecase

import com.backend.immilog.image.application.service.ImageCommandService
import com.backend.immilog.image.application.service.ImageQueryService
import com.backend.immilog.image.domain.enums.ImageType
import com.backend.immilog.image.domain.model.Image
import com.backend.immilog.image.domain.model.ImageMetadata
import com.backend.immilog.image.domain.model.ImagePath
import com.backend.immilog.image.domain.service.FileStorageHandler
import com.backend.immilog.shared.application.event.DomainEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

interface UploadImageUseCase {
    fun uploadImages(
        files: List<MultipartFile>,
        imagePath: String,
        imageType: ImageType
    ): List<String>

    fun deleteImage(previousPath: String?, newPath: String?)

    @Service
    class ImageUploader(
        private val fileStorageHandler: FileStorageHandler,
        private val imageCommandService: ImageCommandService,
        private val imageQueryService: ImageQueryService,
        private val domainEventPublisher: DomainEventPublisher
    ) : UploadImageUseCase {

        @Transactional
        override fun uploadImages(
            files: List<MultipartFile>,
            imagePath: String,
            imageType: ImageType
        ): List<String> {
            try {
                val result = files.takeIf { it.isNotEmpty() }?.map { file ->
                    val url = fileStorageHandler.uploadFile(file, imagePath)
                    val fullPath = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path(url)
                        .build()
                        .toUriString()

                    val path = ImagePath.of(fullPath)
                    val metadata = ImageMetadata.of(
                        imageType = imageType,
                        originalFileName = file.originalFilename,
                        fileSize = file.size,
                        contentType = file.contentType
                    )
                    val image = Image.create(path, metadata)
                    imageCommandService.save(image)
                    fullPath
                }
                return result ?: emptyList()
            } finally {
                domainEventPublisher.publishEvents()
            }
        }

        @Transactional
        override fun deleteImage(previousPath: String?, newPath: String?) {
            try {
                if (!previousPath.isNullOrBlank() && previousPath != newPath) {
                    fileStorageHandler.deleteFile(previousPath)
                    val image = imageQueryService.getImageByPath(previousPath)
                    val deletedImage = image.delete()
                    imageCommandService.save(deletedImage)
                }
            } finally {
                domainEventPublisher.publishEvents()
            }
        }
    }
}