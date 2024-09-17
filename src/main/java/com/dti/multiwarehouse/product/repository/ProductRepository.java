package com.dti.multiwarehouse.product.repository;

import com.dti.multiwarehouse.product.dao.Product;
import com.dti.multiwarehouse.product.dto.response.SoldAndStockUpdateResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
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

    @Query(
            value = """
            select sold, stock
            from product
            where product.id = :productId
            """, nativeQuery = true
    )
    SoldAndStockUpdateResult getSoldAndStock(@Param("productId") Long productId);
}
