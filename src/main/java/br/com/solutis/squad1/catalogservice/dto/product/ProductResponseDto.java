package br.com.solutis.squad1.catalogservice.dto.product;

import br.com.solutis.squad1.catalogservice.dto.category.CategoryResponseDto;
import br.com.solutis.squad1.catalogservice.dto.image.ImageResponseDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.Set;

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
}
