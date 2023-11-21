package br.com.solutis.squad1.catalogservice.model.repository;

import br.com.solutis.squad1.catalogservice.model.builder.CategoryBuilder;
import br.com.solutis.squad1.catalogservice.model.builder.ImageBuilder;
import br.com.solutis.squad1.catalogservice.model.builder.ProductBuilder;
import br.com.solutis.squad1.catalogservice.model.entity.Category;
import br.com.solutis.squad1.catalogservice.model.entity.Image;
import br.com.solutis.squad1.catalogservice.model.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProductRepositoryCustomTest {

    @MockBean
    private ProductRepositoryCustom productRepositoryCustom;

    @Test
    @DisplayName("Finds all products with filter and deleted false")
    void findAllWithFilterAndDeletedFalse_ShouldReturnPageOfProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        String productName = "Product";
        String categoryName = "Category";

        when(productRepositoryCustom.findAllWithFilterAndDeletedFalse(productName, categoryName, pageable)).thenReturn(new PageImpl<>(Arrays.asList(createProduct(), createProduct())));
        Page<Product> result = productRepositoryCustom.findAllWithFilterAndDeletedFalse(productName, categoryName, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }

    @Test
    @DisplayName("Finds all products with filter by sellerId and deleted false")
    void findAllWithFilterBySellerIdAndDeletedFalse_ShouldReturnPageOfProducts() {
        Long sellerId = 1L;
        String productName = "Product";
        String categoryName = "Category";
        Pageable pageable = PageRequest.of(0, 10);

        when(productRepositoryCustom.findAllWithFilterBySellerIdAndDeletedFalse(sellerId, productName, categoryName, pageable)).thenReturn(new PageImpl<>(Arrays.asList(createProduct(), createProduct())));
        Page<Product> result = productRepositoryCustom.findAllWithFilterBySellerIdAndDeletedFalse(sellerId, productName, categoryName, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }

    private Product createProduct(){
        ProductBuilder builder = new ProductBuilder();

        return builder
                .id(1L)
                .name("Product")
                .description("Description")
                .price(new BigDecimal(10))
                .sellerId(1L)
                .categories(Collections.singleton(createCategory()))
                .image(createImage())
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .deletedAt(null)
                .build();
    }

    private Category createCategory(){
        CategoryBuilder builder = new CategoryBuilder();

        return builder
                .id(1L)
                .name("Category name")
                .build();
    }

    private Image createImage() {
        ImageBuilder builder = new ImageBuilder();

        return builder
                .id(1L)
                .archiveName("Archive name")
                .originalName("Original name")
                .contentType("Content type")
                .size(1L)
                .deleted(false)
                .url("url")
                .createdAt(LocalDateTime.now())
                .deletedAt(null)
                .build();
    }
}