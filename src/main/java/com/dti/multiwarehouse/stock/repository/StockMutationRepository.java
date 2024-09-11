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
                update product
                set stock = (
                SELECT
                    (
                        SELECT
                            COALESCE(SUM(m.quantity), 0)
                        FROM stock_mutation AS m
                        WHERE m.product_id = :productId
                    ) - (
                        SELECT sum(case when o.status != 'CANCELLED' then i.quantity else 0 end)
                        FROM order_item AS i
                        JOIN orders AS o ON i.order_id = o.id
                        WHERE i.product_id = :productId
                    )
                )
                where product.id = :productId
                """, nativeQuery = true
    )
    void calculateProductStock(@Param("productId") Long productId);

    @Modifying
    @Query(
            value = """
                update stock
                set stock = (
                SELECT
                    (
                        SELECT
                            SUM(CASE WHEN m.warehouse_to_id = 1 AND m.status = 'COMPLETED' THEN m.quantity ELSE 0 END) -
                            SUM(CASE WHEN m.warehouse_from_id = 1 AND (m.status = 'AWAITING_CONFIRMATION' OR m.status = 'COMPLETED') THEN m.quantity ELSE 0 END)
                        FROM stock_mutation AS m
                        WHERE m.product_id = :productId
                    ) - (
                        SELECT sum(case when o.status != 'CANCELLED' then i.quantity else 0 end)
                        FROM order_item AS i
                        JOIN orders AS o ON i.order_id = o.id
                        WHERE i.product_id = :productId AND o.warehouse_id = :warehouseId
                    )
                )
                where stock.warehouse_id = :warehouseId AND stock.product_id = :productId;
            """, nativeQuery = true
    )
    void calculateWarehouseStock(@Param("productId") Long productId,@Param("warehouseId") Long warehouseId);
}
