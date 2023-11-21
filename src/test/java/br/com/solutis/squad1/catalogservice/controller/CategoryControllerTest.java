package br.com.solutis.squad1.catalogservice.controller;

import br.com.solutis.squad1.catalogservice.dto.category.CategoryDto;
import br.com.solutis.squad1.catalogservice.dto.category.CategoryResponseDto;
import br.com.solutis.squad1.catalogservice.exception.EntityNotFoundException;
import br.com.solutis.squad1.catalogservice.model.builder.CategoryBuilder;
import br.com.solutis.squad1.catalogservice.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CategoryService service;

    @Test
    @DisplayName("Returns a list of categories")
    void findAll_ShouldReturnListOfCategories() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<CategoryResponseDto> listCategories = List.of(createCategoryResponseDto(), createCategoryResponseDto());
        Page<CategoryResponseDto> categories = new PageImpl<>(listCategories);

        when(service.findAll(pageable)).thenReturn(categories);

        mvc.perform(get("/api/v1/catalog/categories"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Returns an empty list of categories when there is no data registered")
    void findAll_ShouldReturnAnEmptyListOfCategories() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);

        when(service.findAll(pageable)).thenReturn(Page.empty());

        mvc.perform(get("/api/v1/catalog/categories"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Returns a category by id")
    void findById_ShouldReturnCategoryById() throws Exception {
        Long id = 1L;

        when(service.findById(id)).thenReturn(createCategoryResponseDto());

        mvc.perform(get("/api/v1/catalog/categories/" + id))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Returns an exception EntityNotFoundException when the category is not found")
    void findById_ShouldReturnNotFoundStatus() throws Exception {
        Long id = 999L;

        when(service.findById(id)).thenThrow(new EntityNotFoundException("Category not found"));

        mvc.perform(get("/api/v1/catalog/categories/" + id))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException))
                .andExpect(result -> assertEquals("Category not found", result.getResolvedException().getMessage()));
    }

    @Test
    @DisplayName("Should return HTTP 201 Created when creating a new category")
    @WithMockUser(authorities = "category:create")
    void save_ShouldReturnCreateStatus() throws Exception {
        CategoryDto dto = createCategoryDto();

        mvc.perform(post("/api/v1/catalog/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should return HTTP 201 Created when creating a new category")
    @WithAnonymousUser
    void save_ShouldReturnForbiddenStatus() throws Exception {
        CategoryDto dto = createCategoryDto();

        mvc.perform(post("/api/v1/catalog/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Returns 200 status when update category successfully")
    @WithMockUser(authorities = "category:update")
    void update_ShouldReturnOkStatus() throws Exception {
        Long id = 1L;
        CategoryDto dto = createCategoryDto();

        mvc.perform(put("/api/v1/catalog/categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Returns 403 if user lacks update authority")
    @WithAnonymousUser
    void update_ShouldReturnForbiddenStatus() throws Exception {
        Long id = 1L;
        CategoryDto dto = createCategoryDto();

        mvc.perform(put("/api/v1/catalog/categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Returns 204 after successful deletion")
    @WithMockUser(authorities = "category:delete")
    void delete_ShouldReturnNoContentStatus() throws Exception {
        Long id = 1L;

        mvc.perform(delete("/api/v1/catalog/categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Returns 404 if ID is missing in the URL")
    @WithMockUser(authorities = "category:delete")
    void delete_ShouldReturnBadRequestStatus() throws Exception {
        mvc.perform(delete("/api/v1/orders/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Returns 403 if user lacks delete authority")
    @WithAnonymousUser
    void delete_ShouldReturnForbiddenStatus() throws Exception {
        mvc.perform(delete("/api/v1/orders/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    private CategoryResponseDto createCategoryResponseDto() {
        CategoryBuilder builder = new CategoryBuilder();

        return builder
                .id(1L)
                .name("Category 1")
                .buildCategoryResponseDto();
    }

    private CategoryDto createCategoryDto(){
        CategoryBuilder builder = new CategoryBuilder();

        return builder
                .name("Category 1")
                .buildCategoryDto();
    }
}