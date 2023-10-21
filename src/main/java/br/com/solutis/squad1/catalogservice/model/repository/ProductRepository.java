package br.com.solutis.squad1.catalogservice.model.repository;

import br.com.solutis.squad1.catalogservice.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(
            "SELECT p FROM Product p JOIN FETCH p.categories WHERE p.deleted = false"
    )
    Page<Product> findAllByDeletedFalseWithCategories(Pageable pageable);

    @Query(
            "SELECT p FROM Product p JOIN p.categories c WHERE c.name = :category AND p.deleted = false"
    )
    Page<Product> findAllByCategoryAndDeletedFalse(@Param("category") String category, Pageable pageable);

    Optional<Product> findByIdAndDeletedIsFalse(long id);

    Page<Product> findAllBySellerIdAndDeletedFalse(Long id, Pageable pageable);

    @Query(
            "SELECT p FROM Product p JOIN p.categories c WHERE  p.sellerId = :id AND c.name = :category AND p.deleted = false"
    )
    Page<Product> findAllBySellerIdAndCategoryAndDeletedFalse(
            @Param("id") Long id,
            @Param("category") String category,
            Pageable pageable
    );
}
