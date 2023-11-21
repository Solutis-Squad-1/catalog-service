package br.com.solutis.squad1.catalogservice.model.builder;

import br.com.solutis.squad1.catalogservice.dto.category.CategoryDto;
import br.com.solutis.squad1.catalogservice.dto.category.CategoryResponseDto;
import br.com.solutis.squad1.catalogservice.model.entity.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.time.LocalDateTime;

public class CategoryBuilder {
    private Long id;
    private String name;
    private Boolean deleted = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public CategoryBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public CategoryBuilder name(String name) {
        this.name = name;
        return this;
    }

    public CategoryBuilder deleted(Boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public CategoryBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public CategoryBuilder updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public CategoryBuilder deletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

    public Category build(){
        return new Category(id, name, deleted, createdAt, updatedAt, deletedAt);
    }

    public CategoryResponseDto buildCategoryResponseDto(){
        return new CategoryResponseDto(id, name);
    }

    public CategoryDto buildCategoryDto(){
        return new CategoryDto(name);
    }
}
