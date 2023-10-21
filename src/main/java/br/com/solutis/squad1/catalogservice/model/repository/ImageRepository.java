package br.com.solutis.squad1.catalogservice.model.repository;

import br.com.solutis.squad1.catalogservice.model.entity.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Page<Image> findAllByDeletedFalse(Pageable pageable);

    Optional<Image> findByIdAndDeletedIsFalse(Long id);

    @Query(
            "SELECT pi FROM Product p JOIN p.image pi WHERE p.id = :id AND pi.deleted = false AND p.deleted = false"
    )
    Optional<Image> findByProductIdAndDeletedIsFalse(@Param("id") Long id);
}
