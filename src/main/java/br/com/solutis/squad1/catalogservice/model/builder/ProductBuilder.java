package br.com.solutis.squad1.catalogservice.model.builder;

import br.com.solutis.squad1.catalogservice.dto.category.CategoryResponseDto;
import br.com.solutis.squad1.catalogservice.dto.image.ImageResponseDto;
import br.com.solutis.squad1.catalogservice.dto.product.ProductPostDto;
import br.com.solutis.squad1.catalogservice.dto.product.ProductPutDto;
import br.com.solutis.squad1.catalogservice.dto.product.ProductResponseDto;
import br.com.solutis.squad1.catalogservice.model.entity.Category;
import br.com.solutis.squad1.catalogservice.model.entity.Image;
import br.com.solutis.squad1.catalogservice.model.entity.Product;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class ProductBuilder {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long sellerId;
    private Set<Category> categories;
    private Set<CategoryResponseDto> categoriesResponseDto;
    private List<Long> categoryIds;
    private Image image;
    private ImageResponseDto imageResponseDto;
    private Boolean deleted = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public ProductBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public ProductBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ProductBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ProductBuilder price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public ProductBuilder sellerId(Long sellerId) {
        this.sellerId = sellerId;
        return this;
    }

    public ProductBuilder categories(Set<Category> categories) {
        this.categories = categories;
        return this;
    }

    public ProductBuilder categoriesResponseDto(Set<CategoryResponseDto> categoriesResponseDto) {
        this.categoriesResponseDto = categoriesResponseDto;
        return this;
    }

    public ProductBuilder categoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
        return this;
    }

    public ProductBuilder image(Image image) {
        this.image = image;
        return this;
    }

    public ProductBuilder imageResponseDto(ImageResponseDto imageResponseDto) {
        this.imageResponseDto = imageResponseDto;
        return this;
    }

    public ProductBuilder deleted(Boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public ProductBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public ProductBuilder updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public ProductBuilder deletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

    public Product build(){
        return new Product(id, name, description, price, sellerId, categories, image, deleted, createdAt, updatedAt, deletedAt);
    }

    public ProductPostDto buildProductPostDto(){
        return new ProductPostDto(name, description, price, sellerId, categoryIds);
    }

    public ProductPutDto buildProductPutDto(){
        return new ProductPutDto(name, description, price, categoryIds);
    }

    public ProductResponseDto buildProductResponseDto(){
        return new ProductResponseDto(id, name, description, price, sellerId, categoriesResponseDto, imageResponseDto);
    }
}
