package br.com.solutis.squad1.catalogservice.model.builder;

import br.com.solutis.squad1.catalogservice.dto.category.CategoryResponseDto;
import br.com.solutis.squad1.catalogservice.dto.image.ImageDto;
import br.com.solutis.squad1.catalogservice.dto.image.ImageResponseDto;
import br.com.solutis.squad1.catalogservice.model.entity.Category;
import br.com.solutis.squad1.catalogservice.model.entity.Image;
import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public class ImageBuilder {
    private Long id;
    private String archiveName;
    private String originalName;
    private String contentType;
    private Long size;
    private Boolean deleted = false;
    private String url;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    public ImageBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public ImageBuilder archiveName(String archiveName) {
        this.archiveName = archiveName;
        return this;
    }

    public ImageBuilder originalName(String originalName) {
        this.originalName = originalName;
        return this;
    }

    public ImageBuilder contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public ImageBuilder size(Long size) {
        this.size = size;
        return this;
    }

    public ImageBuilder deleted(Boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public ImageBuilder url(String url) {
        this.url = url;
        return this;
    }

    public ImageBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public ImageBuilder deletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

    public Image build(){
        return new Image(id, archiveName, originalName, contentType, size, deleted, url, createdAt, deletedAt);
    }

    public ImageResponseDto buildImageResponseDto(){
        return new ImageResponseDto(id, archiveName, originalName, contentType, size, url);
    }

    public ImageDto buildImageDto(){
        return new ImageDto(archiveName, originalName, contentType, size, url);
    }
}
