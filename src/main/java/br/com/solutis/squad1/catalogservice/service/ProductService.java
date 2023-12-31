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
import br.com.solutis.squad1.catalogservice.model.repository.ProductRepositoryCustom;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class that provides business logic for product-related operations.
 *
 * This service handles operations such as retrieving, creating, updating, and deleting products. It also manages
 * product images, including saving and deleting images associated with a product.
 *
 * All operations are transactional, ensuring data consistency and integrity.
 *
 * This service interacts with the {@link ProductRepository} and {@link ProductRepositoryCustom} for database access,
 * {@link CategoryRepository} for category-related operations, {@link ProductMapper} for mapping between DTOs and entities,
 * and {@link CategoryMapper} for category-related mapping.
 *
 * @Service Indicates that this class is a Spring service bean.
 * @Transactional Specifies that the methods of this service are transactional.
 * @RequiredArgsConstructor Lombok annotation that generates a constructor with required fields.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final ProductRepositoryCustom productRepositoryCustom;
    private final CategoryRepository categoryRepository;
    private final ProductMapper mapper;
    private final CategoryMapper categoryMapper;

    /**
     * Find all products
     *
     * @param productName
     * @param categoryName
     * @param pageable
     * @return Page<ProductResponseDto>
     */
    public Page<ProductResponseDto> findAll(String productName, String categoryName, Pageable pageable) {
        LOGGER.info("Find all products with product name: {} and category name: {}", productName, categoryName);

        Page<Product> products = productRepositoryCustom
                .findAllWithFilterAndDeletedFalse(productName, categoryName, pageable);
        productRepository.findProductsCategories(products.getContent());
        productRepository.findProductsImage(products.getContent());

        return products.map(mapper::toResponseDto);
    }

    /**
     * Find all products by seller id
     *
     * @param id
     * @param productName
     * @param categoryName
     * @param pageable
     * @return Page<ProductResponseDto>
     */
    public Page<ProductResponseDto> findBySellerId(Long id, String productName, String categoryName, Pageable pageable) {
        LOGGER.info("Find seller by id {} with category", id);

        Page<Product> products = productRepositoryCustom
                .findAllWithFilterBySellerIdAndDeletedFalse(id, productName, categoryName, pageable);
        productRepository.findProductsCategories(products.getContent());
        productRepository.findProductsImage(products.getContent());

        return products.map(mapper::toResponseDto);
    }

    /**
     * Find product by id
     *
     * @param id
     * @return ProductResponseDto
     */
    public ProductResponseDto findById(Long id) {
        LOGGER.info("Find product by id {}", id);
        Product product = productRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        return mapper.toResponseDto(product);
    }

    /**
     * Save product
     *
     * @param productPostDto
     * @return ProductResponseDto
     */
    public ProductResponseDto save(ProductPostDto productPostDto) {
        LOGGER.info("Saving product {}", productPostDto);
        Product product = mapper.postDtoToEntity(productPostDto);

        // TODO: Garantir que o vendedor existe

        Set<Category> categories = getCategories(productPostDto.categoryIds());
        if (categories.isEmpty()) {
            throw new EntityNotFoundException("Category not found");
        }
        Set<Long> categoriesIds = categories.stream().map(Category::getId).collect(Collectors.toSet());

        LOGGER.info("Save product");
        product = productRepository.save(product);
        LOGGER.info("Product saved");

        LOGGER.info("Save product categories");
        productRepository.saveAllCategories(product.getId(), categoriesIds);
        LOGGER.info("Product categories saved");

        Set<CategoryResponseDto> categoriesResponse = categoryMapper.toResponseDto(categories);
        return new ProductResponseDto(product, categoriesResponse);
    }

    /**
     * Update product
     *
     * @param id
     * @param productPutDto
     * @return ProductResponseDto
     */
    public ProductResponseDto update(Long id, ProductPutDto productPutDto) {
        LOGGER.info("Updating product with id {}", id);
        Product product = productRepository.getReferenceById(id);

        // TODO: Garantir que o vendedor existe

        Product newProduct = mapper.putDtoToEntity(productPutDto);

        Set<Category> categories = getCategories(productPutDto.categoryIds());
        if (categories.isEmpty()) {
            throw new EntityNotFoundException("Category not found");
        }
        Set<Long> categoriesIds = categories.stream().map(Category::getId).collect(Collectors.toSet());

        LOGGER.info("Update product");
        product.update(newProduct);
        LOGGER.info("Product updated");

        LOGGER.info("Delete product categories");
        product.deleteCategories();
        LOGGER.info("Product categories deleted");

        LOGGER.info("Save product categories");
        productRepository.saveAllCategories(product.getId(), categoriesIds);
        LOGGER.info("Product categories saved");

        Set<CategoryResponseDto> categoriesResponse = categoryMapper.toResponseDto(categories);
        return new ProductResponseDto(product, categoriesResponse);
    }

    /**
     * Delete product
     *
     * @param id
     */
    public void delete(Long id) {
        LOGGER.info("Deleting product with id {}", id);
        Product product = productRepository.getReferenceById(id);
        product.delete();
        LOGGER.info("Product deleted");
    }

    /**
     * Save product image
     *
     * @param productId
     * @param image
     */
    public void saveImage(Long productId, Image image) {
        LOGGER.info("Save product image for product with id {}", productId);
        Product product = productRepository.getReferenceById(productId);
        product.setImage(image);
        productRepository.save(product);
        LOGGER.info("Product image saved");
    }

    /**
     * Delete product image
     *
     * @param id
     */
    public void deleteImage(Long id) {
        LOGGER.info("Deleting product image with id {}", id);
        Product product = productRepository.getReferenceById(id);
        product.setImage(null);
        productRepository.save(product);
    }

    private Set<Category> getCategories(List<Long> ids) {
        LOGGER.info("Find categories by ids {}", ids);
        return categoryRepository.findAllByListOfIdAndDeletedFalse(ids);
    }

    /**
     * Find products list
     *
     * @param productsId
     * @return List<ProductResponseDto>
     */
    public List<ProductResponseDto> findProductsList(List<Long> productsId) {
        LOGGER.info("Find products list with id: {}", productsId);
        List<Product> products = productRepository.findAllById(productsId);

        return products.stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
