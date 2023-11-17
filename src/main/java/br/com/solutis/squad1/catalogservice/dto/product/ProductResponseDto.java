package br.com.solutis.squad1.catalogservice.dto.product;

import br.com.solutis.squad1.catalogservice.dto.category.CategoryResponseDto;
import br.com.solutis.squad1.catalogservice.dto.image.ImageResponseDto;
import br.com.solutis.squad1.catalogservice.model.entity.Product;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Product response DTO
 */
public record ProductResponseDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Long sellerId,
        Set<CategoryResponseDto> categories,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        ImageResponseDto image
) {
    public ProductResponseDto(Product product, Set<CategoryResponseDto> categories) {
        this(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getSellerId(),
                categories,
                product.getImage() == null ? null : new ImageResponseDto(product.getImage())
        );
    }
}
