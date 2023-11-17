package br.com.solutis.squad1.catalogservice.model.repository;

import br.com.solutis.squad1.catalogservice.model.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    /**
     * Find all categories that are not deleted
     *
     * @return Page<Category>
     */
    Page<Category> findAllByDeletedFalse(Pageable pageable);

    /**
     * Find category by id
     *
     * @param id
     * @return Optional<Category>
     */
    Optional<Category> findByIdAndDeletedIsFalse(long id);

    /**
     * Find all categories by list of ids
     *
     * @param ids
     * @return Set<Category>
     */
    @Query(
            "SELECT c FROM Category c WHERE c.id IN :ids AND c.deleted = false"
    )
    Set<Category> findAllByListOfIdAndDeletedFalse(List<Long> ids);
}
