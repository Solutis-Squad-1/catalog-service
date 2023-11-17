package br.com.solutis.squad1.catalogservice.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.List;

/**
 * Product post DTO
 */
public record ProductPostDto(
        @NotBlank
        @Min(value = 3, message = "Name must be at least 3 characters long")
        String name,

        @NotBlank
        @Min(value = 3, message = "Description must be at least 3 characters long")
        String description,

        @NotBlank
        @Min(value = 0, message = "Price must be at least 0")
        BigDecimal price,

        @NotBlank
        Long sellerId,

        @NotEmpty
        List<Long> categoryIds
) {
}
