package br.com.solutis.squad1.catalogservice.controller;

import br.com.solutis.squad1.catalogservice.dto.image.ImageResponseDto;
import br.com.solutis.squad1.catalogservice.dto.product.ProductPostDto;
import br.com.solutis.squad1.catalogservice.dto.product.ProductPutDto;
import br.com.solutis.squad1.catalogservice.dto.product.ProductResponseDto;
import br.com.solutis.squad1.catalogservice.exception.ImageException;
import br.com.solutis.squad1.catalogservice.service.ImageService;
import br.com.solutis.squad1.catalogservice.service.ProductService;
import jakarta.activation.MimetypesFileTypeMap;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ImageService imageService;

    @GetMapping
    public Page<ProductResponseDto> findAll(
            @RequestParam(required = false) String category,
            Pageable pageable
    ) {
        return productService.findAll(category, pageable);
    }

    @GetMapping("/{id}")
    public ProductResponseDto findById(
            @PathVariable Long id
    ) {
        return productService.findById(id);
    }

    @GetMapping("/search")
    public Page<ProductResponseDto> findProductsByName(
            @RequestParam String name,
            Pageable pageable
    ){
        return productService.findProductsByName(name, pageable);
    }

    @GetMapping("/sellers/{id}")
    public Page<ProductResponseDto> findBySellerId(
            @PathVariable Long id,
            @RequestParam(required = false) String category,
            Pageable pageable
    ) {
        return productService.findBySellerId(id, category, pageable);
    }

    @GetMapping("/images/{name}")
    public ResponseEntity<?> loadImage(@PathVariable String name) {
        Resource resource = imageService.load(name);

        if (resource == null) throw new ImageException("Photo not found");

        // Pegar o tipo de conteúdo do arquivo
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        String contentType = mimeTypesMap.getContentType(resource.getFilename());

        // Adicionar o tipo de conteúdo do arquivo no cabeçalho da resposta
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.parseMediaType(contentType).toString());

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponseDto save(
            @RequestBody ProductPostDto productPostDto
    ) {
        return productService.save(productPostDto);
    }

    @PostMapping(path = "{productId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ImageResponseDto uploadImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file
    ) {
        return imageService.save(productId, file);
    }

    @PutMapping("/{id}")
    public ProductResponseDto update(
            @PathVariable Long id,
            @RequestBody ProductPutDto productPutDto
    ) {
        return productService.update(id, productPutDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id
    ) {
        productService.delete(id);
    }

    @DeleteMapping("{productId}/images")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(
            @PathVariable Long productId
    ) {
        imageService.delete(productId);
    }
}
