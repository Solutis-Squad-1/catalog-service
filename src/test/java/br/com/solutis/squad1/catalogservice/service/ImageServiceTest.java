package br.com.solutis.squad1.catalogservice.service;

import br.com.solutis.squad1.catalogservice.dto.image.ImageResponseDto;
import br.com.solutis.squad1.catalogservice.exception.EntityNotFoundException;
import br.com.solutis.squad1.catalogservice.mapper.ImageMapper;
import br.com.solutis.squad1.catalogservice.model.builder.ImageBuilder;
import br.com.solutis.squad1.catalogservice.model.entity.Image;
import br.com.solutis.squad1.catalogservice.model.repository.ImageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;
    @Mock
    private ProductService productService;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private ImageMapper mapper;
    @Value("${upload.dir}")
    private String uploadDir;

    @Test
    @DisplayName("Finds image by product id")
    void findByProductId_ShouldFindImageByProductId() {
        Long productId = 1L;
        Image image = createImage();
        ImageResponseDto expectedDto = createImageResponseDto();

        when(imageRepository.findByProductIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(image));
        when(mapper.toResponseDto(image)).thenReturn(expectedDto);

        ImageResponseDto result = imageService.findByProductId(productId);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(imageRepository).findByProductIdAndDeletedIsFalse(productId);
        verify(mapper).toResponseDto(image);
    }

    @Test
    @DisplayName("Throws EntityNotFoundException when image not found")
    void findByProductId_ShouldThrowEntityNotFoundExceptionWhenImageNotFound() {
        Long productId = 1L;

        when(imageRepository.findByProductIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> imageService.findByProductId(productId));
        verify(imageRepository).findByProductIdAndDeletedIsFalse(productId);
        verifyNoInteractions(mapper);
    }

    @Test
    void save() {
    }

    @Test
    void delete(){
    }

    @Test
    @DisplayName("Returns RuntimeException when the file is not found")
    void load_NonExistingFile() {
        String fileName = "nonExistingFile.txt";

        assertThrows(RuntimeException.class, () -> imageService.load(fileName));
    }

    @Test
    @DisplayName("Returns RuntimeException when file with a malformed URL")
    void load_FileWithMalformedUrl() {
        String fileName = "malformedFileName.txt";

        assertThrows(RuntimeException.class, () -> imageService.load(fileName));
    }

    private Image createImage() {
        ImageBuilder builder = new ImageBuilder();

        return builder
                .id(1L)
                .archiveName("Archive name")
                .originalName("Original name")
                .contentType("Content type")
                .size(1L)
                .deleted(false)
                .url("url")
                .createdAt(LocalDateTime.now())
                .deletedAt(null)
                .build();
    }

    private ImageResponseDto createImageResponseDto() {
        ImageBuilder builder = new ImageBuilder();

        return builder
                .id(1L)
                .archiveName("Archive name")
                .originalName("Original name")
                .contentType("Content type")
                .size(1L)
                .url("url")
                .buildImageResponseDto();
    }
}