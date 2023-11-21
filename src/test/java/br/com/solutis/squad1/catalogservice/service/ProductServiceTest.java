package br.com.solutis.squad1.catalogservice.service;

import br.com.solutis.squad1.catalogservice.dto.category.CategoryResponseDto;
import br.com.solutis.squad1.catalogservice.dto.image.ImageResponseDto;
import br.com.solutis.squad1.catalogservice.dto.product.ProductPostDto;
import br.com.solutis.squad1.catalogservice.dto.product.ProductPutDto;
import br.com.solutis.squad1.catalogservice.dto.product.ProductResponseDto;
import br.com.solutis.squad1.catalogservice.exception.EntityNotFoundException;
import br.com.solutis.squad1.catalogservice.mapper.CategoryMapper;
import br.com.solutis.squad1.catalogservice.mapper.ProductMapper;
import br.com.solutis.squad1.catalogservice.model.builder.CategoryBuilder;
import br.com.solutis.squad1.catalogservice.model.builder.ImageBuilder;
import br.com.solutis.squad1.catalogservice.model.builder.ProductBuilder;
import br.com.solutis.squad1.catalogservice.model.entity.Category;
import br.com.solutis.squad1.catalogservice.model.entity.Image;
import br.com.solutis.squad1.catalogservice.model.entity.Product;
import br.com.solutis.squad1.catalogservice.model.repository.CategoryRepository;
import br.com.solutis.squad1.catalogservice.model.repository.ProductRepository;
import br.com.solutis.squad1.catalogservice.model.repository.ProductRepositoryCustom;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class ProductServiceTest {

    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepositoryCustom productRepositoryCustom;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private CategoryMapper categoryMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Returns a list of products")
    @Transactional
    void findAll_ShouldReturnListOfProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Product product = createProduct();
        String category = "Category name";
        List<Product> productList = List.of(product);
        Page<Product> productPage = new PageImpl<>(productList);

        when(productRepositoryCustom.findAllWithFilterAndDeletedFalse(product.getName(), category, pageable)).thenReturn(productPage);
        when(productRepository.findProductsCategories(productPage.getContent())).thenReturn(productList);
        when(productRepository.findProductsImage(productPage.getContent())).thenReturn(productList);
        when(productMapper.toResponseDto(product)).thenReturn(createProductResponseDto());

        Page<ProductResponseDto> result = productService.findAll(product.getName(), category, pageable);

        assertAll(
                () -> assertEquals(productList.size(), result.getContent().size()),
                () -> verify(productRepository).findProductsCategories(productList),
                () -> verify(productRepository).findProductsImage(productList),
                () -> verify(productMapper, times(productList.size())).toResponseDto(any())
        );
    }

    @Test
    @DisplayName("Returns an empty list when no products are found")
    @Transactional
    void findAll_ShouldReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Product product = createProduct();
        String category = "Category name";
        Page<Product> emptyProductPage = new PageImpl<>(Collections.emptyList());

        when(productRepositoryCustom.findAllWithFilterAndDeletedFalse(product.getName(), category, pageable))
                .thenReturn(emptyProductPage);

        Page<ProductResponseDto> result = productService.findAll(product.getName(), category, pageable);

        assertTrue(result.getContent().isEmpty());
    }


    @Test
    @DisplayName("Returns a list of products by seller ID")
    @Transactional
    void findBySellerId_ShouldReturnListOfProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Long sellerId = 1L;
        Product product = createProduct();
        List<Product> productList = List.of(product);
        Page<Product> productPage = new PageImpl<>(productList);

        when(productRepositoryCustom.findAllWithFilterBySellerIdAndDeletedFalse(sellerId, product.getName(), null, pageable)).thenReturn(productPage);
        when(productRepository.findProductsCategories(productPage.getContent())).thenReturn(productList);
        when(productRepository.findProductsImage(productPage.getContent())).thenReturn(productList);
        when(productMapper.toResponseDto(product)).thenReturn(createProductResponseDto());

        Page<ProductResponseDto> result = productService.findBySellerId(sellerId, product.getName(), null, pageable);

        assertAll(
                () -> assertEquals(productList.size(), result.getContent().size()),
                () -> verify(productRepository).findProductsCategories(productList),
                () -> verify(productRepository).findProductsImage(productList),
                () -> verify(productMapper, times(productList.size())).toResponseDto(any())
        );
    }

    @Test
    @DisplayName("Returns a list of products from a given category by seller ID")
    @Transactional
    void findBySellerId_ShouldReturnListOfProductsFromAGivenCategory() {
        Pageable pageable = PageRequest.of(0, 10);
        Long sellerId = 1L;
        Product product = createProduct();
        Category category = createCategory();
        product.setCategories(Set.of(category));
        List<Product> productList = List.of(product);
        Page<Product> productPage = new PageImpl<>(productList);

        when(productRepositoryCustom.findAllWithFilterBySellerIdAndDeletedFalse(sellerId, product.getName(), category.getName(), pageable)).thenReturn(productPage);
        when(productRepository.findProductsCategories(productPage.getContent())).thenReturn(productList);
        when(productRepository.findProductsImage(productPage.getContent())).thenReturn(productList);
        when(productMapper.toResponseDto(product)).thenReturn(createProductResponseDto());

        Page<ProductResponseDto> result = productService.findBySellerId(sellerId, product.getName(), category.getName(), pageable);

        assertAll(
                () -> assertEquals(productList.size(), result.getContent().size()),
                () -> assertTrue(productList.get(0).getCategories().contains(category)),
                () -> verify(productRepository).findProductsCategories(productList),
                () -> verify(productRepository).findProductsImage(productList),
                () -> verify(productMapper, times(productList.size())).toResponseDto(any())
        );
    }

    @Test
    @DisplayName("Returns an empty list when no products are found by seller ID")
    @Transactional
    void findBySellerId_ShouldReturnEmptyList() {
        Long sellerId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        when(productRepositoryCustom.findAllWithFilterBySellerIdAndDeletedFalse(sellerId, null, null, pageable)).thenReturn(new PageImpl<>(Collections.emptyList()));
        Page<ProductResponseDto> result = productService.findBySellerId(sellerId, null, null, pageable);

        assertAll(
                () -> assertTrue(result.getContent().isEmpty()),
                () -> verify(productMapper, never()).toResponseDto(any())
        );
    }

    @Test
    @DisplayName("Returns a product when found by ID")
    void findById_ShouldReturnProductWhenFound() {
        // Arrange
        Long productId = 1L;
        Product product = createProduct();
        when(productRepository.findByIdAndDeletedIsFalse(productId))
                .thenReturn(Optional.of(product));

        when(productMapper.toResponseDto(product)).thenReturn(createProductResponseDto());

        ProductResponseDto result = productService.findById(productId);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(createProductResponseDto(), result),
                () -> verify(productMapper).toResponseDto(product)
        );
    }

    @Test
    @DisplayName("Throws EntityNotFoundException when no product found by ID")
    void findById_ShouldThrowEntityNotFoundExceptionWhenNotFound() {
        Long nonExistentProductId = 2L;
        when(productRepository.findByIdAndDeletedIsFalse(nonExistentProductId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.findById(nonExistentProductId), "Product not found");
    }

    @Test
    @DisplayName("Returns the successfully saved product")
    void save_ShouldSaveProduct() {
        ProductPostDto productPostDto = createProductPostDto();
        Product product = createProduct();
        Set<Category> categories = Set.of(createCategory());
        Set<CategoryResponseDto> categoriesReponseDto = Set.of(createCategoryResponseDto());

        when(productMapper.postDtoToEntity(productPostDto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(categoryMapper.toResponseDto(categories)).thenReturn(categoriesReponseDto);
        when(categoryRepository.findAllByListOfIdAndDeletedFalse(anyList())).thenReturn(categories);

        ProductResponseDto result = productService.save(productPostDto);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(product.getId(), result.id()),
                () -> assertEquals(product.getName(), result.name()),
                () -> assertEquals(product.getPrice(), result.price()),
                () -> verify(productRepository).save(product),
                () -> verify(productRepository).saveAllCategories(product.getId(), categories.stream().map(Category::getId).collect(Collectors.toSet())),
                () -> verify(categoryMapper).toResponseDto(categories)
        );
    }

    @Test
    @DisplayName("Throws EntityNotFoundException when no categories are provided")
    void save_ShouldThrowEntityNotFoundExceptionWhenNoCategoriesProvided() {
        ProductPostDto productPostDto = createProductPostDtoEntityCategories();

        assertThrows(EntityNotFoundException.class, () -> productService.save(productPostDto), "Category not found");
    }

    @Test
    @DisplayName("Throws EntityNotFoundException when no categories are found")
    void save_ShouldThrowEntityNotFoundExceptionWhenNoCategoriesFound() {
        ProductPostDto productPostDto = createProductPostDto();
        Product product = createProduct();
        Set<Category> categories = Set.of(createCategory());
        Set<CategoryResponseDto> categoriesReponseDto = Set.of(createCategoryResponseDto());

        when(productMapper.postDtoToEntity(productPostDto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(categoryMapper.toResponseDto(categories)).thenReturn(categoriesReponseDto);

        assertThrows(EntityNotFoundException.class, () -> productService.save(productPostDto), "Category not found");
    }

    @Test
    @DisplayName("Returns the successfully updated product")
    void update_ShouldUpdateProductWithValidData() {
        ProductPostDto productPostDto = createProductPostDto();
        Product product = createProduct();
        Long productId = product.getId();
        ProductPutDto productPutDto = createProductPutDto();
        Product existingProduct = createProduct();
        Set<Category> categories = Set.of(createCategory());
        Set<CategoryResponseDto> categoriesResponseDto = Set.of(createCategoryResponseDto());

        when(productMapper.postDtoToEntity(productPostDto)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(categoryMapper.toResponseDto(categories)).thenReturn(categoriesResponseDto);
        when(categoryRepository.findAllByListOfIdAndDeletedFalse(anyList())).thenReturn(categories);
        productService.save(productPostDto);

        when(productRepository.getReferenceById(productId)).thenReturn(existingProduct);
        when(productMapper.putDtoToEntity(productPutDto)).thenReturn(existingProduct);
        when(categoryMapper.toResponseDto(categories)).thenReturn(categoriesResponseDto);
        when(categoryRepository.findAllByListOfIdAndDeletedFalse(anyList())).thenReturn(categories);

        ProductResponseDto result = productService.update(productId, productPutDto);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(existingProduct.getId(), result.id()),
                () -> assertEquals(existingProduct.getName(), result.name()),
                () -> assertEquals(existingProduct.getPrice(), result.price())
        );
    }

    @Test
    @DisplayName("Throws EntityNotFoundException when product is not found")
    void update_ShouldThrowEntityNotFoundExceptionWhenProductNotFound() {
        Long productId = 1L;
        ProductPutDto productPutDto = createProductPutDto();
        when(productRepository.getReferenceById(productId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> productService.update(productId, productPutDto), "Product not found");
    }

    @Test
    @DisplayName("Throws EntityNotFoundException when no categories are found")
    void update_ShouldThrowEntityNotFoundExceptionWhenNoCategoriesFound() {
        Long productId = 1L;
        ProductPutDto productPutDto = createProductPutDto();
        Product existingProduct = createProduct();
        when(productRepository.getReferenceById(productId)).thenReturn(existingProduct);
        when(categoryRepository.findAllByListOfIdAndDeletedFalse(anyList())).thenReturn(Collections.emptySet());

        assertThrows(EntityNotFoundException.class, () -> productService.update(productId, productPutDto), "Category not found");
    }

    @Test
    @DisplayName("Deletes product by ID")
    void delete_ShouldDeleteProductById() {
        Long productId = 1L;
        Product product = createProduct();

        when(productRepository.getReferenceById(productId)).thenReturn(product);
        productService.delete(productId);

        assertAll(
                () -> assertTrue(product.getDeleted()),
                () -> verify(productRepository, times(1)).getReferenceById(productId)
        );
    }

    @Test
    @DisplayName("Saves product image for a product")
    void saveImage_ShouldSaveProductImageForProduct() {
        Product product = createProduct();
        Image image = product.getImage();

        when(productRepository.getReferenceById(product.getId())).thenReturn(product);
        productService.saveImage(product.getId(), image);

        assertAll(
                () -> assertEquals(image, product.getImage()),
                () -> verify(productRepository).getReferenceById(product.getId()),
                () -> verify(productRepository).save(product)
        );
    }

  /*  @Test
    @DisplayName("Does nothing when product not found")
    void saveImage_ShouldDoNothingWhenProductNotFound() {
        Long productId = 1L;
        Image image = createImage();

        when(productRepository.getReferenceById(productId)).thenThrow(NullPointerException.class);
        productService.saveImage(productId, image);

        verify(productRepository, never()).save(any());
    }*/

    @Test
    @DisplayName("Deletes product image by ID")
    void deleteImage_ShouldDeleteProductImageById() {
        Long imageId = 1L;
        Product product = createProduct();

        when(productRepository.getReferenceById(imageId)).thenReturn(product);
        productService.deleteImage(imageId);

        assertAll(
                () -> assertNull(product.getImage()),
                () -> verify(productRepository).getReferenceById(imageId),
                () -> verify(productRepository).save(product)
        );
    }

   /* @Test
    @DisplayName("Does nothing when product not found")
    void deleteImage_ShouldDoNothingWhenProductNotFound() {
        Long imageId = 1L;

        when(productRepository.getReferenceById(imageId)).thenReturn(null);
        productService.deleteImage(imageId);

        verify(productRepository, never()).save(any());
    }*/

    @Test
    @DisplayName("Returns products list by IDs")
    void findProductsList_ShouldFindProductsListByIds() {
        List<Long> productIds = List.of(createProduct().getId());
        List<Product> products = Arrays.asList(createProduct());
        List<ProductResponseDto> expectedResponse = Arrays.asList(createProductResponseDto());

        when(productRepository.findAllById(productIds)).thenReturn(products);
        when(productMapper.toResponseDto(any())).thenReturn(createProductResponseDto());

        List<ProductResponseDto> result = productService.findProductsList(productIds);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(expectedResponse.size(), result.size()),
                () -> assertEquals(expectedResponse, result),
                () -> verify(productRepository).findAllById(productIds),
                () -> verify(productMapper, times(productIds.size())).toResponseDto(any())
        );
    }

    @Test
    @DisplayName("Returns empty list when no products found")
    void findProductsList_ShouldReturnEmptyListWhenNoProductsFound() {
        List<Long> productIds = List.of(createProduct().getId());

        when(productRepository.findAllById(productIds)).thenReturn(Collections.emptyList());

        List<ProductResponseDto> result = productService.findProductsList(productIds);

        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result.isEmpty()),
                () -> verify(productRepository).findAllById(productIds),
                () -> verify(productMapper, never()).toResponseDto(any())
        );
    }

    private Product createProduct(){
        ProductBuilder builder = new ProductBuilder();

        return builder
                .id(1L)
                .name("Product")
                .description("Description")
                .price(new BigDecimal(10))
                .sellerId(1L)
                .categories(Collections.singleton(createCategory()))
                .image(createImage())
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .deletedAt(null)
                .build();
    }

    private ProductResponseDto createProductResponseDto(){
        ProductBuilder builder = new ProductBuilder();

        return builder
                .id(1L)
                .name("Product")
                .description("Description")
                .price(new BigDecimal(10))
                .sellerId(1L)
                .categoriesResponseDto(Collections.singleton(createCategoryResponseDto()))
                .imageResponseDto(createImageResponseDto())
                .buildProductResponseDto();
    }

    private ProductPostDto createProductPostDto(){
        ProductBuilder builder = new ProductBuilder();
        Category category = createCategory();

        return builder
                .name("Product")
                .description("Description")
                .price(new BigDecimal(10))
                .sellerId(1L)
                .categoryIds(List.of(category.getId()))
                .buildProductPostDto();
    }

    private ProductPostDto createProductPostDtoEntityCategories(){
        ProductBuilder builder = new ProductBuilder();
        Category category = createCategory();

        return builder
                .name("Product")
                .description("Description")
                .price(new BigDecimal(10))
                .sellerId(1L)
                .categoryIds(Collections.emptyList())
                .buildProductPostDto();
    }

    private ProductPutDto createProductPutDto(){
        ProductBuilder builder = new ProductBuilder();

        return builder
                .name("Product")
                .description("Description")
                .price(new BigDecimal(10))
                .categoryIds(List.of(createCategory().getId()))
                .buildProductPutDto();
    }

    private Category createCategory(){
        CategoryBuilder builder = new CategoryBuilder();

        return builder
                .id(1L)
                .name("Category name")
                .build();
    }

    private CategoryResponseDto createCategoryResponseDto(){
        CategoryBuilder builder = new CategoryBuilder();

        return builder
                .id(1L)
                .name("Category name")
                .buildCategoryResponseDto();
    }

    private Image createImage() {
        ImageBuilder builder = new ImageBuilder();

        return builder
                .id(1L)
                .archiveName("Archive name")
                .originalName("Original name")
                .contentType("Content type")
                .size(1L)
                .deleted(false)
                .url("url")
                .createdAt(LocalDateTime.now())
                .deletedAt(null)
                .build();
    }

    private ImageResponseDto createImageResponseDto() {
        ImageBuilder builder = new ImageBuilder();

        return builder
                .id(1L)
                .archiveName("Archive name")
                .originalName("Original name")
                .contentType("Content type")
                .size(1L)
                .url("url")
                .buildImageResponseDto();
    }

    private List<Long> createListProductsIds(){
        List<Long> listProductsIds = new ArrayList<>();
        listProductsIds.add(1L);
        listProductsIds.add(2L);
        listProductsIds.add(3L);

        return listProductsIds;
    }
}