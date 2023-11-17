package br.com.solutis.squad1.catalogservice.service;

import br.com.solutis.squad1.catalogservice.dto.category.CategoryDto;
import br.com.solutis.squad1.catalogservice.dto.category.CategoryResponseDto;
import br.com.solutis.squad1.catalogservice.exception.EntityNotFoundException;
import br.com.solutis.squad1.catalogservice.mapper.CategoryMapper;
import br.com.solutis.squad1.catalogservice.model.entity.Category;
import br.com.solutis.squad1.catalogservice.model.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;

    /**
     * Find all categories
     *
     * @param pageable
     * @return Page<CategoryResponseDto>
     */
    public Page<CategoryResponseDto> findAll(Pageable pageable) {
        log.info("Find all categories");
        return categoryRepository.findAllByDeletedFalse(pageable)
                .map(mapper::toResponseDto);
    }

    /**
     * Find category by id
     *
     * @param id
     * @return CategoryResponseDto
     */
    public CategoryResponseDto findById(Long id) {
        log.info("Find category by id {}", id);
        Category category = categoryRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        return mapper.toResponseDto(category);
    }

    /**
     * Save category
     *
     * @param categoryDto
     * @return CategoryResponseDto
     */
    public CategoryResponseDto save(CategoryDto categoryDto) {
        log.info("Saving category {}", categoryDto);
        Category category = mapper.dtoToEntity(categoryDto);
        category = categoryRepository.save(category);

        return mapper.toResponseDto(category);
    }

    /**
     * Update category
     *
     * @param id
     * @param categoryDto
     * @return CategoryResponseDto
     */
    public CategoryResponseDto update(Long id, CategoryDto categoryDto) {
        log.info("Updating category with id {}", id);
        Category category = categoryRepository.getReferenceById(id);
        category.update(mapper.dtoToEntity(categoryDto));
        return mapper.toResponseDto(category);
    }

    /**
     * Delete category
     *
     * @param id
     */
    public void delete(Long id) {
        log.info("Deleting category with id {}", id);
        Category category = categoryRepository.getReferenceById(id);
        category.delete();
    }
}
