package com.dti.multiwarehouse.stock.repository;

import com.dti.multiwarehouse.dashboard.dto.response.RetrieveMonthlyStockSummary;
import com.dti.multiwarehouse.stock.dao.StockMutation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

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

    @Query(
            value = """
            SELECT *
            FROM stock_mutation
            WHERE warehouse_from_id = :warehouseId and status = 'AWAITING_CONFIRMATION'
            """, nativeQuery = true
    )
    List<StockMutation> findAllActiveRequestByWarehouseId(@Param("warehouseId") Long warehouseId);

    @Query(
            value = """
            SELECT *
            FROM stock_mutation
            WHERE status = 'AWAITING_CONFIRMATION'
            """, nativeQuery = true
    )
    List<StockMutation> findAllActiveRequest();

    @Query(
            value = """
            SELECT p.id,
            p.name,
            sum(CASE WHEN s.warehouse_to_id = :warehouseId AND s.status = 'COMPLETED' THEN s.quantity ELSE 0 END) AS incoming,
            sum(CASE WHEN s.warehouse_from_id = :warehouseId AND s.status = 'COMPLETED' THEN s.quantity ELSE 0 END) +
            sum(CASE WHEN o.warehouse_id = :warehouseId AND o.status != 'CANCELLED' THEN oi.quantity ELSE 0 END)
              AS outgoing,
            p.stock
            FROM stock_mutation AS s
            JOIN product AS p ON s.product_id = p.id
            JOIN order_item AS oi ON s.product_id = oi.product_id
            JOIN orders AS o ON oi.order_id = o.id
            WHERE
              (date_trunc('month', s.created_at) = date_trunc('month', CAST(:date AS timestamp))
            AND date_trunc('year', s.created_at) = date_trunc('year', CAST(:date AS timestamp)))
              OR
              (date_trunc('month', o.created_at) = date_trunc('month', CAST(:date AS timestamp))
            AND date_trunc('year', o.created_at) = date_trunc('year', CAST(:date AS timestamp)))
            GROUP BY p.id
            """, nativeQuery = true
    )
    List<RetrieveMonthlyStockSummary> getMonthlyStockSummary(@Param("warehouseId") Long warehouseId, @Param("date") Date date);
}
