package com.dti.multiwarehouse.stock.repository;

import com.dti.multiwarehouse.stock.dao.Stock;
import com.dti.multiwarehouse.stock.dao.key.StockCompositeKey;
import com.dti.multiwarehouse.stock.dto.response.RetrieveProductAndStockAvailabilityDto;
import com.dti.multiwarehouse.stock.dto.response.RetrieveStock;
import com.dti.multiwarehouse.stock.dto.response.RetrieveStockDetails;
import com.dti.multiwarehouse.stock.dto.response.RetrieveWarehouseAndStockAvailabilityDto;
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
                s.stock,
                (SELECT i.image_urls
                 FROM product_image_urls i
                 WHERE i.product_id = p.id
                 LIMIT 1) AS thumbnail,
                COALESCE(sm.incoming, 0) AS incoming,
                COALESCE(sm.outgoing, 0) AS outgoing
            FROM
                stock s
            JOIN
                product p ON p.id = s.product_id AND s.warehouse_id = :warehouseId
            LEFT JOIN
                (SELECT
                    p.id AS product_id,
                    SUM(CASE WHEN sm.warehouse_to_id = :warehouseId AND sm.status = 'COMPLETED' THEN sm.quantity ELSE 0 END) AS incoming,
                    SUM(CASE WHEN sm.warehouse_from_id = :warehouseId AND sm.status = 'COMPLETED' THEN sm.quantity ELSE 0 END) +
                    SUM(CASE WHEN o.warehouse_id = :warehouseId AND o.status != 'CANCELLED' THEN oi.quantity ELSE 0 END) AS outgoing
                FROM
                    product p
                LEFT JOIN
                    stock_mutation sm ON p.id = sm.product_id
                LEFT JOIN
                    order_item oi ON p.id = oi.product_id
                LEFT JOIN
                    orders o ON oi.order_id = o.id
                WHERE
                    (sm.created_at is null OR
                        (date_trunc('month', sm.created_at) = date_trunc('month', CAST(:date AS timestamp))
                        AND date_trunc('year', sm.created_at) = date_trunc('year', CAST(:date AS timestamp)))
                    )
                    AND
                    (o.created_at is null OR
                        (date_trunc('month', o.created_at) = date_trunc('month', CAST(:date AS timestamp))
                        AND date_trunc('year', o.created_at) = date_trunc('year', CAST(:date AS timestamp)))
                    )
                GROUP BY
                    p.id
                ) sm ON p.id = sm.product_id
            ORDER BY
                p.id;
            """, nativeQuery = true
    )
    List<RetrieveStock> retrieveStock(@Param("warehouseId") Long warehouseId, @Param("date") LocalDate date);

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
            SELECT s.stock, w.id AS warehouseId
            FROM stock AS s
            JOIN warehouse AS w ON s.warehouse_id = w.id
            WHERE w.id != :warehouseId AND s.product_id = :productId
            """, nativeQuery = true
    )
    List<RetrieveWarehouseAndStockAvailabilityDto> retrieveWarehouseAndStockAvailability(@Param("warehouseId") Long warehouseId, @Param("productId") Long productId);
    @Query(
            value = """
            SELECT created_at, -(quantity) as quantity, 'order' AS source, order_data.id AS note
            FROM (
                SELECT o.id, oi.quantity, o.created_at
                FROM order_item AS oi
                JOIN orders AS o ON oi.order_id = o.id
                WHERE oi.product_id = :productId
            ) AS order_data
            
            UNION
            
            SELECT created_at, quantity, 'restock' AS source, 0 AS note
            FROM stock_mutation
            WHERE product_id = :productId AND warehouse_to_id = :warehouseId AND warehouse_from_id is null
            
            UNION
            
            SELECT created_at, quantity, 'mutation_in' AS source, warehouse_from_id AS note
            FROM stock_mutation
            WHERE product_id = :productId AND warehouse_to_id = :warehouseId AND warehouse_from_id is not null
            
            UNION
            
            SELECT created_at, -(quantity) as quantity, 'mutation_out' AS source, warehouse_to_id AS note
            FROM stock_mutation
            WHERE product_id = :productId AND warehouse_from_id = :warehouseId
            
            ORDER BY created_at;
            """, nativeQuery = true
    )
    List<RetrieveStockDetails> retrieveStockDetails(@Param("warehouseId") Long warehouseId, @Param("productId") Long productId);
}
