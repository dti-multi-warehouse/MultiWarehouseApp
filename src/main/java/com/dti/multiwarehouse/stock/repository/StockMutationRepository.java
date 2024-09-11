package com.dti.multiwarehouse.stock.repository;

import com.dti.multiwarehouse.stock.dao.StockMutation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Modifying
    @Query(
            value = """
            UPDATE Stock s
            SET s.stock = (
                SELECT COALESCE(SUM(CASE WHEN m.warehouseTo.id = :warehouseId AND m.status = 'COMPLETED' THEN m.quantity ELSE 0 END), 0) -
                       COALESCE(SUM(CASE WHEN m.warehouseFrom.id = :warehouseId AND (m.status = 'AWAITING_CONFIRMATION' OR m.status = 'COMPLETED') THEN m.quantity ELSE 0 END), 0)
                FROM StockMutation m
                WHERE m.product.id = :productId
            )
            WHERE s.id.warehouse.id = :warehouseId
            """
    )
    void calculateWarehouseStock(@Param("productId") Long productId,@Param("warehouseId") Long warehouseId);
}
