package br.com.solutis.squad1.catalogservice.service;

import br.com.solutis.squad1.catalogservice.dto.category.CategoryDto;
import br.com.solutis.squad1.catalogservice.dto.category.CategoryResponseDto;
import br.com.solutis.squad1.catalogservice.exception.EntityNotFoundException;
import br.com.solutis.squad1.catalogservice.mapper.CategoryMapper;
import br.com.solutis.squad1.catalogservice.model.builder.CategoryBuilder;
import br.com.solutis.squad1.catalogservice.model.entity.Category;
import br.com.solutis.squad1.catalogservice.model.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    @Test
    @DisplayName("Returns a list of categories")
    @Transactional
    void findAll_ShouldReturnListOfCategories() {
        Pageable pageable = PageRequest.of(0, 10);
        Category category = createCategory();

        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        categoryRepository.save(category);
        when(categoryMapper.toResponseDto(any(Category.class))).thenReturn(createCategoryResponseDto());
        when(categoryRepository.findAllByDeletedFalse(pageable)).thenReturn(new PageImpl<>(Collections.singletonList(category), pageable, 1));
        Page<CategoryResponseDto> response = categoryService.findAll(pageable);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(1, response.getContent().size()),
                () -> assertEquals(CategoryResponseDto.class, response.getContent().get(0).getClass()),
                () -> verify(categoryRepository, times(1)).findAllByDeletedFalse(pageable)
        );
    }

    @Test
    @DisplayName("Returns an empty category list")
    void findAll_ShouldReturnAnEmptyListOfCategories() {
        Pageable pageable = PageRequest.of(0, 10);

        when(categoryRepository.findAllByDeletedFalse(pageable)).thenReturn(new PageImpl<>(Collections.EMPTY_LIST, pageable, 0));
        Page<CategoryResponseDto> response = categoryService.findAll(pageable);

        assertEquals(0, response.getContent().size());
        verify(categoryRepository, times(1)).findAllByDeletedFalse(pageable);
    }

    @Test
    @DisplayName("Return CategoryResponseDto if category is found")
    void findById_ShouldReturnCategoryResponseDto() {
        Category category = createCategory();

        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        categoryRepository.save(category);
        when(categoryMapper.toResponseDto(any(Category.class))).thenReturn(createCategoryResponseDto());
        when(categoryRepository.findByIdAndDeletedIsFalse(category.getId())).thenReturn(Optional.of(category));
        CategoryResponseDto response = categoryService.findById(category.getId());

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(CategoryResponseDto.class, response.getClass()),
                () -> assertEquals(category.getId(), response.id())
        );
    }

    @Test
    @DisplayName("Return EntityNotFoundException if category is not found")
    void findById_WithInvalidCategoryId_ShouldThrowEntityNotFoundException() {
        Long id = 999L;

        when(categoryRepository.findByIdAndDeletedIsFalse(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.findById(id));
    }

    @Test
    void save_ShouldSaveCategoryAndReturnResponseDto() {
        CategoryDto categoryDto = createCategoryDto();
        Category category = createCategory();
        CategoryResponseDto categoryResponseDto = createCategoryResponseDto();

        when(categoryMapper.dtoToEntity(categoryDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toResponseDto(category)).thenReturn(categoryResponseDto);

        CategoryResponseDto response = categoryService.save(categoryDto);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(categoryResponseDto, response),
                () -> verify(categoryMapper, times(1)).dtoToEntity(categoryDto),
                () -> verify(categoryRepository, times(1)).save(category),
                () -> verify(categoryMapper, times(1)).toResponseDto(category)
        );
    }

    @Test
    @DisplayName("Return null when the passed object is null")
    void save_ShouldReturnNull() {
        CategoryDto categoryDto = null;
        Category category = null;
        CategoryResponseDto categoryResponseDto = null;

        when(categoryMapper.dtoToEntity(categoryDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toResponseDto(category)).thenReturn(categoryResponseDto);

        CategoryResponseDto result = categoryService.save(categoryDto);

        assertNull(result);
    }

    @Test
    @DisplayName("Should update the category successfully")
    void update_ShouldUpdateCategory() {
        Long categoryId = 1L;
        CategoryDto categoryDto = createCategoryUpdateDto();
        Category existingCategory = createCategory();
        Category updatedCategory = createCategory();

        when(categoryRepository.getReferenceById(categoryId)).thenReturn(existingCategory);
        when(categoryMapper.dtoToEntity(categoryDto)).thenReturn(updatedCategory);
        when(categoryMapper.toResponseDto(updatedCategory)).thenReturn(new CategoryResponseDto(existingCategory.getId(), existingCategory.getName()));

        CategoryResponseDto result = categoryService.update(categoryId, categoryDto);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(categoryId, result.id()),
                () -> assertEquals(updatedCategory.getName(), result.name()),
                () -> verify(categoryRepository, times(1)).getReferenceById(categoryId),
                () -> verify(categoryMapper, times(1)).dtoToEntity(categoryDto),
                () -> verify(categoryMapper, times(1)).toResponseDto(updatedCategory)
        );
    }

    @Test
    void update_ShouldHandleNonExistingCategory() {
        Long categoryId = 1L;
        CategoryDto categoryDto = new CategoryDto("Updated category");

        when(categoryRepository.getReferenceById(categoryId)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> categoryService.update(categoryId, categoryDto));
    }

    @Test
    @DisplayName("Should delete order and order items")
    void delete_ShouldDeleteOrderAndOrderItems() {
        Long id = 1L;
        Category category = new Category();

        when(categoryRepository.getReferenceById(id)).thenReturn(category);
        categoryService.delete(id);

        verify(categoryRepository, times(1)).getReferenceById(id);
    }

    private Category createCategory() {
        CategoryBuilder builder = new CategoryBuilder();

        return builder
                .id(1L)
                .name("Category 1")
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .deletedAt(null)
                .build();
    }

    private CategoryDto createCategoryDto() {
        CategoryBuilder builder = new CategoryBuilder();

        return builder
                .name("Category 1")
                .buildCategoryDto();
    }

    private CategoryDto createCategoryUpdateDto() {
        CategoryBuilder builder = new CategoryBuilder();

        return builder
                .name("Updated category")
                .buildCategoryDto();
    }

    private CategoryResponseDto createCategoryResponseDto() {
        CategoryBuilder builder = new CategoryBuilder();

        return builder
                .id(1L)
                .name("Category 1")
                .buildCategoryResponseDto();
    }
}