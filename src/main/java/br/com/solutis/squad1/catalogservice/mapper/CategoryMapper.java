package br.com.solutis.squad1.catalogservice.mapper;

import br.com.solutis.squad1.catalogservice.dto.category.CategoryDto;
import br.com.solutis.squad1.catalogservice.dto.category.CategoryResponseDto;
import br.com.solutis.squad1.catalogservice.model.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponseDto toResponseDto(Category category);

    @Mapping(target = "id", ignore = true)
    Category responseDtoToEntity(CategoryResponseDto categoryResponseDto);

    @Mapping(target = "id", ignore = true)
    Category dtoToEntity(CategoryDto categoryDto);
}