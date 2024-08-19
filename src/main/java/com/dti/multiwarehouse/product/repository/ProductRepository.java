package com.dti.multiwarehouse.product.repository;

import com.dti.multiwarehouse.product.dao.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
