package br.com.solutis.squad1.catalogservice.service;

import br.com.solutis.squad1.catalogservice.dto.image.ImageResponseDto;
import br.com.solutis.squad1.catalogservice.exception.BadRequestException;
import br.com.solutis.squad1.catalogservice.exception.EntityNotFoundException;
import br.com.solutis.squad1.catalogservice.mapper.ImageMapper;
import br.com.solutis.squad1.catalogservice.model.entity.Image;
import br.com.solutis.squad1.catalogservice.model.repository.ImageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

/**
 * Service class that provides business logic for image-related operations.
 *
 * This service handles operations such as retrieving, saving, updating, and deleting images.
 *
 * All operations are transactional, ensuring data consistency and integrity.
 *
 * This service interacts with the {@link ImageRepository} for database access,
 * {@link ProductService} for handling product-related operations, and {@link ImageMapper} for mapping between DTOs and entities.
 *
 * @Service Indicates that this class is a Spring service bean.
 * @Transactional Specifies that the methods of this service are transactional.
 * @RequiredArgsConstructor Lombok annotation that generates a constructor with required fields.
 * @Slf4j Lombok annotation that generates a logger field for logging.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private final ImageRepository imageRepository;
    private final ProductService productService;
    private final ImageMapper mapper;

    @Value("${upload.dir}")
    private String uploadDir;

    @Value("${upload.url}")
    private String uploadUrl;

    private static String generateNewFilename(Long productId, MultipartFile file) {
        log.info("Generating new file name for product {}", productId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String formattedDate = LocalDateTime.now().format(formatter);
        return productId + "-" + formattedDate + Objects.requireNonNull(
                file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf('.')
        );
    }

    /**
     * Find image by product id
     *
     * @param id
     * @return ImageResponseDto
     */
    public ImageResponseDto findByProductId(Long id) {
        log.info("Finding image by product id {}", id);
        Image image = imageRepository.findByProductIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Product Image not found"));
        return mapper.toResponseDto(image);
    }

    /**
     * Save image
     *
     * @param productId
     * @param file
     * @return ImageResponseDto
     */
    public ImageResponseDto save(Long productId, MultipartFile file) {
        log.info("Saving product image with {}", productId);
        // Se tiver, deleta a imagem antiga do produto
        Optional<Image> image = imageRepository.findByProductIdAndDeletedIsFalse(productId);
        if (image.isPresent()) {
            log.info("Deleting old product image");
            delete(productId);
            log.info("Old product image deleted");
        }

        log.info("Uploading product image");
        ImageResponseDto productImage = upload(productId, file);
        log.info("Product image uploaded");

        log.info("Product image saved");
        Image savedImage = imageRepository.save(mapper.responseDtoToEntity(productImage));
        productService.saveImage(productId, savedImage);
        return mapper.toResponseDto(savedImage);
    }

    /**
     * Delete image
     *
     * @param productId
     * @return void
     */
    public void delete(Long productId) {
        log.info("Deleting product image with id {}", productId);
        ImageResponseDto imageResponseDto = findByProductId(productId);
        Image image = imageRepository.getReferenceById(imageResponseDto.id());
        image.delete();

        log.info("Deleting product image from product");
        productService.deleteImage(productId);

        log.info("Deleting product image from directory");
        deleteFile(imageResponseDto.archiveName());
    }

    /**
     * Load image
     *
     * @param name
     * @return Resource
     */
    public Resource load(String name) {
        try {
            log.info("Loading file {}", name);
            Path filePath = Paths.get(uploadDir).resolve(name);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }

            log.error("File not found: " + name);
            throw new FileNotFoundException("File not found: " + name);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        } catch (MalformedURLException e) {
            log.error("An error occurred while loading the file", e);
            throw new RuntimeException("An error occurred while loading the file", e);
        }
    }

    private ImageResponseDto upload(Long productId, MultipartFile file) {
        log.info("Uploading file with id {}", productId);
        // Valida se o arquivo é uma imagem
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new BadRequestException("File must be an image");
        }

        log.info("Uploading product image from product");
        // Upload da imagem
        String newFileName = generateNewFilename(productId, file);
        store(file, newFileName);

        log.info("Saving product image from directory");
        // Coloca os dados da imagem que foi feito o upload no objeto para retornar
        Image image = getProductImageFromFile(file, newFileName);
        return mapper.toResponseDto(image);
    }

    private Image getProductImageFromFile(MultipartFile file, String newFileName) {
        log.info("Getting product image from file");
        Image image = new Image();
        image.setOriginalName(file.getOriginalFilename());
        image.setArchiveName(newFileName);
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setUrl(uploadUrl + newFileName);
        return image;
    }

    private void store(MultipartFile file, String fileName) {
        try {
            log.info("Storing file {}", fileName);
            Path targetPath = Paths.get(uploadDir).resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("An error occurred while storing the file", e);
            throw new RuntimeException("An error occurred while storing the file", e);
        }
    }

    private void deleteFile(String fileName) {
        try {
            log.info("Deleting file {}", fileName);
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("An error occurred while storing the file", e);
            throw new RuntimeException("An error occurred while deleting the file", e);
        }
    }
}
