package br.com.solutis.squad1.catalogservice.model.repository;

import br.com.solutis.squad1.catalogservice.model.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
/**
 * JPA Repository for the {@link Image} entity.
 *
 * This repository extends the {@link JpaRepository} interface provided by Spring Data JPA, offering standard CRUD operations
 * for the {@link Image} entity. Additionally, it defines a custom query using the JPA {@link Query} annotation to fetch an
 * image by product ID.
 *
 * The repository uses the JPA {@link Query} annotation to define a JPQL query for retrieving images based on the product ID.
 * This query ensures that only non-deleted images associated with non-deleted products are retrieved.
 */
public interface ImageRepository extends JpaRepository<Image, Long> {
    /**
     * Find image by product id
     *
     * @return Optional<Image>
     */
    @Query(
            "SELECT pi FROM Product p JOIN p.image pi WHERE p.id = :id AND pi.deleted = false AND p.deleted = false"
    )
    Optional<Image> findByProductIdAndDeletedIsFalse(@Param("id") Long id);
}
