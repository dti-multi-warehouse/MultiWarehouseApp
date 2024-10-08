package com.dti.multiwarehouse.stock.repository;

import com.dti.multiwarehouse.stock.dao.Stock;
import com.dti.multiwarehouse.stock.dao.key.StockCompositeKey;
import com.dti.multiwarehouse.stock.dto.response.RetrieveProductAndStockAvailabilityDto;
import com.dti.multiwarehouse.stock.dto.response.RetrieveStock;
import com.dti.multiwarehouse.stock.dto.response.RetrieveStockDetails;
import com.dti.multiwarehouse.stock.dto.response.RetrieveWarehouseAndStockAvailabilityDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findById(StockCompositeKey id);

    @Query(
            value = """
            SELECT
                p.id,
                p.name,
                COALESCE(i.image_urls, '') AS thumbnail,
                COALESCE(sm_in.incoming, 0) AS incoming,
                COALESCE(sm_out.outgoing, 0) + COALESCE(o_out.outgoing, 0) AS outgoing,
                warehouse_stock.stock
            FROM product AS p
            LEFT JOIN LATERAL (
                SELECT i.image_urls
                FROM product_image_urls i
                WHERE i.product_id = p.id
                LIMIT 1
            ) i ON true
            LEFT JOIN (
                SELECT product_id, SUM(quantity) AS incoming
                FROM stock_mutation
                WHERE
                    warehouse_to_id = :warehouseId
                    AND status = 'COMPLETED'
                    AND (date_trunc('month', created_at) = date_trunc('month', CAST(:date AS timestamp))
                    AND date_trunc('year', created_at) = date_trunc('year', CAST(:date AS timestamp)))
                GROUP BY product_id
            ) AS sm_in ON p.id = sm_in.product_id
            LEFT JOIN (
                SELECT product_id, SUM(quantity) AS outgoing
                FROM stock_mutation
                WHERE
                    warehouse_from_id = :warehouseId
                    AND status = 'COMPLETED'
                    AND (date_trunc('month', created_at) = date_trunc('month', CAST(:date AS timestamp))
                    AND date_trunc('year', created_at) = date_trunc('year', CAST(:date AS timestamp)))
                GROUP BY product_id
            ) AS sm_out ON p.id = sm_out.product_id
            LEFT JOIN (
                SELECT oi.product_id, SUM(oi.quantity) AS outgoing
                FROM order_item AS oi
                JOIN orders AS o ON oi.order_id = o.id
                WHERE
                    o.warehouse_id = :warehouseId AND o.status != 'CANCELLED'
                    AND (date_trunc('month', o.created_at) = date_trunc('month', CAST(:date AS timestamp))
                    AND date_trunc('year', o.created_at) = date_trunc('year', CAST(:date AS timestamp)))
                GROUP BY oi.product_id
                ) AS o_out ON p.id = o_out.product_id
            JOIN (
                SELECT
                    s.product_id,
                    s.stock
                FROM stock AS s
                WHERE s.warehouse_id = :warehouseId
                    ) AS warehouse_stock ON p.id = warehouse_stock.product_id
            WHERE p.name ILIKE :query
            ORDER BY p.id
            """, nativeQuery = true
    )
    Page<RetrieveStock> retrieveStock(@Param("warehouseId") Long warehouseId, @Param("date") LocalDate date, @Param("query") String query, Pageable pageable);

    @Query(
            value = """
            SELECT p.id, p.name, COALESCE(s.stock, 0) AS stock,
                   (SELECT i.image_urls
                    FROM product_image_urls i
                    WHERE i.product_id = p.id
                    LIMIT 1) AS thumbnail
            FROM product p
            LEFT JOIN stock s ON p.id = s.product_id AND s.warehouse_id = :warehouseId
            ORDER BY p.id;
            """, nativeQuery = true
    )
    List<RetrieveProductAndStockAvailabilityDto> retrieveProductAndStockAvailability(@Param("warehouseId") Long warehouseId);

    @Query(
            value = """
            SELECT s.stock, w.id AS warehouseId, w.name as warehouseName
            FROM stock AS s
            JOIN warehouse AS w ON s.warehouse_id = w.id
            WHERE w.id != :warehouseId AND s.product_id = :productId
            """, nativeQuery = true
    )
    List<RetrieveWarehouseAndStockAvailabilityDto> retrieveWarehouseAndStockAvailability(@Param("warehouseId") Long warehouseId, @Param("productId") Long productId);
    @Query(
            value = """
            SELECT created_at, quantity as quantity, 'order' AS source, order_data.id::varchar AS note
            FROM (
                SELECT o.id, oi.quantity, o.created_at
                FROM order_item AS oi
                JOIN orders AS o ON oi.order_id = o.id
                WHERE oi.product_id = :productId
                AND date_trunc('month', o.created_at) = date_trunc('month', CAST(:date AS timestamp))
                AND date_trunc('year', o.created_at) = date_trunc('year', CAST(:date AS timestamp))
            ) AS order_data
            
            UNION
            
            SELECT created_at, quantity, 'restock' AS source, '' AS note
            FROM stock_mutation
            WHERE product_id = :productId
            AND warehouse_to_id = :warehouseId AND warehouse_from_id is null
            AND date_trunc('month', created_at) = date_trunc('month', CAST(:date AS timestamp))
            AND date_trunc('year', created_at) = date_trunc('year', CAST(:date AS timestamp))
            
            UNION
            
            SELECT sm.created_at, sm.quantity, 'mutation_in' AS source, w.name AS note
            FROM stock_mutation AS sm
            JOIN warehouse AS w ON sm.warehouse_from_id = w.id
            WHERE sm.product_id = :productId
            AND sm.warehouse_to_id = :warehouseId
            AND sm.warehouse_from_id is not null
            AND date_trunc('month', sm.created_at) = date_trunc('month', CAST(:date AS timestamp))
            AND date_trunc('year', sm.created_at) = date_trunc('year', CAST(:date AS timestamp))
            
            UNION
            
            SELECT sm.created_at, sm.quantity as quantity, 'mutation_out' AS source, w.name AS note
            FROM stock_mutation AS sm
            JOIN warehouse AS w ON sm.warehouse_to_id = w.id
            WHERE sm.product_id = :productId
            AND sm.warehouse_from_id = :warehouseId
            AND date_trunc('month', sm.created_at) = date_trunc('month', CAST(:date AS timestamp))
            AND date_trunc('year', sm.created_at) = date_trunc('year', CAST(:date AS timestamp))
            
            ORDER BY created_at;
            """, nativeQuery = true
    )
    List<RetrieveStockDetails> retrieveStockDetails(@Param("warehouseId") Long warehouseId, @Param("productId") Long productId, @Param("date") LocalDate date);
}
