package com.backend.immilog.image.application.usecase

import com.backend.immilog.image.application.service.ImageCommandService
import com.backend.immilog.image.application.service.ImageQueryService
import com.backend.immilog.image.domain.enums.ImageStatus
import com.backend.immilog.image.domain.enums.ImageType
import com.backend.immilog.image.domain.model.Image
import com.backend.immilog.image.domain.model.ImageId
import com.backend.immilog.image.domain.model.ImageMetadata
import com.backend.immilog.image.domain.model.ImagePath
import com.backend.immilog.image.infrastructure.gateway.FileStorageHandler
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@ExtendWith(MockitoExtension::class)
@DisplayName("UploadImageUseCase 테스트")
class UploadImageUseCaseTest {

    @Mock
    private lateinit var fileStorageHandler: FileStorageHandler

    @Mock
    private lateinit var imageCommandService: ImageCommandService

    @Mock
    private lateinit var imageQueryService: ImageQueryService
    private lateinit var uploadImageUseCase: UploadImageUseCase.ImageUploader

    @BeforeEach
    fun setUp() {
        val mockRequest = MockHttpServletRequest().apply {
            scheme = "http"
            serverName = "localhost"
            serverPort = 8080
            contextPath = ""
        }
        val requestAttributes = ServletRequestAttributes(mockRequest)
        RequestContextHolder.setRequestAttributes(requestAttributes)

        uploadImageUseCase = UploadImageUseCase.ImageUploader(
            fileStorageHandler,
            imageCommandService,
            imageQueryService
        )
    }

    @Test
    @DisplayName("이미지를 업로드한다")
    fun `이미지를 업로드한다`() {
        // given
        val file = MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test content".toByteArray()
        )
        val files = listOf(file)
        val imagePath = "/images"
        val imageType = ImageType.PROFILE
        val uploadedUrl = "/images/uploaded/test.jpg"
        val testImage = createTestImage()

        `when`(fileStorageHandler.uploadFile(file, imagePath)).thenReturn(uploadedUrl)
        lenient().`when`(imageCommandService.save(testImage)).thenReturn(testImage)

        // when
        val result = uploadImageUseCase.uploadImages(files, imagePath, imageType)

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0]).contains(uploadedUrl)
        verify(fileStorageHandler).uploadFile(file, imagePath)
    }

    @Test
    @DisplayName("빈 파일 목록으로 업로드시 빈 목록을 반환한다")
    fun `빈 파일 목록으로 업로드시 빈 목록을 반환한다`() {
        // given
        val files = emptyList<MockMultipartFile>()
        val imagePath = "/images"
        val imageType = ImageType.PROFILE

        // when
        val result = uploadImageUseCase.uploadImages(files, imagePath, imageType)

        // then
        assertThat(result).isEmpty()
    }

    @Test
    @DisplayName("이전 이미지와 새 이미지가 다를 때 이전 이미지를 삭제한다")
    fun `이전 이미지와 새 이미지가 다를 때 이전 이미지를 삭제한다`() {
        // given
        val previousPath = "/images/old.jpg"
        val newPath = "/images/new.jpg"
        val originalImage = createTestImage()

        `when`(imageQueryService.getImageByPath(previousPath)).thenReturn(originalImage)

        // when
        uploadImageUseCase.deleteImage(previousPath, newPath)

        // then
        verify(fileStorageHandler).deleteFile(previousPath)
        verify(imageQueryService).getImageByPath(previousPath)
    }

    @Test
    @DisplayName("이전 이미지가 null이면 삭제하지 않는다")
    fun `이전 이미지가 null이면 삭제하지 않는다`() {
        // given
        val previousPath: String? = null
        val newPath = "/images/new.jpg"

        // when
        uploadImageUseCase.deleteImage(previousPath, newPath)

        // then
        verifyNoInteractions(fileStorageHandler)
        verifyNoInteractions(imageQueryService)
        verifyNoInteractions(imageCommandService)
    }

    @Test
    @DisplayName("이전 이미지와 새 이미지가 같으면 삭제하지 않는다")
    fun `이전 이미지와 새 이미지가 같으면 삭제하지 않는다`() {
        // given
        val previousPath = "/images/same.jpg"
        val newPath = "/images/same.jpg"

        // when
        uploadImageUseCase.deleteImage(previousPath, newPath)

        // then
        verifyNoInteractions(fileStorageHandler)
        verifyNoInteractions(imageQueryService)
        verifyNoInteractions(imageCommandService)
    }

    private fun createTestImage(): Image {
        val imagePath = ImagePath.of("/images/test.jpg")
        val imageMetadata = ImageMetadata.of(ImageType.PROFILE)
        return Image.restore(
            id = ImageId.of("test-id"),
            path = imagePath,
            metadata = imageMetadata,
            status = ImageStatus.NORMAL
        )
    }
}