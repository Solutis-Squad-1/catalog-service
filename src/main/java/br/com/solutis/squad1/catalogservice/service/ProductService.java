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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper mapper;
    private final CategoryMapper categoryMapper;

    public Page<ProductResponseDto> findAll(String categoryName, Pageable pageable) {
        try {
            LOGGER.info("Find all products");
            if (categoryName == null) {
                LOGGER.info("Find all products without category");
                Page<Product> products = productRepository.findAllByDeletedFalse(pageable);
                productRepository.findProductCategories(products.getContent());
                return products.map(mapper::toResponseDto);
            }

            LOGGER.info("Find all products with category {}", categoryName);
            Category category = categoryRepository.findByName(categoryName);
            if (category == null) {
                throw new EntityNotFoundException("Category not found");
            }

            Page<Product> products = productRepository.findAllByCategoryIdAndDeletedFalse(category.getId(), pageable);
            productRepository.findProductCategories(products.getContent());
            return products.map(mapper::toResponseDto);
        } catch (EntityNotFoundException e) {
            LOGGER.error("Category not found");
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error while finding products");
            throw e;
        }
    }

    public Page<ProductResponseDto> findBySellerId(Long id, String categoryName, Pageable pageable) {
        try {
            LOGGER.info("Find seller by id {} with category", id);
            if (categoryName == null) {
                LOGGER.info("Find seller without category");
                Page<Product> products = productRepository.findAllBySellerIdAndDeletedFalse(id, pageable);
                productRepository.findProductCategories(products.getContent());
                return products.map(mapper::toResponseDto);
            }

            LOGGER.info("Find all products with category {}", categoryName);
            Category category = categoryRepository.findByName(categoryName);
            if (category == null) {
                throw new EntityNotFoundException("Category not found");
            }

            Page<Product> products = productRepository
                    .findAllBySellerIdAndCategoryIdAndDeletedFalse(id, category.getId(), pageable);
            productRepository.findProductCategories(products.getContent());
            return products.map(mapper::toResponseDto);
        } catch (EntityNotFoundException e) {
            LOGGER.error("Category not found");
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error while finding products");
            throw e;
        }
    }

    public ProductResponseDto findById(Long id) {
        try {
            LOGGER.info("Find product by id {}", id);
            Product product = productRepository.findByIdAndDeletedIsFalse(id)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));

            return mapper.toResponseDto(product);
        } catch (EntityNotFoundException e) {
            LOGGER.error("Product not found");
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error while finding product");
            throw e;
        }
    }

    public ProductResponseDto save(ProductPostDto productPostDto) {
        try {
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
        } catch (EntityNotFoundException e) {
            LOGGER.error("Category not found");
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error while saving product");
            throw e;
        }
    }

    public ProductResponseDto update(Long id, ProductPutDto productPutDto) {
        try {
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
        } catch (EntityNotFoundException e) {
            LOGGER.error("Category not found");
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error while updating product");
            throw e;
        }
    }

    public void delete(Long id) {
        try {
            LOGGER.info("Deleting product with id {}", id);
            Product product = productRepository.getReferenceById(id);
            product.delete();
            LOGGER.info("Product deleted");
        } catch (Exception e) {
            LOGGER.error("Error while deleting product");
            throw e;
        }
    }

    public void saveImage(Long productId, Image image) {
        try {
            LOGGER.info("Save product image for product with id {}", productId);
            Product product = productRepository.getReferenceById(productId);
            product.setImage(image);
            LOGGER.info("Product image saved");
        } catch (Exception e) {
            LOGGER.error("Error while saving product image");
            throw e;
        }
    }

    public void deleteImage(Long id) {
        try {
            LOGGER.info("Deleting product image with id {}", id);
            Product product = productRepository.getReferenceById(id);
            product.setImage(null);
        } catch (Exception e) {
            LOGGER.error("Error while deleting product image");
            throw e;
        }
    }

    private Set<Category> getCategories(List<Long> ids) {
        try {
            LOGGER.info("Find categories by ids {}", ids);
            return categoryRepository.findAllByListOfIdAndDeletedFalse(ids);
        } catch (Exception e) {
            LOGGER.error("Error while finding categories by ids");
            throw e;
        }
    }

    public Page<ProductResponseDto> findProductsByName(String name, Pageable pageable) {
        try{
            LOGGER.info("Searching for products by name containing: {}", name);
            Page<Product> products = productRepository.findProductsByName(name, pageable);
            return products.map(mapper::toResponseDto);
        } catch (Exception e){
            LOGGER.error("Error while deleting product");
            throw e;
        }
    }
}
