package com.dti.multiwarehouse.category.repository;

import com.dti.multiwarehouse.category.dao.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
