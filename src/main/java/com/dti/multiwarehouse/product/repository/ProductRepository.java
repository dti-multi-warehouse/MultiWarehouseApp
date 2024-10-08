package com.dti.multiwarehouse.product.repository;

import com.dti.multiwarehouse.product.dao.Product;
import com.dti.multiwarehouse.product.dto.response.SoldAndStockUpdateResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByOrderByIdAsc();

    @Query(
            value = """
            SELECT *
            FROM product
            WHERE name ILIKE CONCAT('%',:name,'%')
            """, nativeQuery = true
    )
    Page<Product> retrieveDashboardProducts(String name, Pageable pageable);

    @Modifying
    @Query(
            value = """
                update product
                set sold = (
                    select coalesce(sum(case when o.status != 'CANCELLED' then oi.quantity else 0 end), 0) as sold
                    from order_item as oi
                    join orders as o on oi.order_id = o.id
                    where oi.product_id = :productId
                )
                where product.id = :productId
            """, nativeQuery = true
    )
    void recalculateSold(@Param("productId") Long productId);

    @Modifying
    @Query(
            value = """
            UPDATE product
            SET stock = COALESCE((
                SELECT
                    COALESCE((
                        SELECT COALESCE(SUM(m.quantity), 0)
                        FROM stock_mutation AS m
                        WHERE m.product_id = :productId and m.warehouse_from_id is null
                    ), 0) - COALESCE((
                        SELECT COALESCE(SUM(CASE WHEN o.status != 'CANCELLED' THEN i.quantity ELSE 0 END), 0)
                        FROM order_item AS i
                        JOIN orders AS o ON i.order_id = o.id
                        WHERE i.product_id = :productId
                    ), 0)
            ), 0)
            WHERE product.id = :productId
            """, nativeQuery = true
    )
    void recalculateStock(@Param("productId") Long productId);

    @Query(
            value = """
            select sold, stock
            from product
            where product.id = :productId
            """, nativeQuery = true
    )
    SoldAndStockUpdateResult getSoldAndStock(@Param("productId") Long productId);
}
