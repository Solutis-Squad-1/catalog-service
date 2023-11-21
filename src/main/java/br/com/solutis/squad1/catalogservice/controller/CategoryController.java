package br.com.solutis.squad1.catalogservice.controller;

import br.com.solutis.squad1.catalogservice.dto.category.CategoryDto;
import br.com.solutis.squad1.catalogservice.dto.category.CategoryResponseDto;
import br.com.solutis.squad1.catalogservice.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @Operation(summary = "Save category")
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
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('category:delete')")
    public void delete(
            @PathVariable Long id
    ) {
        categoryService.delete(id);
    }
}
