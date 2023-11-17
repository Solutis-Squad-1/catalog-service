package br.com.solutis.squad1.catalogservice.dto.category;

import jakarta.validation.constraints.NotNull;

/**
 * Category DTO
 */
public record CategoryDto(
        @NotNull
        String name
) {

}