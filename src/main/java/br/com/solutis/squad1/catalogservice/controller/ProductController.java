package br.com.solutis.squad1.catalogservice.controller;

import br.com.solutis.squad1.catalogservice.dto.image.ImageResponseDto;
import br.com.solutis.squad1.catalogservice.dto.product.ProductPostDto;
import br.com.solutis.squad1.catalogservice.dto.product.ProductPutDto;
import br.com.solutis.squad1.catalogservice.dto.product.ProductResponseDto;
import br.com.solutis.squad1.catalogservice.service.ImageService;
import br.com.solutis.squad1.catalogservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

/**
 * Controller class that handles HTTP requests related to product operations.
 *
 * This controller provides endpoints for retrieving, creating, updating, and deleting products. It also includes
 * endpoints for managing product images, such as uploading and deleting images associated with a product.
 *
 * Multiple endpoints are secured, and users must have specific authorities to perform certain operations.
 *
 * This controller interacts with the {@link ProductService} for product-related business logic
 * and the {@link ImageService} for image-related operations.
 *
 *  Swagger annotations are used for API documentation. Each method is annotated with {@link Operation}, {@link ApiResponse}, and {@link ApiResponses}
 *  to provide clear and standardized documentation.
 *
 * @RestController Indicates that this class is a controller where request handling methods are defined.
 * @RequestMapping("/api/v1/catalog/products") Maps all endpoints in this controller to the specified base path.
 * @RequiredArgsConstructor Lombok annotation that generates a constructor with required fields.
 */
@RestController
@RequestMapping("/api/v1/catalog/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ImageService imageService;

    /**
     * Find all products
     *
     * @param name
     * @param category
     * @param pageable
     * @return Page<ProductResponseDto>
     */
    @Operation(summary = "Find all products")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of products",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductResponseDto.class))))
    @GetMapping
    public Page<ProductResponseDto> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            Pageable pageable
    ) {
        return productService.findAll(name, category, pageable);
    }

    /**
     * Find products by seller id
     *
     * @param id
     * @param name
     * @param category
     * @param pageable
     * @return Page<ProductResponseDto>
     */
    @Operation(summary = "Find products by seller id")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of products by seller ID",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductResponseDto.class))))
    @GetMapping("/sellers/{id}")
    public Page<ProductResponseDto> findBySellerId(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            Pageable pageable
    ) {
        return productService.findBySellerId(id, name, category, pageable);
    }

    /**
     * Find product by id
     *
     * @param id
     * @return ProductResponseDto
     */
    @Operation(summary = "Find product by id")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of products",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductResponseDto.class))))
    @GetMapping("/{id}")
    public ProductResponseDto findById(
            @PathVariable Long id
    ) {
        return productService.findById(id);
    }

    /**
     * Load image by product name
     *
     * @param name
     * @return ResponseEntity<?>
     */
    @Operation(summary = "Load image by product name")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the image",
            content = @Content(mediaType = "application/octet-stream"))
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

    /**
     * Find products by list of ids
     *
     * @param productsId
     * @return List<ProductResponseDto>
     */
    @Operation(summary = "Find products by list of ids")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of products",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductResponseDto.class))))
    @GetMapping("/cart")
    public List<ProductResponseDto> findProductsByUser(@RequestBody List<Long> productsId) {
        return productService.findProductsList(productsId);
    }

    /**
     * Save product
     *
     * @param productPostDto
     * @return ProductResponseDto
     */
    @Operation(summary = "Save a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product successfully created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Authenticated user without access permission")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('product:create')")
    public ProductResponseDto save(
            @RequestBody ProductPostDto productPostDto
    ) {
        return productService.save(productPostDto);
    }

    /**
     * Upload image to product
     *
     * @param productId
     * @param file
     * @return ImageResponseDto
     */
    @Operation(summary = "Upload image to product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Image successfully uploaded",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ImageResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Authenticated user without access permission")
    })
    @PostMapping(path = "{productId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('product:create:image')")
    public ImageResponseDto uploadImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file
    ) {
        return imageService.save(productId, file);
    }

    /**
     * Update product
     *
     * @param id
     * @param productPutDto
     * @return ProductResponseDto
     */
    @Operation(summary = "Update product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Authenticated user without access permission")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('product:update')")
    public ProductResponseDto update(
            @PathVariable Long id,
            @RequestBody ProductPutDto productPutDto
    ) {
        return productService.update(id, productPutDto);
    }

    /**
     * Delete product
     *
     * @param id
     */
    @Operation(summary = "Delete product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Authenticated user without access permission")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('product:delete')")
    public void delete(
            @PathVariable Long id
    ) {
        productService.delete(id);
    }

    /**
     * Delete image from product
     *
     * @param productId
     */
    @Operation(summary = "Delete image from product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Image deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Authenticated user without access permission")
    })
    @DeleteMapping("{productId}/images")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('product:delete:image')")
    public void deleteImage(
            @PathVariable Long productId
    ) {
        imageService.delete(productId);
    }
}
