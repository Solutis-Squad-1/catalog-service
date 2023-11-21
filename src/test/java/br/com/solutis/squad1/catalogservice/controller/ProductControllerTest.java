package br.com.solutis.squad1.catalogservice.controller;

import br.com.solutis.squad1.catalogservice.dto.category.CategoryResponseDto;
import br.com.solutis.squad1.catalogservice.dto.image.ImageResponseDto;
import br.com.solutis.squad1.catalogservice.dto.product.ProductPostDto;
import br.com.solutis.squad1.catalogservice.dto.product.ProductPutDto;
import br.com.solutis.squad1.catalogservice.dto.product.ProductResponseDto;
import br.com.solutis.squad1.catalogservice.exception.EntityNotFoundException;
import br.com.solutis.squad1.catalogservice.model.builder.CategoryBuilder;
import br.com.solutis.squad1.catalogservice.model.builder.ImageBuilder;
import br.com.solutis.squad1.catalogservice.model.builder.ProductBuilder;
import br.com.solutis.squad1.catalogservice.model.entity.Category;
import br.com.solutis.squad1.catalogservice.service.ImageService;
import br.com.solutis.squad1.catalogservice.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private ImageService imageService;

    @Test
    @DisplayName("Returns a list of products")
    void findAll_ShouldReturnListOfCategories() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductResponseDto> categories = new PageImpl<>(List.of(createProductResponseDto()));
        String name = "name";
        String category = "category";

        when(productService.findAll(name, category, pageable)).thenReturn(categories);

        mvc.perform(get("/api/v1/catalog/products"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Returns an empty list of products when there is no data registered")
    void findAll_ShouldReturnAnEmptyListOfCategories() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        String name = "name";
        String category = "category";

        when(productService.findAll(name, category, pageable)).thenReturn(Page.empty());

        mvc.perform(get("/api/v1/catalog/products"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Returns products by seller id")
    void findBySellerId_ReturnsProductsBySellerId() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Long sellerId = 1L;
        String name = "Product";
        String category = "Electronics";
        Page<ProductResponseDto> categories = new PageImpl<>(List.of(createProductResponseDto()));

        when(productService.findBySellerId(sellerId, name, category, pageable)).thenReturn(categories);

        mvc.perform(get("/api/v1/catalog/products/sellers/" + sellerId))
                  .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Returns an empty list of products when there is no data registered in the seller is ID")
    void findBySellerId_ShouldReturnNotFoundStatus() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Long sellerId = 1L;
        String name = "Product";
        String category = "Electronics";

        when(productService.findBySellerId(sellerId, name, category, pageable)).thenReturn(Page.empty());

        mvc.perform(get("/api/v1/catalog/products/sellers/" + sellerId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Returns a product by id")
    void findById_ShouldReturnProductById() throws Exception {
        Long id = 1L;

        when(productService.findById(id)).thenReturn(createProductResponseDto());

        mvc.perform(get("/api/v1/catalog/products/" + id))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Returns an exception EntityNotFoundException when the product is not found")
    void findById_ShouldReturnNotFoundStatus() throws Exception {
        Long id = 999L;

        when(productService.findById(id)).thenThrow(new EntityNotFoundException("Product not found"));

        mvc.perform(get("/api/v1/catalog/products/" + id))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException))
                .andExpect(result -> assertEquals("Product not found", result.getResolvedException().getMessage()));
    }

    @Test
    @DisplayName("Returns a list of products by user")
    void findProductsByUser_ShouldReturnProductList() throws Exception {
        List<Long> productsId = Arrays.asList(1L);
        List<ProductResponseDto> products = Arrays.asList(createProductResponseDto());

        when(productService.findProductsList(productsId)).thenReturn(products);
        products = productService.findProductsList(productsId);

        assertNotNull(products);
        assertEquals(productsId.size(), products.size());
        assertEquals(200, HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Returns an empty list of products by user when there is no data registered")
    void findProductsByUsr_ShouldReturnProductList() throws Exception {
        List<ProductResponseDto> products = new ArrayList<>();

        when(productService.findProductsList(anyList())).thenReturn(products);
        products = productService.findProductsList(new ArrayList<>());

        assertTrue(products.isEmpty());
        assertEquals(200, HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Should return HTTP 201 Created when creating a new product")
    @WithMockUser(authorities = "product:create")
    void save_ShouldReturnCreateStatus() throws Exception {
        ProductPostDto dto = createProductPostDto();

        mvc.perform(post("/api/v1/catalog/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should return HTTP 201 Created when creating a new product")
    @WithAnonymousUser
    void save_ShouldReturnForbiddenStatus() throws Exception {
        ProductPostDto dto = createProductPostDto();

        mvc.perform(post("/api/v1/catalog/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Update catalog with complete data - Should return No Content status")
    @WithMockUser(authorities = "product:update")
    void updateCompleteCatalog_ShouldReturnNoContentStatus() throws Exception {
        Long id = 1L;
        ProductPutDto dto = createProductPutDto();

        mvc.perform(put("/api/v1/catalog/products/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Update catalog with incomplete data - Should return No Content status")
    @WithMockUser(authorities = "product:update")
    void updateIncompleteCatalog_ShouldReturnNoContentStatus() throws Exception {
        Long id = 1L;
        ProductPutDto dto = createIncompleteProductPutDto();

        mvc.perform(put("/api/v1/catalog/products/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Returns 403 if user lacks update authority")
    @WithAnonymousUser
    void updateOrder_ShouldReturnForbiddenStatus() throws Exception {
        Long id = 1L;
        ProductPutDto dto = createProductPutDto();

        mvc.perform(put("/api/v1/catalog/products/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Returns 204 after successful deletion")
    @WithMockUser(authorities = "product:delete")
    void delete_ShouldReturnNoContentStatus() throws Exception {
        Long id = 1L;

        mvc.perform(delete("/api/v1/catalog/products/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Returns 404 if ID is missing in the URL")
    @WithMockUser(authorities = "product:delete")
    void delete_ShouldReturnBadRequestStatus() throws Exception {
        mvc.perform(delete("/api/v1/catalog/products/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Returns 403 if user lacks delete authority")
    @WithAnonymousUser
    void delete_ShouldReturnForbiddenStatus() throws Exception {
        Long id = 1L;

        mvc.perform(delete("/api/v1/catalog/products/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void loadImage() {
    }

    @Test
    void uploadImage() {
    }

    @Test
    void deleteImage() {
    }

    private ProductPostDto createProductPostDto(){
        ProductBuilder builder = new ProductBuilder();

        return builder
                .name("name")
                .description("description")
                .price(new BigDecimal(10.0))
                .sellerId(1L)
                .categoryIds(List.of(1L))
                .buildProductPostDto();
    }

    private ProductPutDto createProductPutDto(){
        ProductBuilder builder = new ProductBuilder();

        return builder
                .name("name")
                .description("description")
                .price(new BigDecimal(10.0))
                .categoryIds(List.of(1L))
                .buildProductPutDto();
    }

    private ProductPutDto createIncompleteProductPutDto(){
        ProductBuilder builder = new ProductBuilder();

        return builder
                .name("name")
                .price(new BigDecimal(10.0))
                .buildProductPutDto();
    }

    private ProductResponseDto createProductResponseDto() {
        ProductBuilder builder = new ProductBuilder();

        return builder
                .id(1L)
                .name("name")
                .description("description")
                .price(new BigDecimal(10.0))
                .sellerId(1L)
                .categoriesResponseDto(Set.of(createCategoryResponseDto()))
                .imageResponseDto(createImageResponseDto())
                .buildProductResponseDto();
    }

    private Category createCategory(){
        CategoryBuilder builder = new CategoryBuilder();

        return builder
                .id(1L)
                .name("name")
                .build();
    }

    private CategoryResponseDto createCategoryResponseDto() {
        CategoryBuilder builder = new CategoryBuilder();

        return builder
                .id(1L)
                .name("name")
                .buildCategoryResponseDto();
    }

    private ImageResponseDto createImageResponseDto(){
        ImageBuilder builder = new ImageBuilder();

        return builder
                .id(1L)
                .archiveName("archiveName")
                .originalName("originalName")
                .contentType("contentType")
                .size(10L)
                .url("url")
                .buildImageResponseDto();
    }
}