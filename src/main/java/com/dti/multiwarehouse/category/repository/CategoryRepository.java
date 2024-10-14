package com.dti.multiwarehouse.category.repository;

import com.dti.multiwarehouse.category.dao.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByOrderByIdAsc();

    @Query(
            value = """
            SELECT *
            FROM Category
            WHERE deleted_at IS NULL
            ORDER BY id
            """, nativeQuery = true
    )
    List<Category> getAllCategories();
}
