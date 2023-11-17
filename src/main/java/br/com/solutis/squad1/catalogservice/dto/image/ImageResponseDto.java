package br.com.solutis.squad1.catalogservice.dto.image;

import br.com.solutis.squad1.catalogservice.model.entity.Image;

/**
 * Image response DTO
 */
public record ImageResponseDto(
        Long id,
        String archiveName,
        String originalName,
        String contentType,
        Long size,
        String url
) {
    public ImageResponseDto(Image image) {
        this(
                image.getId(),
                image.getArchiveName(),
                image.getOriginalName(),
                image.getContentType(),
                image.getSize(),
                image.getUrl()
        );
    }
}
