package com.backend.immilog.global.presentation.controller;

import com.backend.immilog.global.application.ImageService;
import com.backend.immilog.global.presentation.request.ImageRequest;
import com.backend.immilog.global.presentation.response.GlobalApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@DisplayName("이미지 컨트롤러 테스트")
class ImageControllerTest {
    @Mock
    private ImageService imageService;
    private ImageController imageController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        imageController = new ImageController(imageService);
    }

    @Test
    @DisplayName("이미지 업로드")
    void uploadImage() {
        // given
        List<MultipartFile> files = List.of(mock(MultipartFile.class));
        String imagePath = "imagePath";
        List<String> imageDTO = List.of("imageUrl");
        when(imageService.saveFiles(files, imagePath))
                .thenReturn(imageDTO);
        // when
        ResponseEntity<GlobalApiResponse> response =
                imageController.uploadImage(files, imagePath);
        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
        List<String> data = (List<String>) Objects.requireNonNull(response.getBody()).data();
        assertThat(data.get(0)).isEqualTo("imageUrl");
    }

    @Test
    @DisplayName("이미지 삭제")
    void deleteImage() {
        // given
        String imagePath = "imagePath";
        ImageRequest param = ImageRequest.builder()
                .imageDirectory("directory")
                .imagePath(imagePath)
                .build();
        // when
        ResponseEntity<GlobalApiResponse> response = imageController.deleteImage(param);
        // then
        verify(imageService, times(1)).deleteFile(imagePath);
        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
    }
}