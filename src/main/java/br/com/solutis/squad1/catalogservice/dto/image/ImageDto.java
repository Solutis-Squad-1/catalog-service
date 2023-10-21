package br.com.solutis.squad1.catalogservice.dto.image;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ImageDto(

        @NotBlank
        @Min(value = 3, message = "Archive name must be at least 3 characters long")
        String archiveName,

        @NotBlank
        @Min(value = 3, message = "Name must be at least 3 characters long")
        String originName,

        @NotBlank
        @Min(value = 3, message = "Content Type must be at least 3 characters long")
        String contentType,

        @NotBlank
        Long size,

        @NotBlank
        String url
) {
}
