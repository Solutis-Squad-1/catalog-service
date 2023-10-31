package br.com.solutis.squad1.catalogservice.controller;

import br.com.solutis.squad1.catalogservice.dto.image.ImageResponseDto;
import br.com.solutis.squad1.catalogservice.dto.product.ProductPostDto;
import br.com.solutis.squad1.catalogservice.dto.product.ProductPutDto;
import br.com.solutis.squad1.catalogservice.dto.product.ProductResponseDto;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalog/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ImageService imageService;

    @GetMapping
    public Page<ProductResponseDto> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            Pageable pageable
    ) {
        return productService.findAll(name, category, pageable);
    }

    @GetMapping("/sellers/{id}")
    public Page<ProductResponseDto> findBySellerId(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            Pageable pageable
    ) {
        return productService.findBySellerId(id, name, category, pageable);
    }

    @GetMapping("/{id}")
    public ProductResponseDto findById(
            @PathVariable Long id
    ) {
        return productService.findById(id);
    }

    @GetMapping("/images/{name}")
    public ResponseEntity<?> loadImage(@PathVariable String name) {
        Resource resource = imageService.load(name);

        // Pegar o tipo de conteúdo do arquivo
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        String contentType = mimeTypesMap.getContentType(resource.getFilename());

        // Adicionar o tipo de conteúdo do arquivo no cabeçalho da resposta
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.parseMediaType(contentType).toString());

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @GetMapping("/cart")
    public List<ProductResponseDto> findProductsByUser(@RequestBody List<Long> productsId) {
        return productService.findProductsList(productsId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('product:create')")
    public ProductResponseDto save(
            @RequestBody ProductPostDto productPostDto
    ) {
        return productService.save(productPostDto);
    }

    @PostMapping(path = "{productId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('product:create:image')")
    public ImageResponseDto uploadImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file
    ) {
        return imageService.save(productId, file);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('product:update')")
    public ProductResponseDto update(
            @PathVariable Long id,
            @RequestBody ProductPutDto productPutDto
    ) {
        return productService.update(id, productPutDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('product:delete')")
    public void delete(
            @PathVariable Long id
    ) {
        productService.delete(id);
    }

    @DeleteMapping("{productId}/images")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('product:delete:image')")
    public void deleteImage(
            @PathVariable Long productId
    ) {
        imageService.delete(productId);
    }
}
