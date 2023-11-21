package br.com.solutis.squad1.catalogservice.model.repository;

import br.com.solutis.squad1.catalogservice.exception.EntityNotFoundException;
import br.com.solutis.squad1.catalogservice.model.entity.Category;
import br.com.solutis.squad1.catalogservice.model.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Custom repository for handling custom queries related to the {@link Product} entity.

 * This repository provides methods for querying products with specific criteria, such as product name, category name,
 * seller ID, and pagination. It interacts with the JPA EntityManager to execute native and JPQL queries.
 *
 * The class is annotated with Spring's {@link Repository} to indicate that it is a repository component in the Spring Data context.
 * It is designed to work in conjunction with the standard {@link org.springframework.data.repository.CrudRepository} for the {@link Product} entity.
 *
 * Methods:
 * - {@link #findAllWithFilterAndDeletedFalse(String, String, Pageable)}: Find all products when deleted is false with pagination.
 * - {@link #findAllWithFilterBySellerIdAndDeletedFalse(Long, String, String, Pageable)}: Find all products when deleted is false with pagination and filter by seller ID.
 *
 * Private methods:
 * - {@link #setPagination(Pageable, TypedQuery)}: Set pagination parameters for the query.
 * - {@link #setQueryParameter(String, String, TypedQuery)}: Set query parameters based on optional filters (product name, category name).
 * - {@link #findCategoryByName(String)}: Find a category by name. This method is used internally for filtering by category name.
 * - {@link #getFindAllBySellerIdQuery(String, String)}: Construct a native query for finding products by seller ID.
 * - {@link #getFindAllQuery(String, String)}: Construct a query for finding all products with optional filters.
 *
 * The class uses the JPA {@link PersistenceContext} annotation to inject the EntityManager, allowing it to interact with the database.
 */
@Repository
public class ProductRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    /**
     * Find all products when deleted is false with pagination
     *
     * @param productName
     * @param categoryName
     * @param pageable
     * @return Page<Product>
     */
    public Page<Product> findAllWithFilterAndDeletedFalse(String productName, String categoryName, Pageable pageable) {
        TypedQuery<Product> typedQuery = getFindAllQuery(productName, categoryName);

        setQueryParameter(productName, categoryName, typedQuery);

        setPagination(pageable, typedQuery);

        List<Product> resultList = typedQuery.getResultList();
        return new PageImpl<>(resultList, pageable, resultList.size());
    }

    /**
     * Find all products when deleted is false with pagination and filter by seller id
     *
     * @param sellerId
     * @param productName
     * @param categoryName
     * @param pageable
     * @return Page<Product>
     */
    public Page<Product> findAllWithFilterBySellerIdAndDeletedFalse(
            Long sellerId,
            String productName,
            String categoryName,
            Pageable pageable
    ) {
        TypedQuery<Product> typedQuery = getFindAllBySellerIdQuery(productName, categoryName);
        typedQuery.setParameter("sellerId", sellerId);

        setQueryParameter(productName, categoryName, typedQuery);

        setPagination(pageable, typedQuery);

        List<Product> resultList = typedQuery.getResultList();
        return new PageImpl<>(resultList, pageable, resultList.size());
    }

    private void setPagination(Pageable pageable, TypedQuery<Product> typedQuery) {
        typedQuery.setMaxResults(pageable.getPageSize());
        typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
    }

    private void setQueryParameter(String productName, String categoryName, TypedQuery<Product> typedQuery) {
        if (productName != null) {
            typedQuery.setParameter("name", productName);
        }
        if (categoryName != null) {
            Category category = findCategoryByName(categoryName);

            typedQuery.setParameter("categoryId", category.getId());
        }
    }

    private Category findCategoryByName(String categoryName) {
        try {
            String findCategoryByNameAndDeletedFalseStringQuery = "SELECT c FROM Category c WHERE c.name = :categoryName AND c.deleted = false";
            TypedQuery<Category> findCategoryByNameAndDeletedFalseQuery = em.createQuery(findCategoryByNameAndDeletedFalseStringQuery, Category.class);
            findCategoryByNameAndDeletedFalseQuery.setParameter("categoryName", categoryName);

            return findCategoryByNameAndDeletedFalseQuery.getSingleResult();
        } catch (Exception e) {
            throw new EntityNotFoundException("Category not found");
        }
    }

    private TypedQuery<Product> getFindAllBySellerIdQuery(String productName, String categoryName) {
        StringBuilder query;

        if (categoryName == null) {
            query = new StringBuilder(
                    "SELECT p FROM Product p WHERE p.deleted = false AND p.sellerId = :sellerId"
            );
        } else {
            query = new StringBuilder(
                    "SELECT p.* FROM products p JOIN products_categories pc ON p.id = pc.product_id WHERE pc.category_id = :categoryId AND p.deleted = false AND p.seller_id = :sellerId"
            );
        }

        if (productName != null) {
            query.append(" AND p.name LIKE CONCAT('%', :name, '%')");
        }

        query.append(" ORDER BY p.id ASC");

        if (categoryName != null) {
            return em.createNativeQuery(query.toString(), Product.class).unwrap(TypedQuery.class);
        }

        return em.createQuery(query.toString(), Product.class);
    }

    private TypedQuery<Product> getFindAllQuery(String productName, String categoryName) {
        StringBuilder query;

        if (categoryName == null) {
            query = new StringBuilder(
                    "SELECT p FROM Product p WHERE p.deleted = false"
            );
        } else {
            query = new StringBuilder(
                    "SELECT p.* FROM products p JOIN products_categories pc ON p.id = pc.product_id WHERE pc.category_id = :categoryId AND p.deleted = false"
            );
        }

        if (productName != null) {
            query.append(" AND p.name LIKE CONCAT('%', :name, '%')");
        }

        query.append(" ORDER BY p.id ASC");

        if (categoryName != null) {
            return em.createNativeQuery(query.toString(), Product.class).unwrap(TypedQuery.class);
        }

        return em.createQuery(query.toString(), Product.class);
    }
}
