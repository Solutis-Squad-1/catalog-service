package br.com.solutis.squad1.catalogservice.mapper;

import br.com.solutis.squad1.catalogservice.dto.product.ProductPostDto;
import br.com.solutis.squad1.catalogservice.dto.product.ProductPutDto;
import br.com.solutis.squad1.catalogservice.dto.product.ProductResponseDto;
import br.com.solutis.squad1.catalogservice.model.entity.Category;
import br.com.solutis.squad1.catalogservice.model.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponseDto toResponseDto(Product product);

    @Mapping(target = "id", ignore = true)
    Category responseDtoToEntity(ProductResponseDto productResponseDto);

    @Mapping(target = "id", ignore = true)
    Product postDtoToEntity(ProductPostDto productPostDto);

    @Mapping(target = "id", ignore = true)
    Product putDtoToEntity(ProductPutDto productPutDto);
}
