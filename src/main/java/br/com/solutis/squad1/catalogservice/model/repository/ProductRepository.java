package br.com.solutis.squad1.catalogservice.model.repository;

import br.com.solutis.squad1.catalogservice.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, Long> {
    /**
     * Find all categories from product
     *
     * @return List<Product>
     */
    @Query(
            "SELECT p FROM Product p JOIN FETCH p.categories c WHERE p.deleted = false AND c.deleted = false AND p IN :products"
    )
    List<Product> findProductsCategories(@Param("products") List<Product> products);

    /**
     * Find all images from product
     *
     * @return List<Product>
     */
    @Query(
            "SELECT p FROM Product p JOIN FETCH p.image i WHERE p.deleted = false AND i.deleted = false AND p IN :products"
    )
    List<Product> findProductsImage(@Param("products") List<Product> products);

    /**
     * Find product by id where deleted is false
     *
     * @return Optional<Product>
     */
    @Query(
            "SELECT p FROM Product p JOIN FETCH p.categories c WHERE p.deleted = false AND c.deleted = false AND p.id = :id"
    )
    Optional<Product> findByIdAndDeletedIsFalse(@Param("id") Long id);

    /**
     * Save all categories from product
     *
     * @param productId
     * @param categoryIds
     * @return void
     */
    @Modifying
    @Query(
            value = "INSERT INTO products_categories (product_id, category_id) SELECT :productId, id FROM categories WHERE id IN :categoryIds",
            nativeQuery = true
    )
    void saveAllCategories(@Param("productId") Long productId, @Param("categoryIds") Set<Long> categoryIds);
}
