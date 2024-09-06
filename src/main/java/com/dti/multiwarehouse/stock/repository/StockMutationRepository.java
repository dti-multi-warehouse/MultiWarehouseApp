package com.dti.multiwarehouse.stock.repository;

import com.dti.multiwarehouse.stock.dao.StockMutation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;

public interface StockMutationRepository extends JpaRepository<StockMutation, Long> {
    @Modifying
    @Query(
            value = """
                UPDATE Product p
                SET p.stock = (
                    SELECT COALESCE(SUM(m.quantity), 0)
                    FROM StockMutation m
                    WHERE m.product.id = p.id
                    AND m.warehouseFrom IS NULL
                )
                WHERE p.id = :productId
                """
    )
    void calculateProductStock(Long productId);
}
