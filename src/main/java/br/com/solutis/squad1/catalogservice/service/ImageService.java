package br.com.solutis.squad1.catalogservice.service;

import br.com.solutis.squad1.catalogservice.dto.image.ImageResponseDto;
import br.com.solutis.squad1.catalogservice.exception.EntityNotFoundException;
import br.com.solutis.squad1.catalogservice.exception.ImageException;
import br.com.solutis.squad1.catalogservice.mapper.ImageMapper;
import br.com.solutis.squad1.catalogservice.model.entity.Image;
import br.com.solutis.squad1.catalogservice.model.repository.ImageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final ProductService productService;
    private final ImageMapper mapper;

    @Value("${upload.dir}")
    private String uploadDir;

    @Value("${upload.url}")
    private String uploadUrl;

    public Page<ImageResponseDto> findAll(Pageable pageable) {
        return imageRepository.findAllByDeletedFalse(pageable).map(mapper::toResponseDto);
    }

    public ImageResponseDto findById(Long id) {
        Image image = imageRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Product Image not found"));
        return mapper.toResponseDto(image);
    }

    public ImageResponseDto findByProductId(Long id) {
        Image image = imageRepository.findByProductIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Product Image not found"));
        return mapper.toResponseDto(image);
    }

    public ImageResponseDto save(Long productId, MultipartFile file) {
        // Se tiver, deleta a imagem antiga do produto
        Optional<Image> image = imageRepository.findByProductIdAndDeletedIsFalse(productId);
        if (image.isPresent()) {
            delete(productId);
        }

        ImageResponseDto productImage = upload(productId, file);

        Image savedImage = imageRepository.save(mapper.responseDtoToEntity(productImage));
        productService.saveImage(productId, savedImage);
        return mapper.toResponseDto(savedImage);
    }

    public void delete(Long productId) {
        ImageResponseDto imageResponseDto = findByProductId(productId);
        Image image = imageRepository.getReferenceById(imageResponseDto.id());
        image.delete();

        // Deletar a imagem do produto
        productService.deleteImage(productId);

        // Deleta a imagem do diretório
        deleteFile(imageResponseDto.archiveName());
    }

    public Resource load(String name) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(name);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }

            throw new FileNotFoundException("File not found: " + name);
        } catch (FileNotFoundException e) {
            throw new ImageException(e.getMessage());
        } catch (MalformedURLException e) {
            throw new RuntimeException("An error occurred while loading the file", e);
        }
    }

    private ImageResponseDto upload(Long productId, MultipartFile file) {
        // Valida se o arquivo é uma imagem
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new ImageException("File must be an image");
        }

        // Upload da imagem
        String newFileName = generateNewFilename(productId, file);
        store(file, newFileName);

        // Coloca os dados da imagem que foi feito o upload no objeto para retornar
        Image image = getProductImageFromFile(file, newFileName);

        return mapper.toResponseDto(image);
    }

    private static String generateNewFilename(Long productId, MultipartFile file) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String formattedDate = LocalDateTime.now().format(formatter);
        return productId + "-" + formattedDate + Objects.requireNonNull(
                file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf('.')
        );
    }

    private Image getProductImageFromFile(MultipartFile file, String newFileName) {
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
            Path targetPath = Paths.get(uploadDir).resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ImageException("An error occurred while storing the file");
        }
    }

    private void deleteFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while deleting the file", e);
        }
    }
}
