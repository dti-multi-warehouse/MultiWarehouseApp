package com.dti.multiwarehouse.product.repository;

import com.dti.multiwarehouse.product.dao.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
