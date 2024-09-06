package com.dti.multiwarehouse.stock.repository;

import com.dti.multiwarehouse.stock.dao.StockMutation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

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
                SELECT COALESCE(SUM(CASE WHEN m.warehouseTo.id = :warehouseToId THEN m.quantity ELSE 0 END), 0) -
                       COALESCE(SUM(CASE WHEN m.warehouseFrom.id != :warehouseToId THEN m.quantity ELSE 0 END), 0)
                FROM StockMutation m
                WHERE m.product.id = :productId
                AND (m.warehouseTo.id = :warehouseToId OR m.warehouseFrom.id != :warehouseToId)
            )
            WHERE s.id.warehouse.id = :warehouseToId
            """
    )
    void calculateWarehouseStock(Long productId, Long warehouseToId);
}
