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
            SELECT
                p.id,
                p.name,
                COALESCE(sm_in.incoming, 0) AS incoming,
                COALESCE(sm_out.outgoing, 0) + COALESCE(o_out.outgoing, 0) AS outgoing,
                p.stock
            FROM product AS p
            LEFT JOIN (
                SELECT product_id, SUM(quantity) AS incoming
                FROM stock_mutation
                WHERE
                    warehouse_to_id = :warehouseId
                    AND status = 'COMPLETED'
                    AND (date_trunc('month', created_at) = date_trunc('month', :date::timestamp)
                    AND date_trunc('year', created_at) = date_trunc('year', :date::timestamp))
                GROUP BY product_id
            ) AS sm_in ON p.id = sm_in.product_id
            LEFT JOIN (
                SELECT product_id, SUM(quantity) AS outgoing
                FROM stock_mutation
                WHERE
                    warehouse_from_id = :warehouseId
                    AND status = 'COMPLETED'
                    AND (date_trunc('month', created_at) = date_trunc('month', :date::timestamp)
                    AND date_trunc('year', created_at) = date_trunc('year', :date::timestamp))
                GROUP BY product_id
            ) AS sm_out ON p.id = sm_out.product_id
            LEFT JOIN (
                SELECT oi.product_id, SUM(oi.quantity) AS outgoing
                FROM order_item AS oi
                JOIN orders AS o ON oi.order_id = o.id
                WHERE
                    o.warehouse_id = :warehouseId AND o.status != 'CANCELLED'
                    AND (date_trunc('month', o.created_at) = date_trunc('month', :date::timestamp)
                    AND date_trunc('year', o.created_at) = date_trunc('year', :date::timestamp))
                GROUP BY oi.product_id
                ) AS o_out ON p.id = o_out.product_id
            """, nativeQuery = true
    )
    List<RetrieveMonthlyStockSummary> getMonthlyStockSummary(@Param("warehouseId") Long warehouseId, @Param("date") Date date);
}