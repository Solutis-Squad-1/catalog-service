package br.com.solutis.squad1.catalogservice.mapper;

import br.com.solutis.squad1.catalogservice.dto.image.ImageDto;
import br.com.solutis.squad1.catalogservice.dto.image.ImageResponseDto;
import br.com.solutis.squad1.catalogservice.model.entity.Image;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    ImageResponseDto toResponseDto(Image image);

    ImageDto toDto(Image image);

    @Mapping(target = "id", ignore = true)
    Image responseDtoToEntity(ImageResponseDto imageResponseDto);

    @Mapping(target = "id", ignore = true)
    Image dtoToEntity(ImageDto productImageDto);
}
