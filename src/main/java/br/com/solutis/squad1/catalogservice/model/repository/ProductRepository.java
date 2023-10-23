package br.com.solutis.squad1.catalogservice.model.repository;

import br.com.solutis.squad1.catalogservice.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAllByDeletedFalse(Pageable pageable);

    @Query(
            "SELECT p FROM Product p JOIN FETCH p.categories c WHERE p.deleted = false AND c.deleted = false AND p IN :products"
    )
    List<Product> findProductCategories(@Param("products") List<Product> products);

    @Query(
            value = "SELECT p.* FROM products p JOIN products_categories pc ON p.id = pc.product_id WHERE pc.category_id = :categoryId AND p.deleted = false",
            nativeQuery = true
    )
    Page<Product> findAllByCategoryIdAndDeletedFalse(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query(
            "SELECT p FROM Product p JOIN FETCH p.categories c WHERE p.deleted = false AND c.deleted = false AND p.id = :id"
    )
    Optional<Product> findByIdAndDeletedIsFalse(long id);

    Page<Product> findAllBySellerIdAndDeletedFalse(Long id, Pageable pageable);

    @Query(
            value = "SELECT p.* FROM products p JOIN products_categories pc ON p.id = pc.product_id WHERE pc.category_id = :categoryId AND p.deleted = false AND p.seller_id = :id",
            nativeQuery = true
    )
    Page<Product> findAllBySellerIdAndCategoryIdAndDeletedFalse(
            @Param("id") Long id,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );

    @Modifying
    @Query(
            value = "INSERT INTO products_categories (product_id, category_id) SELECT :productId, id FROM categories WHERE id IN :categoryIds",
            nativeQuery = true
    )
    void saveAllCategories(@Param("productId") Long productId, @Param("categoryIds") Set<Long> categoryIds);

}
