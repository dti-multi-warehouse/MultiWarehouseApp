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
            UPDATE stock
            SET stock = COALESCE((
                SELECT
                    COALESCE((
                        SELECT
                            COALESCE(SUM(CASE WHEN m.warehouse_to_id = :warehouseId AND m.status = 'COMPLETED' THEN m.quantity ELSE 0 END), 0) -
                            COALESCE(SUM(CASE WHEN m.warehouse_from_id = :warehouseId AND (m.status = 'AWAITING_CONFIRMATION' OR m.status = 'COMPLETED') THEN m.quantity ELSE 0 END), 0)
                        FROM stock_mutation AS m
                        WHERE m.product_id = :productId
                    ), 0) - COALESCE((
                        SELECT COALESCE(SUM(CASE WHEN o.status != 'CANCELLED' THEN i.quantity ELSE 0 END), 0)
                        FROM order_item AS i
                        JOIN orders AS o ON i.order_id = o.id
                        WHERE i.product_id = :productId AND o.warehouse_id = :warehouseId
                    ), 0)
            ), 0)
            WHERE stock.warehouse_id = :warehouseId AND stock.product_id = :productId
        """, nativeQuery = true
    )
    void calculateWarehouseStock(@Param("productId") Long productId, @Param("warehouseId") Long warehouseId);
}
