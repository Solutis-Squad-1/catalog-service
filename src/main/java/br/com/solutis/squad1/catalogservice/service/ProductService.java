package br.com.solutis.squad1.catalogservice.service;

import br.com.solutis.squad1.catalogservice.dto.product.ProductPostDto;
import br.com.solutis.squad1.catalogservice.dto.product.ProductPutDto;
import br.com.solutis.squad1.catalogservice.dto.product.ProductResponseDto;
import br.com.solutis.squad1.catalogservice.exception.EntityNotFoundException;
import br.com.solutis.squad1.catalogservice.mapper.CategoryMapper;
import br.com.solutis.squad1.catalogservice.mapper.ProductMapper;
import br.com.solutis.squad1.catalogservice.model.entity.Category;
import br.com.solutis.squad1.catalogservice.model.entity.Image;
import br.com.solutis.squad1.catalogservice.model.entity.Product;
import br.com.solutis.squad1.catalogservice.model.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductMapper mapper;
    private final CategoryMapper categoryMapper;

    public Page<ProductResponseDto> findAll(String category, Pageable pageable) {
        if (category == null) {
            return productRepository.findAllByDeletedFalseWithCategories(pageable).map(mapper::toResponseDto);
        }

        return productRepository.findAllByCategoryAndDeletedFalse(category, pageable).map(mapper::toResponseDto);
    }

    public ProductResponseDto findById(Long id) {
        Product product = productRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        return mapper.toResponseDto(product);
    }

    public Page<ProductResponseDto> findBySellerId(Long id, String category, Pageable pageable) {
        if (category == null) {
            return productRepository.findAllBySellerIdAndDeletedFalse(id, pageable).map(mapper::toResponseDto);
        }

        return productRepository
                .findAllBySellerIdAndCategoryAndDeletedFalse(id, category, pageable).map(mapper::toResponseDto);
    }

    public ProductResponseDto save(ProductPostDto productPostDto) {
        Product product = mapper.postDtoToEntity(productPostDto);

        // TODO: Garantir que o vendedor existe

        Set<Category> categories = getCategories(productPostDto.categoryIds());
        if (categories.isEmpty()) {
            throw new EntityNotFoundException("Category not found");
        }

        product.setCategories(categories);

        product = productRepository.save(product);

        return mapper.toResponseDto(product);
    }

    public ProductResponseDto update(Long id, ProductPutDto productPutDto) {
        Product product = productRepository.getReferenceById(id);

        // TODO: Garantir que o vendedor existe

        Product newProduct = mapper.putDtoToEntity(productPutDto);

        Set<Category> categories = getCategories(productPutDto.categoryIds());
        if (categories.isEmpty()) {
            throw new EntityNotFoundException("Category not found");
        }

        newProduct.setCategories(categories);

        product.update(newProduct);
        return mapper.toResponseDto(product);
    }

    public void delete(Long id) {
        Product product = productRepository.getReferenceById(id);
        product.delete();
    }

    public void saveImage(Long productId, Image image) {
        Product product = productRepository.getReferenceById(productId);
        product.setImage(image);
    }

    public void deleteImage(Long id) {
        Product product = productRepository.getReferenceById(id);
        product.setImage(null);
    }

    private Set<Category> getCategories(List<Long> ids) {
        return categoryService.findByListOfId(ids);
    }
}
