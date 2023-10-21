package br.com.solutis.squad1.catalogservice.dto.product;

import jakarta.annotation.Nullable;

import java.math.BigDecimal;
import java.util.List;

public record ProductPutDto(
        @Nullable
        String name,

        @Nullable
        String description,

        @Nullable
        BigDecimal price,

        @Nullable
        List<Long> categoryIds
) {
}
