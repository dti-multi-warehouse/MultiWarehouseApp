package com.dti.multiwarehouse.stock.repository;

import com.dti.multiwarehouse.stock.dao.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
}
