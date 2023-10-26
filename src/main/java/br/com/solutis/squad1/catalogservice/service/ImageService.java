package br.com.solutis.squad1.catalogservice.service;

import br.com.solutis.squad1.catalogservice.dto.image.ImageResponseDto;
import br.com.solutis.squad1.catalogservice.exception.EntityNotFoundException;
import br.com.solutis.squad1.catalogservice.exception.ImageException;
import br.com.solutis.squad1.catalogservice.mapper.ImageMapper;
import br.com.solutis.squad1.catalogservice.model.entity.Image;
import br.com.solutis.squad1.catalogservice.model.repository.ImageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);
    private final ImageRepository imageRepository;
    private final ProductService productService;
    private final ImageMapper mapper;

    @Value("${upload.dir}")
    private String uploadDir;

    @Value("${upload.url}")
    private String uploadUrl;

    private static String generateNewFilename(Long productId, MultipartFile file) {
        LOGGER.info("Generating new file name for product {}", productId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String formattedDate = LocalDateTime.now().format(formatter);
        return productId + "-" + formattedDate + Objects.requireNonNull(
                file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf('.')
        );
    }

    public ImageResponseDto findByProductId(Long id) {
        LOGGER.info("Finding image by product id {}", id);
        Image image = imageRepository.findByProductIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Product Image not found"));
        return mapper.toResponseDto(image);
    }

    public ImageResponseDto save(Long productId, MultipartFile file) {
        LOGGER.info("Saving product image with {}", productId);
        // Se tiver, deleta a imagem antiga do produto
        Optional<Image> image = imageRepository.findByProductIdAndDeletedIsFalse(productId);
        if (image.isPresent()) {
            LOGGER.info("Deleting old product image");
            delete(productId);
            LOGGER.info("Old product image deleted");
        }

        LOGGER.info("Uploading product image");
        ImageResponseDto productImage = upload(productId, file);
        LOGGER.info("Product image uploaded");

        LOGGER.info("Product image saved");
        Image savedImage = imageRepository.save(mapper.responseDtoToEntity(productImage));
        productService.saveImage(productId, savedImage);
        return mapper.toResponseDto(savedImage);
    }

    public void delete(Long productId) {
        LOGGER.info("Deleting product image with id {}", productId);
        ImageResponseDto imageResponseDto = findByProductId(productId);
        Image image = imageRepository.getReferenceById(imageResponseDto.id());
        image.delete();

        LOGGER.info("Deleting product image from product");
        productService.deleteImage(productId);

        LOGGER.info("Deleting product image from directory");
        deleteFile(imageResponseDto.archiveName());
    }

    public Resource load(String name) {
        try {
            LOGGER.info("Loading file {}", name);
            Path filePath = Paths.get(uploadDir).resolve(name);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }

            LOGGER.error("File not found: " + name);
            throw new FileNotFoundException("File not found: " + name);
        } catch (FileNotFoundException e) {
            throw new ImageException(e.getMessage());
        } catch (MalformedURLException e) {
            LOGGER.error("An error occurred while loading the file", e);
            throw new RuntimeException("An error occurred while loading the file", e);
        }
    }

    private ImageResponseDto upload(Long productId, MultipartFile file) {
        LOGGER.info("Uploading file with id {}", productId);
        // Valida se o arquivo é uma imagem
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new ImageException("File must be an image");
        }

        LOGGER.info("Uploading product image from product");
        // Upload da imagem
        String newFileName = generateNewFilename(productId, file);
        store(file, newFileName);

        LOGGER.info("Saving product image from directory");
        // Coloca os dados da imagem que foi feito o upload no objeto para retornar
        Image image = getProductImageFromFile(file, newFileName);
        return mapper.toResponseDto(image);
    }

    private Image getProductImageFromFile(MultipartFile file, String newFileName) {
        LOGGER.info("Getting product image from file");
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
            LOGGER.info("Storing file {}", fileName);
            Path targetPath = Paths.get(uploadDir).resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.error("An error occurred while storing the file", e);
            throw new ImageException("An error occurred while storing the file");
        }
    }

    private void deleteFile(String fileName) {
        try {
            LOGGER.info("Deleting file {}", fileName);
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            LOGGER.error("An error occurred while storing the file", e);
            throw new RuntimeException("An error occurred while deleting the file", e);
        }
    }
}
