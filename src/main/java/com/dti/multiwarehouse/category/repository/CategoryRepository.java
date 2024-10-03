package com.dti.multiwarehouse.category.repository;

import com.dti.multiwarehouse.category.dao.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByOrderByIdAsc();
}
