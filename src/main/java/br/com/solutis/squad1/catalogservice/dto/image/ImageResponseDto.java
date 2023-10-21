package br.com.solutis.squad1.catalogservice.dto.image;

public record ImageResponseDto(
        Long id,
        String archiveName,
        String originalName,
        String contentType,
        Long size,
        String url
) {
}
