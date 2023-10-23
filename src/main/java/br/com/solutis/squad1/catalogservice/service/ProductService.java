package br.com.solutis.squad1.catalogservice.service;

import br.com.solutis.squad1.catalogservice.dto.category.CategoryResponseDto;
import br.com.solutis.squad1.catalogservice.dto.product.ProductPostDto;
import br.com.solutis.squad1.catalogservice.dto.product.ProductPutDto;
import br.com.solutis.squad1.catalogservice.dto.product.ProductResponseDto;
import br.com.solutis.squad1.catalogservice.exception.EntityNotFoundException;
import br.com.solutis.squad1.catalogservice.mapper.CategoryMapper;
import br.com.solutis.squad1.catalogservice.mapper.ProductMapper;
import br.com.solutis.squad1.catalogservice.model.entity.Category;
import br.com.solutis.squad1.catalogservice.model.entity.Image;
import br.com.solutis.squad1.catalogservice.model.entity.Product;
import br.com.solutis.squad1.catalogservice.model.repository.CategoryRepository;
import br.com.solutis.squad1.catalogservice.model.repository.ProductRepository;
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
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper mapper;
    private final CategoryMapper categoryMapper;

    public Page<ProductResponseDto> findAll(String categoryName, Pageable pageable) {
        if (categoryName == null) {
            Page<Product> products = productRepository.findAllByDeletedFalse(pageable);
            productRepository.findProductCategories(products.getContent());
            return products.map(mapper::toResponseDto);
        }

        Category category = categoryRepository.findIdByName(categoryName);
        if (category == null) {
            throw new EntityNotFoundException("Category not found");
        }

        Page<Product> products = productRepository.findAllByCategoryIdAndDeletedFalse(category.getId(), pageable);
        productRepository.findProductCategories(products.getContent());
        return products.map(mapper::toResponseDto);
    }

    public Page<ProductResponseDto> findBySellerId(Long id, String categoryName, Pageable pageable) {
        if (categoryName == null) {
            Page<Product> products = productRepository.findAllBySellerIdAndDeletedFalse(id, pageable);
            productRepository.findProductCategories(products.getContent());
            return products.map(mapper::toResponseDto);
        }

        Category category = categoryRepository.findIdByName(categoryName);
        if (category == null) {
            throw new EntityNotFoundException("Category not found");
        }

        Page<Product> products = productRepository
                .findAllBySellerIdAndCategoryIdAndDeletedFalse(id, category.getId(), pageable);
        productRepository.findProductCategories(products.getContent());
        return products.map(mapper::toResponseDto);
    }

    public ProductResponseDto findById(Long id) {
        Product product = productRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        return mapper.toResponseDto(product);
    }

    public ProductResponseDto save(ProductPostDto productPostDto) {
        Product product = mapper.postDtoToEntity(productPostDto);

        // TODO: Garantir que o vendedor existe

        Set<Category> categories = getCategories(productPostDto.categoryIds());
        if (categories.isEmpty()) {
            throw new EntityNotFoundException("Category not found");
        }
        Set<Long> categoriesIds = categories.stream().map(Category::getId).collect(Collectors.toSet());

        product = productRepository.save(product);
        productRepository.saveAllCategories(product.getId(), categoriesIds);

        Set<CategoryResponseDto> categoriesResponse = categoryMapper.toResponseDto(categories);
        return new ProductResponseDto(product, categoriesResponse);
    }

    public ProductResponseDto update(Long id, ProductPutDto productPutDto) {
        Product product = productRepository.getReferenceById(id);

        // TODO: Garantir que o vendedor existe

        Product newProduct = mapper.putDtoToEntity(productPutDto);

        Set<Category> categories = getCategories(productPutDto.categoryIds());
        if (categories.isEmpty()) {
            throw new EntityNotFoundException("Category not found");
        }
        Set<Long> categoriesIds = categories.stream().map(Category::getId).collect(Collectors.toSet());

        product.update(newProduct);
        product.deleteCategories();

        productRepository.saveAllCategories(product.getId(), categoriesIds);

        Set<CategoryResponseDto> categoriesResponse = categoryMapper.toResponseDto(categories);
        return new ProductResponseDto(product, categoriesResponse);
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
        return categoryRepository.findAllByListOfIdAndDeletedFalse(ids);
    }
}
