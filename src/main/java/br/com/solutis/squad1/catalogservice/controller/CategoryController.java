package br.com.solutis.squad1.catalogservice.controller;

import br.com.solutis.squad1.catalogservice.dto.category.CategoryDto;
import br.com.solutis.squad1.catalogservice.dto.category.CategoryResponseDto;
import br.com.solutis.squad1.catalogservice.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class that handles HTTP requests related to categories in the catalog service.
 *
 * This controller provides endpoints for retrieving, creating, updating, and deleting categories.
 *
 * The controller interacts with the {@link CategoryService} to perform business logic related to categories.
 *
 * Swagger annotations are used for API documentation. Each method is annotated with {@link Operation}, {@link ApiResponse}, and {@link ApiResponses}
 * to provide clear and standardized documentation.
 *
 * @RestController Indicates that this class is a controller where request handling methods are defined.
 * @RequestMapping("/api/v1/catalog/categories") Maps all endpoints in this controller to the specified base path.
 * @RequiredArgsConstructor Lombok annotation that generates a constructor with required fields.
 * */
@RestController
@RequestMapping("/api/v1/catalog/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    /**
     * Find all categories
     *
     * @param pageable
     * @return Page<CategoryResponseDto>
     */
    @Operation(summary = "Find all categories")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of categories", content = { @Content(mediaType = "application/json",
            schema = @Schema(implementation = Page.class, subTypes = CategoryResponseDto.class)) })
    @GetMapping
    public Page<CategoryResponseDto> findAll(
            Pageable pageable
    ) {
        return categoryService.findAll(pageable);
    }

    /**
     * Find category by id
     *
     * @param id
     * @return CategoryResponseDto
     */
    @Operation(summary = "Find category by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the category by ID",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    @GetMapping("/{id}")
    public CategoryResponseDto findById(
            @PathVariable Long id
    ) {
        return categoryService.findById(id);
    }

    /**
     * Save category
     *
     * @param categoryDto
     * @return CategoryResponseDto
     */
    @Operation(summary = "Save a new category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category successfully created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponseDto.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('category:create')")
    public CategoryResponseDto save(
            @RequestBody CategoryDto categoryDto
    ) {
        return categoryService.save(categoryDto);
    }

    /**
     * Update category
     *
     * @param id
     * @param categoryDto
     * @return CategoryResponseDto
     */
    @Operation(summary = "Update category")
    @ApiResponse(responseCode = "200", description = "Category successfully updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponseDto.class)))
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('category:update')")
    public CategoryResponseDto update(
            @PathVariable Long id,
            @RequestBody CategoryDto categoryDto
    ) {
        return categoryService.update(id, categoryDto);
    }

    /**
     * Delete category
     *
     * @param id
     */
    @Operation(summary = "Delete category")
    @ApiResponse(responseCode = "204", description = "Category successfully deleted")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('category:delete')")
    public void delete(
            @PathVariable Long id
    ) {
        categoryService.delete(id);
    }
}
