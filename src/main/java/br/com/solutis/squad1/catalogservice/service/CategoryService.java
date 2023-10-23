package br.com.solutis.squad1.catalogservice.service;

import br.com.solutis.squad1.catalogservice.dto.category.CategoryDto;
import br.com.solutis.squad1.catalogservice.dto.category.CategoryResponseDto;
import br.com.solutis.squad1.catalogservice.exception.EntityNotFoundException;
import br.com.solutis.squad1.catalogservice.mapper.CategoryMapper;
import br.com.solutis.squad1.catalogservice.model.entity.Category;
import br.com.solutis.squad1.catalogservice.model.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;

    public Page<CategoryResponseDto> findAll(Pageable pageable) {
        try {
            LOGGER.info("Find all categories");
            return categoryRepository.findAllByDeletedFalse(pageable)
                    .map(mapper::toResponseDto);
        } catch (Exception e) {
            LOGGER.error("Error while finding categories");
            throw e;
        }
    }

    public CategoryResponseDto findById(Long id) {
        try {
            LOGGER.info("Find category by id {}", id);
            Category category = categoryRepository.findByIdAndDeletedIsFalse(id)
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));

            return mapper.toResponseDto(category);
        } catch (EntityNotFoundException e) {
            LOGGER.error("Category not found");
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error while finding category");
            throw e;
        }
    }

    public CategoryResponseDto save(CategoryDto categoryDto) {
        try {
            LOGGER.info("Saving category {}", categoryDto);
            Category category = mapper.dtoToEntity(categoryDto);
            category = categoryRepository.save(category);

            return mapper.toResponseDto(category);
        } catch (Exception e) {
            LOGGER.error("Error while saving category");
            throw e;
        }
    }

    public CategoryResponseDto update(Long id, CategoryDto categoryDto) {
        try {
            LOGGER.info("Updating category with id {}", id);
            Category category = categoryRepository.getReferenceById(id);
            category.update(mapper.dtoToEntity(categoryDto));
            return mapper.toResponseDto(category);
        } catch (Exception e) {
            LOGGER.error("Error while updating category");
            throw e;
        }
    }

    public void delete(Long id) {
        try {
            LOGGER.info("Deleting category with id {}", id);
            Category category = categoryRepository.getReferenceById(id);
            category.delete();
        } catch (Exception e) {
            LOGGER.error("Error while deleting category");
            throw e;
        }
    }
}