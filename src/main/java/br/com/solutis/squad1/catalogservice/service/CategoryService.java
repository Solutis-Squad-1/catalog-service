package br.com.solutis.squad1.catalogservice.service;

import br.com.solutis.squad1.catalogservice.dto.category.CategoryDto;
import br.com.solutis.squad1.catalogservice.dto.category.CategoryResponseDto;
import br.com.solutis.squad1.catalogservice.exception.EntityNotFoundException;
import br.com.solutis.squad1.catalogservice.mapper.CategoryMapper;
import br.com.solutis.squad1.catalogservice.model.entity.Category;
import br.com.solutis.squad1.catalogservice.model.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;

    public Page<CategoryResponseDto> findAll(Pageable pageable) {
        return categoryRepository.findAllByDeletedFalse(pageable)
                .map(mapper::toResponseDto);
    }

    public CategoryResponseDto findById(Long id) {
        Category category = categoryRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        return mapper.toResponseDto(category);
    }

    public Set<Category> findByListOfId(List<Long> ids) {
        return categoryRepository.findAllByListOfIdAndDeletedFalse(ids);
    }

    public CategoryResponseDto save(CategoryDto categoryDto) {
        Category category = mapper.dtoToEntity(categoryDto);
        category = categoryRepository.save(category);

        return mapper.toResponseDto(category);
    }

    public CategoryResponseDto update(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.getReferenceById(id);
        category.update(mapper.dtoToEntity(categoryDto));
        return mapper.toResponseDto(category);
    }

    public void delete(Long id) {
        Category category = categoryRepository.getReferenceById(id);
        category.delete();
    }
}
